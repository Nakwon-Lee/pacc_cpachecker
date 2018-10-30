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
import java.util.Map.Entry;
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
import org.sosy_lab.cpachecker.core.interfaces.Pair;
import org.sosy_lab.cpachecker.util.CFAUtils;

public class CFADistanceToError {

  private Set<CFAEdge> errorEdges = new HashSet<>();

  private Set<CFANode> visitedByForward;

  public void findErrorLocations(CFANode pRootNode, String errorindi) {
    Deque<CFANode> nodestack = new ArrayDeque<>();
    Set<CFANode> visited = new HashSet<>();

    // pNode must be the root node
    nodestack.push(pRootNode);

    while (!nodestack.isEmpty()) {
      CFANode currnode = nodestack.pop();
      if (visited.contains(currnode)) {
        continue;
      } else {
        visited.add(currnode);
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
    Deque<NCPair> nodestack = new ArrayDeque<>();
    Set<CFANode> visited = new HashSet<>();
    Set<String> functions_En = new HashSet<>();
    Set<String> functions_Ex = new HashSet<>();

    // pNode must be the root node
    nodestack.push(new NCPair(pRootNode, null));

    while (!nodestack.isEmpty()) {
      /*
       * for (NCPair apair : nodestack) { System.out.print("N" + apair.getLeft().getNodeNumber() +
       * ", "); } System.out.println();
       */
      NCPair currpair = nodestack.pop();
      CFANode currnode = currpair.getLeft();
      // System.out.println("Pick: N" + currnode.getNodeNumber());

      boolean key = false;

      if (currnode instanceof FunctionEntryNode) {
        FunctionEntryNode fenode = (FunctionEntryNode) currnode;
        String funcname = fenode.getFunctionName();
        if (funcname.compareTo("main") != 0) {
          if (functions_En.contains(funcname)) {
            key = true;
            if (functions_Ex.contains(funcname)) {
              // already visited function with returnable
              nodestack.push(currpair.getRight());
            }
          } else {
            functions_En.add(funcname);
          }
        }
      }

      if (visited.contains(currnode)) {
        continue;
      } else {
        assert !key : "if a functionentrynode is in functions_En, it should be visited";
        visited.add(currnode);
      }

      currnode.initDistancetoerr(errorEdges.size());

      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();

      while (successors.hasNext()) {
        CFANode successor = successors.next();
        CFAEdge edge = currnode.getEdgeTo(successor);
        CFAEdgeType edgetype = edge.getEdgeType();
        NCPair succpair = null;
        // assuming that CFunctionCallStatemnet is the only case of error function call
        if (edgetype == CFAEdgeType.StatementEdge) {

          if (edge instanceof CFunctionSummaryStatementEdge) {
            continue;
          }

        } else if (edgetype == CFAEdgeType.FunctionCallEdge) {
          CFANode returnnode = currnode.getLeavingSummaryEdge().getSuccessor();
          succpair = new NCPair(returnnode, currpair.getRight());
        } else if (edgetype == CFAEdgeType.FunctionReturnEdge) {
          if (successor.compareTo(currpair.getRight().getLeft()) != 0) {
            // invalid return edge
            continue;
          }
          succpair = currpair.getRight();
          functions_Ex.add(currnode.getFunctionName());
        }
        if (succpair == null) {
          succpair = currpair.getRight();
        }
        nodestack.push(new NCPair(successor, succpair));
      }
    }
    visitedByForward = visited;
    System.out.println("errorEdges: " + errorEdges.toString());
  }

  public void calcDistanceToError() {

    int i = 0;

    for (CFAEdge edge : errorEdges) {

      System.out.println("Start erroredge " + i);

      DistanceToErrComparator dtecomp = new DistanceToErrComparator(i);

      Deque<NavigableSet<NDPair>> funcstack = new ArrayDeque<>();
      NavigableSet<NDPair> tempstack = new TreeSet<>(dtecomp);
      funcstack.push(tempstack);

      Deque<Set<CFANode>> visitstack = new ArrayDeque<>();
      Set<CFANode> tempvisited = new HashSet<>();
      visitstack.push(tempvisited);

      Deque<Integer> compdiststack = new ArrayDeque<>();
      compdiststack.push(0);

      HashMap<String, Integer> function_Dist = new HashMap<>();

      CFANode errnode = edge.getSuccessor();
      errnode.setDistancetoerr(i, 0);

      CFANode node = edge.getPredecessor();

      assert node
          .getNumLeavingEdges() == 1 : "predecessor of error edge should have only one leaving edge";
      node.setDistancetoerr(i, 1);

      // visited nodes set for current call stack
      Set<CFANode> visited = tempvisited;
      visited.add(node);
      visited.add(errnode);

      // waiting nodes stack for current call stack
      NavigableSet<NDPair> nodestack = tempstack;
      nodestack.add(new NDPair(new NCPair(node, null), 0));

      // input comparative distance for current call stack
      Integer currcompvisit = compdiststack.peek();

      while (!funcstack.isEmpty()) {

        nodestack = funcstack.peek();
        visited = visitstack.peek();
        currcompvisit = compdiststack.peek();
        String temp = "";
        for (NDPair nddd : nodestack) {
          CFANode dd = nddd.getLeft().getLeft();
          temp = temp.concat("N" + dd.getNodeNumber() + ":" + dd.getDistancetoerr(i) + " ");
        }
        System.out.println(temp + nodestack.first().getLeft().getLeft().getFunctionName());

        NDPair currndpair = nodestack.pollFirst();
        NCPair currncpair = currndpair.getLeft();
        CFANode currnode = currncpair.getLeft();

        System.out
            .println("Pick N" + currnode.getNodeNumber() + ":" + currnode.getDistancetoerr(i));

        Iterator<CFANode> predecessors = CFAUtils.predecessorsOf(currnode).iterator();

        int predcount = 0;
        boolean freturn = false;
        boolean fcall = false;
        while (predecessors.hasNext()) {
          CFANode predecessor = predecessors.next();

          CFAEdge preedge = predecessor.getEdgeTo(currnode);
          CFAEdgeType preedgetype = preedge.getEdgeType();

          if (!visitedByForward.contains(predecessor)) {
            continue;
          }

          if (preedgetype == CFAEdgeType.AssumeEdge) {

            if (visited.contains(predecessor)) {
              continue;
            }

            if (predecessor.getDistancetoerr(i) > currnode.getDistancetoerr(i) + 1) {
              predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i) + 1);
            }

            Integer predndint = currndpair.getRight() + 1;

            nodestack.add(new NDPair(new NCPair(predecessor, currncpair.getRight()), predndint));
            visited.add(predecessor);

          } else if (preedgetype == CFAEdgeType.StatementEdge) {

            if (visited.contains(predecessor)) {
              continue;
            }

            if (preedge instanceof CFunctionSummaryStatementEdge) {
              continue;
            }

            if (predecessor.getDistancetoerr(i) > currnode.getDistancetoerr(i)) {
              predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
            }
            nodestack.add(
                new NDPair(new NCPair(predecessor, currncpair.getRight()), currndpair.getRight()));
            visited.add(predecessor);

          } else if (preedgetype == CFAEdgeType.FunctionReturnEdge) {

            freturn = true;

            String funcname = predecessor.getFunctionName();
            CFANode caller = currnode.getEnteringSummaryEdge().getPredecessor();

            if (function_Dist.containsKey(funcname)) { // do not enter the function

              assert function_Dist.get(
                  funcname) != null : "we assume that a deeper stacked function should be processed first";

              if(visited.contains(caller)) {
                continue;
              }

              NDPair tempndpair =
                  new NDPair(new NCPair(caller, currncpair.getRight()), currndpair.getRight());
              if (caller.getDistancetoerr(i) > currnode.getDistancetoerr(i)
                  + function_Dist.get(funcname)) {
                caller.setDistancetoerr(
                    i,
                    currnode.getDistancetoerr(i) + function_Dist.get(funcname));
              }
              nodestack.add(tempndpair);
              visited.add(caller);

            } else { // enter the function (new function stack, and new visited set)

              NavigableSet<NDPair> tempset = new TreeSet<>(dtecomp);
              funcstack.push(tempset);

              Set<CFANode> tempnodeset = new HashSet<>();
              visitstack.push(tempnodeset);

              compdiststack.push(currndpair.getRight());

              function_Dist.put(predecessor.getFunctionName(), null);

              if (predecessor.getDistancetoerr(i) > currnode.getDistancetoerr(i)) {
                predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
              }
              NCPair predncpair =
                  new NCPair(predecessor, new NCPair(caller, currncpair.getRight()));
              tempset.add(new NDPair(predncpair, 0));
              tempnodeset.add(predecessor);
            }

          } else if (preedgetype == CFAEdgeType.FunctionCallEdge) {

            if (currncpair.getRight() != null) {// has caller function
              if (predecessor.compareTo(currncpair.getRight().getLeft()) != 0) {
                // invalid callEdge
                continue;
              }
            }

            if (currncpair.getRight() != null) {// has caller function

              fcall = true;

              NavigableSet<NDPair> temptop = funcstack.pop();
              Set<CFANode> tempvitop = visitstack.pop();

              if (visitstack.peek().contains(predecessor)) {
                funcstack.push(temptop);
                visitstack.push(tempvitop);
                continue;
              }

              assert function_Dist.containsKey(
                  currnode
                      .getFunctionName()) : "function_dist must have the key for callee function";
              function_Dist.put(currnode.getFunctionName(), currndpair.getRight());

              if (predecessor.getDistancetoerr(i) > currnode.getDistancetoerr(i)) {
                predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
              }

              funcstack.peek().add(
                  new NDPair(
                      new NCPair(predecessor, currncpair.getRight().getRight()),
                      currcompvisit + currndpair.getRight()));
              visitstack.peek().add(predecessor);

              funcstack.push(temptop);
              visitstack.push(tempvitop);

            }else {// has no caller function

              if (visited.contains(predecessor)) {
                continue;
              }

              if (predecessor.getDistancetoerr(i) > currnode.getDistancetoerr(i)) {
                predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
              }
              nodestack.add(
                  new NDPair(
                      new NCPair(predecessor, currncpair.getRight()),
                      currndpair.getRight()));
              visited.add(predecessor);
            }

          } else {

            if (visited.contains(predecessor)) {
              continue;
            }

            if (predecessor.getDistancetoerr(i) > currnode.getDistancetoerr(i)) {
              predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
            }
            nodestack.add(
                new NDPair(new NCPair(predecessor, currncpair.getRight()), currndpair.getRight()));
            visited.add(predecessor);

          }
          predcount = predcount + 1;
        }

        if (fcall) {
          assert predcount == 1 : "fcall should have only one valid predecessor";
        }

        if (freturn) {
          assert predcount == 1 : "freturn should have only one predecessor";
        }

        // need changing funcstack
        while (funcstack.peek().isEmpty()) {
          funcstack.pop();
          visitstack.pop();
          compdiststack.pop();
        }
      }

      i = i + 1;
    }
  }

  public void calcDistanceToError2() {
    int i = 0;

    for (CFAEdge edge : errorEdges) {

      System.out.println("Start erroredge " + i);

      DistanceToErrComparator dtecomp = new DistanceToErrComparator(i);
      NavigableSet<NDPair> nodestack = new TreeSet<>(dtecomp);

      Set<CFANode> visited = new HashSet<>();

      // function name and its waited nodes
      Map<String, Set<NDPair>> function_En = new HashMap<>();
      Map<String, Integer> function_LocD = new HashMap<>();
      // function name and its shortest dist
      Map<String, Integer> function_Ex = new HashMap<>();
      //function name and its direct dist
      Map<String, Integer> direct_Ex = new HashMap<>();

      CFANode errnode = edge.getSuccessor();
      if (!errnode.getInitVisit()) {
        continue;
      }
      errnode.setDistancetoerr(i, 0);

      CFANode node = edge.getPredecessor();

      assert node
          .getNumLeavingEdges() == 1 : "predecessor of error edge should have only one leaving edge";
      node.setDistancetoerr(i, 1);

      visited.add(node);
      visited.add(errnode);

      nodestack.add(new NDPair(new NCPair(node, null), 0));

      while (!nodestack.isEmpty()) {
        NDPair currndpair = nodestack.pollFirst();
        NCPair currncpair = currndpair.getLeft();
        CFANode currnode = currncpair.getLeft();

        Iterator<CFANode> predecessors = CFAUtils.predecessorsOf(currnode).iterator();

        while (predecessors.hasNext()) {
          CFANode predecessor = predecessors.next();
          CFAEdge preedge = predecessor.getEdgeTo(currnode);
          CFAEdgeType preedgetype = preedge.getEdgeType();

          if (!visitedByForward.contains(predecessor)) {
            continue;
          }

          //action for each edge type

          if (preedgetype == CFAEdgeType.AssumeEdge) {
            if (visited.contains(predecessor)) {
              continue;
            }
            predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i) + 1);

            // increase local dist
            Integer predndint = currndpair.getRight() + 1;

            nodestack.add(new NDPair(new NCPair(predecessor, currncpair.getRight()), predndint));
            visited.add(predecessor);

          } else if (preedgetype == CFAEdgeType.StatementEdge) {

            if (preedge instanceof CFunctionSummaryStatementEdge) {
              continue;
            }

            if (visited.contains(predecessor)) {
              continue;
            }

            predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));

            nodestack.add(
                new NDPair(new NCPair(predecessor, currncpair.getRight()), currndpair.getRight()));
            visited.add(predecessor);

          } else if (preedgetype == CFAEdgeType.FunctionReturnEdge) {

            String funcname = predecessor.getFunctionName();
            CFANode caller = currnode.getEnteringSummaryEdge().getPredecessor();

            if (function_En.containsKey(funcname)) {// somebody is traversing (or traversed) the function

              if (visited.contains(caller)) {
                continue;
              }

              if (function_Ex.containsKey(funcname)) {// traversed!
                // caller is the predecessor
                NDPair tempndpair =
                    new NDPair(
                        new NCPair(caller, currncpair.getRight()),
                        currndpair.getRight() + function_Ex.get(funcname));
                caller
                    .setDistancetoerr(i, currnode.getDistancetoerr(i) + function_Ex.get(funcname));

                nodestack.add(tempndpair);
                visited.add(caller);

              } else if(direct_Ex.containsKey(funcname)) {// traversed! directly
                // caller is the predecessor and might be visited

              } else {// is traversing!
             // caller is the predecessor but not yet computed
                NDPair tempndpair = new NDPair(new NCPair(caller, currncpair.getRight()),
                        currndpair.getRight());
                caller
                .setDistancetoerr(i, currnode.getDistancetoerr(i));
                function_En.get(funcname).add(tempndpair);
              }

            } else {// I'm the first who traverse the function
              function_En.put(funcname, new HashSet<NDPair>());
              function_LocD.put(funcname, currndpair.getRight());
              predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
              NCPair predncpair =
                  new NCPair(predecessor, new NCPair(caller, currncpair.getRight()));
              nodestack.add(new NDPair(predncpair, 0));
              visited.add(predecessor);
            }

          }else if(preedgetype == CFAEdgeType.FunctionCallEdge) {

            if (visited.contains(predecessor)) {
              continue;
            }

            if (currncpair.getRight() != null) {// has caller function
              if (predecessor.compareTo(currncpair.getRight().getLeft()) != 0) {
                // invalid callEdge
                continue;
              }

              function_Ex.put(currnode.getFunctionName(), currndpair.getRight());
              predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
              nodestack.add(
                  new NDPair(
                      new NCPair(predecessor, currncpair.getRight().getRight()),
                      function_LocD.get(currnode.getFunctionName()) + currndpair.getRight()));
              visited.add(predecessor);

            } else {// has no caller function

              predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
              nodestack.add(
                  new NDPair(
                      new NCPair(predecessor, currncpair.getRight()),
                      currndpair.getRight()));
              visited.add(predecessor);
              if (!direct_Ex.containsKey(currnode.getFunctionName())) {
                direct_Ex.put(currnode.getFunctionName(), currnode.getDistancetoerr(i));
              }

            }

          } else {

            if (visited.contains(predecessor)) {
              continue;
            }

            predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
            nodestack.add(
                new NDPair(new NCPair(predecessor, currncpair.getRight()), currndpair.getRight()));
            visited.add(predecessor);

          }
          //end action for an edge
        }
        // end actions for predecessors
        // handle waited function returns in here
        for (Entry<String, Set<NDPair>> entry : function_En.entrySet()) {
          if (direct_Ex.containsKey(entry.getKey())) {
            // direct_Ex found! throw all nodes!
            entry.getValue().clear();
          }

          if (function_Ex.containsKey(entry.getKey())) {
            // function_Ex found! add all nodes!
            for (NDPair apair : entry.getValue()) {
              apair.getLeft().getLeft().setDistancetoerr(
                  i,
                  apair.getLeft().getLeft().getDistancetoerr(i) + function_Ex.get(entry.getKey()));
              apair.setRight(apair.getRight() + function_Ex.get(entry.getKey()));
              nodestack.add(apair);
              visited.add(apair.getLeft().getLeft());
            }

            entry.getValue().clear();
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

    private int curridx;

    public DistanceToErrComparator(int pi) {
      curridx = pi;
    }

    @Override
    public int compare(NDPair pnc0, NDPair pnc1) {
      CFANode pArg0 = pnc0.getLeft().getLeft();
      CFANode pArg1 = pnc1.getLeft().getLeft();
      int ret = Integer.compare(pArg0.getDistancetoerr(curridx), pArg1.getDistancetoerr(curridx));
      if (ret == 0) {
        ret = pArg0.compareTo(pArg1);
      }
      return ret;
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

  private static class NDPair implements Pair<NCPair, Integer> {
    private NCPair ncpair;
    private Integer compdist;

    public NDPair(NCPair pPair, Integer pDist) {
      ncpair = pPair;
      compdist = pDist;
    }

    @Override
    public void setLeft(NCPair pPt1) {
      ncpair = pPt1;
    }

    @Override
    public NCPair getLeft() {
      return ncpair;
    }

    @Override
    public void setRight(Integer pPt2) {
      compdist = pPt2;
    }

    @Override
    public Integer getRight() {
      return compdist;
    }
  }

  private static class FDPair implements Pair<String, Integer> {
    private String fname;
    private Integer fcompdist;

    public FDPair(String pName, Integer pDist) {
      fname = pName;
      fcompdist = pDist;
    }

    @Override
    public void setLeft(String pPt1) {
      fname = pPt1;
    }

    @Override
    public String getLeft() {
      return fname;
    }

    @Override
    public void setRight(Integer pPt2) {
      fcompdist = pPt2;
    }

    @Override
    public Integer getRight() {
      return fcompdist;
    }

  }
}
