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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.sosy_lab.cpachecker.cfa.ast.AStatement;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallStatement;
import org.sosy_lab.cpachecker.cfa.model.AStatementEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdgeType;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.c.CFunctionSummaryStatementEdge;
import org.sosy_lab.cpachecker.util.CFAUtils;

public class CFADistanceToError {

  private Set<CFAEdge> errorEdges = new HashSet<>(1);

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
        // assuming that CFunctionCallStatemnet is the only case of error function call
        if (edgetype == CFAEdgeType.StatementEdge) {

          if (edge instanceof CFunctionSummaryStatementEdge) {
            continue;
          }

          AStatementEdge stmtedge = (AStatementEdge)edge;
          AStatement stmt = stmtedge.getStatement();
          if (stmt instanceof CFunctionCallStatement) {
            CFunctionCallStatement cfcstmt = (CFunctionCallStatement) stmt;
            String errorfunname =
                cfcstmt.getFunctionCallExpression()
                    .getFunctionNameExpression()
                    .toQualifiedASTString();
            if (errorfunname.compareTo(errorindi) == 0) {
              errorEdges.add(edge);
            }
          }
        }
        nodestack.push(successor);
      }
    }
    System.out.println("errorEdges: " + errorEdges.toString());
  }

  public void calcDistanceToError() {

    int i = 0;

    for (CFAEdge edge : errorEdges) {

      // System.out.println("Start erroredge " + i);

      DistanceToErrComparator dtecomp = new DistanceToErrComparator(i);

      NavigableSet<CFANode> nodestack = new TreeSet<>(dtecomp);
      Set<CFANode> visited = new HashSet<>();

      CFANode errnode = edge.getSuccessor();
      errnode.setDistancetoerr(i, 0);

      CFANode node = edge.getPredecessor();

      assert node
          .getNumLeavingEdges() == 1 : "predecessor of error edge should have only one leaving edge";
      node.setDistancetoerr(i, 1);

      visited.add(node);
      visited.add(errnode);

      nodestack.add(node);

      while(!nodestack.isEmpty()) {

        /*
         * String temp = ""; for (CFANode dd : nodestack) { temp = temp.concat("N" +
         * dd.getNodeNumber() + ":" + dd.getDistancetoerr(i) + " "); } System.out.println(temp);
         */

        CFANode currnode = nodestack.pollFirst();

        /*
         * System.out .println("Pick N" + currnode.getNodeNumber() + ":" +
         * currnode.getDistancetoerr(i));
         */

        Iterator<CFANode> predecessors = CFAUtils.predecessorsOf(currnode).iterator();

        while (predecessors.hasNext()) {
          CFANode predecessor = predecessors.next();

          CFAEdge preedge = predecessor.getEdgeTo(currnode);
          CFAEdgeType preedgetype = preedge.getEdgeType();

          if (visited.contains(predecessor)) {
            continue;
          }

          if (!visitedByForward.contains(predecessor)) {
            continue;
          }

          if (preedgetype == CFAEdgeType.AssumeEdge) {

            predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i) + 1);
            nodestack.add(predecessor);
            visited.add(predecessor);

          } else if (preedgetype == CFAEdgeType.StatementEdge) {

            if (preedge instanceof CFunctionSummaryStatementEdge) {
              continue;
            }

            predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
            nodestack.add(predecessor);
            visited.add(predecessor);

          } else {

            predecessor.setDistancetoerr(i, currnode.getDistancetoerr(i));
            nodestack.add(predecessor);
            visited.add(predecessor);

          }
        }
      }

      i = i + 1;
    }
  }

  public void initiationDistToError(CFANode prootnode) {
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

      currnode.initDistancetoerr(errorEdges.size());

      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();

      while (successors.hasNext()) {
        CFANode successor = successors.next();
        nodestack.push(successor);
      }
    }
    visitedByForward = visited;
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

  private static class DistanceToErrComparator implements Comparator<CFANode> {

    private int curridx;

    public DistanceToErrComparator(int pi) {
      curridx = pi;
    }

    @Override
    public int compare(CFANode pArg0, CFANode pArg1) {
      int ret = Integer.compare(pArg0.getDistancetoerr(curridx), pArg1.getDistancetoerr(curridx));
      if (ret == 0) {
        ret = pArg0.compareTo(pArg1);
      }
      return ret;
    }
  }
}
