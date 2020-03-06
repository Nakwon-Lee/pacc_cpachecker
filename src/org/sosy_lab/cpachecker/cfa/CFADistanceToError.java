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
package org.sosy_lab.cpachecker.cfa;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.sosy_lab.cpachecker.cfa.ast.AStatement;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallStatement;
import org.sosy_lab.cpachecker.cfa.model.AStatementEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdgeType;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.FunctionEntryNode;
import org.sosy_lab.cpachecker.cfa.model.c.CFunctionSummaryStatementEdge;
import org.sosy_lab.cpachecker.util.CFAUtils;

public class CFADistanceToError {

  private Set<CFAEdge> errorEdges = new HashSet<>();

  private Set<CFANode> visitedByForward;

  private Set<CFANode> targets = new HashSet<>();

  public void findErrorLocations(CFA pcfa, String errorindi, BlockScheme pScheme) {
    Deque<CFANode> nodestack = new ArrayDeque<>();
    Set<CFANode> visited = new HashSet<>();

    switch (pScheme) {
      case L:
        if (pcfa.getAllLoopHeads().isPresent()) {
          targets.addAll(pcfa.getAllLoopHeads().get());
        }
        break;
      case LF:
        if (pcfa.getAllLoopHeads().isPresent()) {
          targets.addAll(pcfa.getAllLoopHeads().get());
        }
        break;
      default:
        break;
    }

    CFANode pRootNode = pcfa.getMainFunction();

    // pNode must be the root node
    nodestack.push(pRootNode);

    while (!nodestack.isEmpty()) {
      CFANode currnode = nodestack.pop();
      if (visited.contains(currnode)) {
        continue;
      } else {
        visited.add(currnode);
      }

      switch (pScheme) {
        case L:
          break;
        case LF:
          if (currnode instanceof FunctionEntryNode
              || (currnode.getEnteringSummaryEdge() != null)) {
            targets.add(currnode);
          }
          break;
        default:
          break;
      }

      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();

      while (successors.hasNext()) {
        CFANode successor = successors.next();
        CFAEdge edge = currnode.getEdgeTo(successor);
        CFAEdgeType edgetype = edge.getEdgeType();

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
            if (errorfunname.compareTo(errorindi) == 0) {
              errorEdges.add(edge);
              // Iterator<CFANode> errorsuccs = CFAUtils.successorsOf(successor).iterator();
              // remove all leaving edges of error state
              /*
               * while (errorsuccs.hasNext()) { CFANode errorsucc = errorsuccs.next(); CFAEdge
               * errsuccedge = successor.getEdgeTo(errorsucc);
               * successor.removeLeavingEdge(errsuccedge); }
               */
            }
          }
        }
        nodestack.push(successor);
      }
    }
  }

  public void initiationDistToError(CFANode pRootNode) {
    Deque<CFANode> nodestack = new ArrayDeque<>();
    Set<CFANode> reached = new HashSet<>();

    // pNode must be the root node
    nodestack.push(pRootNode);

    while (!nodestack.isEmpty()) {

      CFANode currnode = nodestack.pop();

      if (reached.contains(currnode)) {
        continue;
      }

      currnode.initDistancetoerr(errorEdges.size());

      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();

      while (successors.hasNext()) {
        CFANode successor = successors.next();
        nodestack.push(successor);
      }

      reached.add(currnode);
    }
    visitedByForward = reached;
    System.out.println("errorEdges: " + errorEdges.toString());
  }

  public void calcDistanceToError2() {
    int i = 0;

    for (CFAEdge edge : errorEdges) {

      System.out.println("Start erroredge " + i);

      DistanceToErrComparator dtecomp = new DistanceToErrComparator();
      NavigableSet<NDPair> nodestack = new TreeSet<>(dtecomp);

      Set<CFANode> reached = new HashSet<>();

      // function name and the summary edge for the first attempt
      Map<String, PairND> function_F = new HashMap<>();
      // function name and the summary edge waiting that the distE of the function is computed
      Map<String, Set<PairND>> function_En = new HashMap<>();
      // function name and the distE value of the function
      Map<String, Integer> function_Ex = new HashMap<>();
      // a set of functions that are directly reached
      Set<String> direct_Ex = new HashSet<>();

      CFANode errnode = edge.getSuccessor();
      if (!errnode.getInitVisit()) {
        continue;
      }
      errnode.setDistancetoerr(i, 0);

      CFANode node = edge.getPredecessor();

      assert node
          .getNumLeavingEdges() == 1 : "predecessor of error edge should have only one leaving edge";

      reached.add(node);
      reached.add(errnode);

      NDPair tempnd = new NDPair(node,0,true);

      nodestack.add(tempnd);

      tempnd.getNode().setDistancetoerr(i, tempnd.getDist());

      while (!nodestack.isEmpty()) {
        NDPair currndpair = nodestack.pollFirst();
        CFANode currnode = currndpair.getNode();

        Iterator<CFANode> predecessors = CFAUtils.predecessorsOf(currnode).iterator();

        while (predecessors.hasNext()) {
          CFANode predecessor = predecessors.next();
          CFAEdge preedge = predecessor.getEdgeTo(currnode);
          CFAEdgeType preedgetype = preedge.getEdgeType();

          if (!visitedByForward.contains(predecessor)) {
            continue;
          }

          boolean isTarget = false;

          if (targets.contains(predecessor)) {
            isTarget = true;
          }

          //action for each edge type

          if (preedgetype == CFAEdgeType.StatementEdge) {

            if (preedge instanceof CFunctionSummaryStatementEdge) {
              continue;
            }

            if (reached.contains(predecessor)) {
              continue;
            }

            NDPair tempcurrnd;
            if (isTarget) {
              tempcurrnd =
                  new NDPair(predecessor, currndpair.getDist() + 1, currndpair.getDirect());
            } else {
              tempcurrnd =
                  new NDPair(predecessor, currndpair.getDist(), currndpair.getDirect());
            }
            tempcurrnd.getNode().setDistancetoerr(i, tempcurrnd.getDist());
            reached.add(tempcurrnd.getNode());
            nodestack.add(tempcurrnd);

          } else if (preedgetype == CFAEdgeType.FunctionReturnEdge) {

            String funcname = predecessor.getFunctionName();
            CFANode callnode = currnode.getEnteringSummaryEdge().getPredecessor();

            boolean isTargetSub = false;

            // is the call node a target?
            if (targets.contains(callnode)) {
              isTargetSub = true;
            }

            if (function_En.containsKey(funcname)) {// somebody is traversing (or traversed) the function

              if (reached.contains(callnode)) {
                continue;
              }

              if (function_Ex.containsKey(funcname)) {// traversed! the distE value of the callee
                                                      // function is already computed.
                // compute the distE value of the callnode using the distE value of the callee
                // function
                NDPair tempndpair;
                if (isTargetSub) {
                  tempndpair =
                      new NDPair(
                          callnode,
                          currndpair.getDist() + function_Ex.get(funcname) + 1,
                          currndpair.getDirect());
                } else {
                  tempndpair =
                      new NDPair(
                          callnode,
                          currndpair.getDist() + function_Ex.get(funcname),
                          currndpair.getDirect());
                }
                tempndpair.getNode().setDistancetoerr(i, tempndpair.getDist());
                nodestack.add(tempndpair);
                reached.add(tempndpair.getNode());

              } else {// is traversing!
                NDPair tempndpair =
                    new NDPair(callnode, currndpair.getDist(), currndpair.getDirect());

                PairND temppairnd = new PairND(tempndpair, currndpair);

                function_En.get(funcname).add(temppairnd);
              }

            } else {// I'm the first who traverse the function

              if (reached.contains(predecessor)) {
                continue;
              }

              NDPair tempndpair =
                  new NDPair(callnode, currndpair.getDist(), currndpair.getDirect());
              PairND temppairnd = new PairND(tempndpair, currndpair);
              function_F.put(funcname, temppairnd);
              function_En.put(funcname, new HashSet<PairND>());

              NDPair temp2ndpair;
              if (isTarget) {
                temp2ndpair = new NDPair(predecessor, currndpair.getDist() + 1, false);
              } else {
                temp2ndpair = new NDPair(predecessor, currndpair.getDist(), false);
              }
              temp2ndpair.getNode().setDistancetoerr(i, temp2ndpair.getDist());
              nodestack.add(temp2ndpair);
              reached.add(temp2ndpair.getNode());
            }

          }else if(preedgetype == CFAEdgeType.FunctionCallEdge) {

            String funcname = currndpair.getNode().getFunctionName();

            if (currndpair.getDirect()) {
              //the function is reached directly
              NDPair tempndpair;
              if(isTarget) {
                tempndpair = new NDPair(predecessor,currndpair.getDist() + 1,currndpair.getDirect());
              }else {
                tempndpair = new NDPair(predecessor,currndpair.getDist(),currndpair.getDirect());
              }
              tempndpair.getNode().setDistancetoerr(i, tempndpair.getDist());
              reached.add(tempndpair.getNode());
              nodestack.add(tempndpair);

              direct_Ex.add(funcname);
            }else {
              //the function is reached from the return transition
              NDPair callnodend = function_F.get(funcname).getPred();
              if(predecessor.compareTo(callnodend.getNode())!=0) {
                continue;
              }

              function_Ex.put(funcname, currndpair.getDist() - function_F.get(funcname).getSucc().getDist());

              if(isTarget) {
                callnodend.setDist(currndpair.getDist() + 1);
              }else {
                callnodend.setDist(currndpair.getDist());
              }
              callnodend.getNode().setDistancetoerr(i, callnodend.getDist());
              reached.add(callnodend.getNode());
              nodestack.add(callnodend);
            }

          } else { // other edges

            if (reached.contains(predecessor)) {
              continue;
            }

            NDPair tempcurrnd;
            if (isTarget) {
              tempcurrnd =
                  new NDPair(predecessor, currndpair.getDist() + 1, currndpair.getDirect());
            } else {
              tempcurrnd = new NDPair(predecessor, currndpair.getDist(), currndpair.getDirect());
            }
            tempcurrnd.getNode().setDistancetoerr(i, tempcurrnd.getDist());
            reached.add(tempcurrnd.getNode());
            nodestack.add(tempcurrnd);
          }
          //end action for an edge
        }
        // end actions for predecessors
        // handle waited function returns in here
        for (String fname : function_En.keySet()) {
          // if the function is visited directly, it is unnecessary to compute the distE of
          // callnode
          if (direct_Ex.contains(fname)) {
            // direct_Ex found! throw all nodes!
            function_En.get(fname).clear();
          }
          // the distE of the function is computed
          if (function_Ex.containsKey(fname)) {
            // function_Ex found! add all nodes!
            for (PairND apair : function_En.get(fname)) {
              NDPair callndpair = apair.getPred();
              NDPair succnd = apair.getSucc();
              if (targets.contains(callndpair.getNode())) {
                callndpair.setDist(succnd.getDist() + function_Ex.get(fname) + 1);
              } else {
                callndpair.setDist(succnd.getDist() + function_Ex.get(fname));
              }
              callndpair.getNode().setDistancetoerr(i, callndpair.getDist());
              nodestack.add(callndpair);
              reached.add(callndpair.getNode());
            }
            function_En.get(fname).clear();
          }
        }
      }
      i = i + 1;
    }
  }

  public String toStringDistErr(CFANode prootnode) {
    String retstr = "";
    Deque<CFANode> nodestack = new ArrayDeque<>();
    Set<CFANode> visited = new HashSet<>();

    // pNode must be the root node
    nodestack.push(prootnode);

    while (!nodestack.isEmpty()) {
      CFANode currnode = nodestack.pop();
      if (visited.contains(currnode)) {
        continue;
      } else {
        visited.add(currnode);
      }

      retstr = retstr.concat("N" + currnode.getNodeNumber() + ":");
      List<Integer> key = currnode.getDistancetoerrList();
      for (int i = 0; i < key.size(); i++) {
        retstr = retstr.concat(currnode.getDistancetoerr(i) + ",");
      }
      retstr = retstr.concat(System.lineSeparator());

      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();

      while (successors.hasNext()) {
        CFANode successor = successors.next();
        nodestack.push(successor);
      }
    }
    return retstr;
  }

  private static class DistanceToErrComparator implements Comparator<NDPair> {

    @Override
    public int compare(NDPair pnd0, NDPair pnd1) {
      Integer pint0 = pnd0.getDist();
      Integer pint1 = pnd1.getDist();
      CFANode pnode0 = pnd0.getNode();
      CFANode pnode1 = pnd1.getNode();
      int ret = Integer.compare(pint0, pint1);
      if (ret == 0) {
        ret = pnode0.compareTo(pnode1);
      }
      return ret;
    }
  }

  private static class NDPair {
    private CFANode node;
    private Integer dist;
    private boolean isDirect;

    public NDPair(CFANode pNode, Integer pDist, boolean pDirect) {
      node = pNode;
      dist = pDist;
      isDirect = pDirect;
    }

    public void setNode(CFANode pNode) {
      node = pNode;
    }

    public CFANode getNode() {
      return node;
    }

    public void setDist(Integer pDist) {
      dist = pDist;
    }

    public Integer getDist() {
      return dist;
    }

    public void setDirect(boolean pDirect) {
      isDirect = pDirect;
    }

    public boolean getDirect() {
      return isDirect;
    }
  }

  private static class PairND {
    NDPair pred;
    NDPair succ;

    public PairND(NDPair pPred, NDPair pSucc) {
      pred = pPred;
      succ = pSucc;
    }

    public void setPred(NDPair pPred) {
      pred = pPred;
    }

    public NDPair getPred() {
      return pred;
    }

    public void setSucc(NDPair pSucc) {
      succ = pSucc;
    }

    public NDPair getSucc() {
      return succ;
    }
  }
}
