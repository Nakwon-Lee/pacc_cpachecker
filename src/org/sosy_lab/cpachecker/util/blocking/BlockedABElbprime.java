/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.sosy_lab.cpachecker.util.blocking;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.ast.AStatement;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallStatement;
import org.sosy_lab.cpachecker.cfa.model.AStatementEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdgeType;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.c.CFunctionSummaryStatementEdge;
import org.sosy_lab.cpachecker.core.interfaces.Pair;
import org.sosy_lab.cpachecker.util.CFAUtils;
import org.sosy_lab.cpachecker.util.blocking.interfaces.BlockComputer;

@Options(prefix = "blockABElbprime")
public class BlockedABElbprime implements BlockComputer {

  @Option(
    secure = true,
    description = "Allow reduction of loop heads; calculate abstractions always at loop heads?")
  private boolean allowReduceLoopHeads = true;

  @Option(secure = true, name = "errorloc", description = "This option is the name of error loc.")
  private String errorlocindi;

  public BlockedABElbprime(Configuration pConfig)
      throws InvalidConfigurationException {
    pConfig.inject(this);
  }

  @Override
  public ImmutableSet<CFANode> computeAbstractionNodes(CFA pCfa) {
    Set<CFANode> endlocs = findEndLocations(pCfa.getMainFunction());
    System.out.println("endLocs: " + endlocs);
    Set<CFANode> abslocs = calcABEbpAbstractionNodes(endlocs);
    System.out.println("absLocs: " + abslocs);
    return ImmutableSet.copyOf(abslocs);
  }

  public Set<CFANode> findEndLocations(CFANode pRootNode) {
    Set<CFANode> endlocs = new HashSet<>();
    Deque<CFANode> nodestack = new ArrayDeque<>();
    Set<CFANode> visited = new HashSet<>();

    nodestack.push(pRootNode);

    while (!nodestack.isEmpty()) {
      CFANode currnode = nodestack.pop();
      if (visited.contains(currnode)) {
        continue;
      } else {
        visited.add(currnode);
      }

      boolean hassuccessor = false;
      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();
      while (successors.hasNext()) {
        hassuccessor = true;
        CFANode successor = successors.next();
        CFAEdge edge = currnode.getEdgeTo(successor);
        CFAEdgeType edgetype = edge.getEdgeType();
        // assuming that CFunctionCallStatemnet is the only case of error function call
        if (edgetype == CFAEdgeType.StatementEdge) {

          if (edge instanceof CFunctionSummaryStatementEdge) {
            continue;
          }

          AStatementEdge stmtedge = (AStatementEdge) edge;
          AStatement stmt = stmtedge.getStatement();
          if (stmt instanceof CFunctionCallStatement) {
            CFunctionCallStatement cfcstmt = (CFunctionCallStatement) stmt;
            String errorfunname =
                cfcstmt.getFunctionCallExpression()
                    .getFunctionNameExpression()
                    .toQualifiedASTString();
            if (errorfunname.compareTo(errorlocindi) == 0) {
              successor.setIsEncoded(true);
              endlocs.add(successor);
            }
          }
        }
        nodestack.push(successor);
      }
      if (!hassuccessor) {
        endlocs.add(currnode);
      }
    }

    return endlocs;
  }

  private Set<CFANode> calcABEbpAbstractionNodes(Set<CFANode> pEndlocs) {

    Deque<NAPair> nodestack = new ArrayDeque<>();
    Map<String, Set<NAPair>> function_En = new HashMap<>();
    Map<String, TFPair> function_LocE = new HashMap<>();
    Map<String, TFPair> function_Ex = new HashMap<>();
    Map<String, TFPair> direct_Ex = new HashMap<>();
    Map<CFANode, NAPair> branchmap = new HashMap<>();
    Set<CFANode> visited = new HashSet<>();

    Set<CFANode> absNodes = new HashSet<>();

    for (CFANode anode : pEndlocs) {
      NAPair napair;
      if (anode.getIsEncoded()) {
        // absNodes.add(anode);
        napair = new NAPair(new NCPair(anode, null), new TFPair(true, true));
      } else {
        napair = new NAPair(new NCPair(anode, null), new TFPair(false, false));
      }

      nodestack.add(napair);
      visited.add(anode);
    }

    while (!nodestack.isEmpty()) {

      /*
       * for (NAPair apair : nodestack) { System.out.print("N" +
       * apair.getLeft().getLeft().getNodeNumber() + ", "); } System.out.println();
       */

      NAPair currnapair = nodestack.pollFirst();
      NCPair currncpair = currnapair.getLeft();
      TFPair currtfpair = currnapair.getRight();
      CFANode currnode = currncpair.getLeft();

      // System.out.println("Pick: N" + currnode.getNodeNumber());

      TFPair intermediatepair;

      Iterator<CFANode> predecessors = CFAUtils.predecessorsOf(currnode).iterator();

      boolean multiple = false;
      if (currnode.getNumEnteringEdges() > 1) {
        if (!currnode.isLoopStart()) {
          multiple = true;
        }
      }

      if (multiple) {
        intermediatepair = new TFPair(false, false);
      } else {
        intermediatepair = new TFPair(currtfpair.getLeft(), currtfpair.getRight());
      }

      while (predecessors.hasNext()) {
        CFANode predecessor = predecessors.next();
        CFAEdge preedge = predecessor.getEdgeTo(currnode);
        CFAEdgeType preedgetype = preedge.getEdgeType();

        if (preedgetype == CFAEdgeType.StatementEdge) {

          if (preedge instanceof CFunctionSummaryStatementEdge) {
            continue;
          }

          if (visited.contains(predecessor)) {
            continue;
          }

          nodestack.add(
              new NAPair(
                  new NCPair(predecessor, currncpair.getRight()),
                  intermediatepair));
          visited.add(predecessor);

        } else if (preedgetype == CFAEdgeType.AssumeEdge) {

          if (visited.contains(predecessor)) {
            continue;
          }

          if (!predecessor.isLoopStart() && predecessor.getNumLeavingEdges() > 1) {
            // branch!
            assert predecessor
                .getNumLeavingEdges() == 2 : "the branch has more than two leaving edges";

            if (branchmap.containsKey(predecessor)) {
              // branch, already found (need result of another assume edge)
              NAPair prednapair = branchmap.get(predecessor);
              TFPair predtfpair = prednapair.getRight();
              NCPair predncpair = prednapair.getLeft();

              boolean tleft = predtfpair.getLeft() && intermediatepair.getLeft();
              boolean tright = predtfpair.getRight() && intermediatepair.getRight();
              TFPair newtfpair = new TFPair(tleft, tright);
              NCPair newncpair;
              if (predncpair.getRight() != null) {
                newncpair = new NCPair(predecessor, predncpair.getRight());
              } else if (currncpair.getRight() != null) {
                newncpair = new NCPair(predecessor, currncpair.getRight());
              } else {
                newncpair = new NCPair(predecessor, null);
              }

              if (newtfpair.getLeft() || newtfpair.getRight()) {
                absNodes.add(predecessor);
              }

              nodestack.add(new NAPair(newncpair, newtfpair));
              visited.add(predecessor);
              branchmap.remove(predecessor);
            } else {
              // branch, found first

              branchmap.put(
                  predecessor,
                  new NAPair(
                      new NCPair(predecessor, currncpair.getRight()),
                      new TFPair(intermediatepair.getLeft(), intermediatepair.getRight())));
            }
          } else if (predecessor.isLoopStart()) {
            // loop head!
            if (branchmap.containsKey(predecessor)) {
              // loop head, already found
              NAPair prednapair = branchmap.get(predecessor);
              NCPair predncpair = prednapair.getLeft();
              NCPair newncpair;

              if (predncpair.getRight() != null) {
                newncpair = new NCPair(predecessor, predncpair.getRight());
              } else if (currncpair.getRight() != null) {
                newncpair = new NCPair(predecessor, currncpair.getRight());
              } else {
                newncpair = new NCPair(predecessor, null);
              }

              // absNodes.add(predecessor);
              nodestack.add(
                  new NAPair(newncpair, new TFPair(true, true)));
              visited.add(predecessor);
              branchmap.remove(predecessor);

            } else {
              // loop head, found first
              branchmap.put(
                  predecessor,
                  new NAPair(
                      new NCPair(predecessor, currncpair.getRight()),
                      new TFPair(intermediatepair.getLeft(), intermediatepair.getRight())));
              // add loop predecessor to nodestack
              Iterator<CFANode> looppreds = CFAUtils.predecessorsOf(predecessor).iterator();

              while (looppreds.hasNext()) {
                CFANode looppred = looppreds.next();
                if (looppred.getNodeNumber() > predecessor.getNodeNumber()) {
                  nodestack.add(
                      new NAPair(
                          new NCPair(looppred, currncpair.getRight()),
                          new TFPair(true, true)));
                  visited.add(looppred);
                }
              }
            }
          } else {
            // single edge assuming
            nodestack.add(
                new NAPair(
                    new NCPair(predecessor, currncpair.getRight()),
                    new TFPair(intermediatepair.getLeft(), intermediatepair.getRight())));
            visited.add(predecessor);
          }
        } else if (preedgetype == CFAEdgeType.FunctionReturnEdge) {
          String funcname = predecessor.getFunctionName();
          CFANode caller = currnode.getEnteringSummaryEdge().getPredecessor();

          if (function_En.containsKey(funcname)) {
            // somebody is traversing (or traversed) the function
            if (visited.contains(caller)) {
              continue;
            }

            if (function_Ex.containsKey(funcname)) {
              // traversed! caller is the predecessor

              boolean tfleft =
                  intermediatepair.getLeft()
                      ? function_Ex.get(funcname).getLeft()
                      : function_Ex.get(funcname).getRight();
              boolean tfright =
                  intermediatepair.getRight()
                      ? function_Ex.get(funcname).getLeft()
                      : function_Ex.get(funcname).getRight();

              nodestack.add(
                  new NAPair(
                      new NCPair(caller, currncpair.getRight()),
                      new TFPair(tfleft, tfright)));
              visited.add(caller);
            } else if (direct_Ex.containsKey(funcname)) {// traversed! directly
              // caller is the predecessor and might be visited
            } else {// is traversing!
              // caller is the predecessor but not yet computed
              function_En.get(funcname)
                  .add(new NAPair(new NCPair(caller, currncpair.getRight()), intermediatepair));
            }

          } else {// I'm the first who traverse the function
            function_En.put(funcname, new HashSet<NAPair>());
            function_LocE.put(funcname, intermediatepair);
            nodestack.add(
                new NAPair(
                    new NCPair(predecessor, new NCPair(caller, currncpair.getRight())),
                    new TFPair(true, false)));
            visited.add(predecessor);
          }

        } else if (preedgetype == CFAEdgeType.FunctionCallEdge) {

          if (visited.contains(predecessor)) {
            continue;
          }

          if (currncpair.getRight() != null) {// has caller function
            if (predecessor.compareTo(currncpair.getRight().getLeft()) != 0) {
              // invalid callEdge
              continue;
            }

            TFPair newtfpair = new TFPair(intermediatepair.getLeft(), intermediatepair.getRight());
            function_Ex.put(currnode.getFunctionName(), newtfpair);
            boolean tfleft =
                function_LocE.get(currnode.getFunctionName()).getLeft()
                    ? newtfpair.getLeft()
                    : newtfpair.getRight();
            boolean tfright =
                function_LocE.get(currnode.getFunctionName()).getRight()
                    ? newtfpair.getLeft()
                    : newtfpair.getRight();
            nodestack.add(
                new NAPair(
                    new NCPair(predecessor, currncpair.getRight().getRight()),
                    new TFPair(tfleft, tfright)));
            visited.add(predecessor);
          } else {
            // has no caller function
            nodestack.add(
                new NAPair(
                    new NCPair(predecessor, currncpair.getRight()),
                    new TFPair(intermediatepair.getLeft(), intermediatepair.getRight())));
            if (!direct_Ex.containsKey(currnode.getFunctionName())) {
              direct_Ex.put(
                  currnode.getFunctionName(),
                  new TFPair(intermediatepair.getLeft(), intermediatepair.getRight()));
            }
          }

        } else {
          if (visited.contains(predecessor)) {
            continue;
          }

          nodestack.add(
              new NAPair(
                  new NCPair(predecessor, currncpair.getRight()),
                  new TFPair(intermediatepair.getLeft(), intermediatepair.getRight())));
          visited.add(predecessor);
        }
        // end action for an edge
      }
      // end actions for predecessors
      // handle waited function returns in here
      for (Entry<String, Set<NAPair>> entry : function_En.entrySet()) {
        if (direct_Ex.containsKey(entry.getKey())) {
          // direct_Ex found! throw all nodes!
          entry.getValue().clear();
        }

        if (function_Ex.containsKey(entry.getKey())) {
          // function_Ex found! add all nodes!
          for (NAPair apair : entry.getValue()) {
            boolean tfleft =
                apair.getRight().getLeft()
                    ? function_Ex.get(entry.getKey()).getLeft()
                    : function_Ex.get(entry.getKey()).getRight();
            boolean tfright =
                apair.getRight().getRight()
                    ? function_Ex.get(entry.getKey()).getLeft()
                    : function_Ex.get(entry.getKey()).getRight();

            apair.setRight(new TFPair(tfleft, tfright));
            nodestack.add(apair);
            visited.add(apair.getLeft().getLeft());
          }

          entry.getValue().clear();
        }
      }

    }

    return absNodes;
  }

  private static class TFPair implements Pair<Boolean, Boolean> {

    private Boolean truecase = null;
    private Boolean falsecase = null;

    public TFPair(Boolean pTrue, Boolean pFalse) {
      truecase = pTrue;
      falsecase = pFalse;
    }

    @Override
    public void setLeft(Boolean pPt1) {
      truecase = pPt1;
    }

    @Override
    public Boolean getLeft() {
      return truecase;
    }

    @Override
    public void setRight(Boolean pPt2) {
      falsecase = pPt2;
    }

    @Override
    public Boolean getRight() {
      return falsecase;
    }

  }

  private static class NCPair implements Pair<CFANode, NCPair> {
    private CFANode node;
    private NCPair caller;

    public NCPair(CFANode me, NCPair pCaller) {
      node = me;
      caller = pCaller;
    }

    @Override
    public void setLeft(CFANode pPt1) {
      node = pPt1;
    }

    @Override
    public CFANode getLeft() {
      return node;
    }

    @Override
    public void setRight(NCPair pPt2) {
      caller = pPt2;
    }

    @Override
    public NCPair getRight() {
      return caller;
    }
  }

  private static class NAPair implements Pair<NCPair, TFPair> {

    NCPair node;
    TFPair enco;

    public NAPair(NCPair pNode, TFPair pEnco) {
      node = pNode;
      enco = pEnco;
    }

    @Override
    public void setLeft(NCPair pPt1) {
      node = pPt1;
    }

    @Override
    public NCPair getLeft() {
      return node;
    }

    @Override
    public void setRight(TFPair pPt2) {
      enco = pPt2;
    }

    @Override
    public TFPair getRight() {
      return enco;
    }

  }

}
