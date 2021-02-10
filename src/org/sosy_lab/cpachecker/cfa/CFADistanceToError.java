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
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.cpachecker.cfa.ast.AStatement;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCall;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallStatement;
import org.sosy_lab.cpachecker.cfa.model.AStatementEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdgeType;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.FunctionExitNode;
import org.sosy_lab.cpachecker.cfa.model.FunctionSummaryEdge;
import org.sosy_lab.cpachecker.cfa.model.c.CFunctionSummaryEdge;
import org.sosy_lab.cpachecker.cfa.model.c.CFunctionSummaryStatementEdge;
import org.sosy_lab.cpachecker.util.CFAUtils;

public final class CFADistanceToError {

  private static Set<CFAEdge> errorEdges = new HashSet<>();
  private static Map<CFAEdge, Integer> edgeWeights = new HashMap<>();
  private static Map<String, Integer> functiondist = new HashMap<>();

  public static void findErrorLocations(CFA pcfa, String errorindi, DistanceScheme pScheme)
      throws InvalidConfigurationException {

    for (CFANode anode : pcfa.getAllNodes()) {
      boolean thisweight = false;

      Iterator<CFANode> successors = CFAUtils.allSuccessorsOf(anode).iterator();
      while (successors.hasNext()) {
        CFANode successor = successors.next();
        CFAEdge edge = null;
        if (successor.getEnteringSummaryEdge() != null) {
          CFAEdge tedge = successor.getEnteringSummaryEdge();
          if(tedge.getPredecessor().equals(anode)) {
            edge = successor.getEnteringSummaryEdge();
          }
        }

        if(edge==null) {
          edge = anode.getEdgeTo(successor);
        }

        CFAEdgeType edgetype = edge.getEdgeType();

        switch (pScheme) {
          case Statements:
            thisweight = true;
            break;
          case Basicblocks:
            if (edgetype == CFAEdgeType.AssumeEdge) {
              thisweight = true;
            }
            break;
          case Loopheads:
            if (anode.isLoopStart()) {
              thisweight = true;
            }
            break;
        }

        if (thisweight) {
          edgeWeights.put(edge, 1);
        } else {
          edgeWeights.put(edge, 0);
        }

        if (edgetype == CFAEdgeType.CallToReturnEdge) {

          CFunctionSummaryEdge summaryedge = (CFunctionSummaryEdge) edge;
          CFunctionCall funccall = summaryedge.getExpression();
          CFunctionCallExpression callexpr = funccall.getFunctionCallExpression();
          CExpression expr = callexpr.getFunctionNameExpression();
          String errorfunname = expr.toQualifiedASTString();

          if (errorindi.equals(errorfunname)) {
            errorEdges.add(edge);
          }

        } else if (edgetype == CFAEdgeType.StatementEdge) {
          AStatementEdge stmtedge = (AStatementEdge) edge;
          AStatement stmt = stmtedge.getStatement();

          if (stmt instanceof CFunctionCallStatement) {
            CFunctionCallStatement cfcstmt = (CFunctionCallStatement) stmt;
            String errorfunname =
                cfcstmt.getFunctionCallExpression()
                    .getFunctionNameExpression()
                    .toQualifiedASTString();
            if (errorindi.equals(errorfunname)) {
              errorEdges.add(edge);
            }
          }
        }
      }
    }

    if (errorEdges.size() != 1) {
      throw new InvalidConfigurationException(
          "error distance can take programs with only one error location : "
              + String.valueOf(errorEdges.size()));
    }
  }

  public static void calcAbsDistanceToError() {

    int i = 0;
    for (CFAEdge edge : errorEdges) {

      System.out.println("Start erroredge " + i);

      Set<CFANode> reached = new HashSet<>();
      NavigableMap<Integer, Queue<CFANode>> nodequeue = new TreeMap<>();

      CFANode errnode = edge.getSuccessor();
      errnode.setAbsDistanceId(0);
      CFANode node = edge.getPredecessor();
      node.setAbsDistanceId(0);

      reached.add(node);
      reached.add(errnode);

      nodequeue.put(0, new ArrayDeque<>(List.of(node)));

      while(!nodequeue.isEmpty()) {

        Queue<CFANode> firstentryqueue = nodequeue.firstEntry().getValue();
        CFANode currnode = firstentryqueue.poll();
        if (firstentryqueue.isEmpty()) {
          nodequeue.pollFirstEntry();
        }

        Iterator<CFANode> predecessors = CFAUtils.predecessorsOf(currnode).iterator();

        while (predecessors.hasNext()) {
          CFANode predecessor = predecessors.next();
          CFAEdge preedge = predecessor.getEdgeTo(currnode);
          CFAEdgeType preedgetype = preedge.getEdgeType();

          if (reached.contains(predecessor)) {
            continue;
          }
          if (preedgetype == CFAEdgeType.StatementEdge) {
            if (preedge instanceof CFunctionSummaryStatementEdge) {
              continue;
            }
          }

          int thisweight = currnode.getAbsDistanceId() + edgeWeights.get(preedge);
          predecessor.setAbsDistanceId(thisweight);
          if (nodequeue.containsKey(thisweight)) {
            nodequeue.get(thisweight).add(predecessor);
          } else {
            nodequeue.put(thisweight, new ArrayDeque<>(List.of(predecessor)));
          }
          reached.add(predecessor);
        }
      }

      i++;
    }
  }

  public static void calcRelDistanceToError(CFA pcfa) {

    Set<String> functionnames = Set.copyOf(pcfa.getAllFunctionNames());

    Iterator<String> fnamesit = functionnames.iterator();
    while (fnamesit.hasNext()) {
      String currname = fnamesit.next();
      if (functiondist.containsKey(currname)) {
        continue;
      }
      functiondist.put(currname, calcRelDistForSingleFunction(pcfa, currname));
    }
  }

  private static int calcRelDistForSingleFunction(CFA pcfa, String pFname) {

    Set<CFANode> reached = new HashSet<>();
    NavigableMap<Integer, Queue<CFANode>> nodequeue = new TreeMap<>();

    FunctionExitNode exit = pcfa.getFunctionHead(pFname).getExitNode();

    exit.setRelDistanceId(0);
    reached.add(exit);
    nodequeue.put(0, new ArrayDeque<>(List.of(exit)));

    while (!nodequeue.isEmpty()) {
      Queue<CFANode> firstentryqueue = nodequeue.firstEntry().getValue();
      CFANode currnode = firstentryqueue.poll();
      if (firstentryqueue.isEmpty()) {
        nodequeue.pollFirstEntry();
      }

      if (currnode.equals(pcfa.getFunctionHead(pFname))) {
        continue;
      }

      Iterator<CFANode> predecessors = CFAUtils.allPredecessorsOf(currnode).iterator();

      while (predecessors.hasNext()) {
        CFANode predecessor = predecessors.next();

        if (reached.contains(predecessor)) {
          continue;
        }
        if (!currnode.getFunctionName().equals(predecessor.getFunctionName())) {
          continue;
        }

        CFAEdge preedge;
        int thisweight;

        if(predecessor.getLeavingSummaryEdge()!=null) {
          preedge = predecessor.getLeavingSummaryEdge();
          CFAEdge tedge = currnode.getEnteringSummaryEdge();
          assert preedge.equals(tedge);

          String edgefunction =
              ((FunctionSummaryEdge) preedge).getFunctionEntry().getFunctionName();

          // recursion check
          if (pFname.equals(edgefunction)) {
            continue;
          }

          if (functiondist.containsKey(edgefunction)) {
            thisweight = functiondist.get(edgefunction);
          } else {
            thisweight = calcRelDistForSingleFunction(pcfa, edgefunction);
            functiondist.put(edgefunction, thisweight);
          }

        }else {
          preedge = predecessor.getEdgeTo(currnode);
          thisweight = edgeWeights.get(preedge);
        }

        predecessor.setRelDistanceId(currnode.getRelDistanceId() + thisweight);
        if (nodequeue.containsKey(currnode.getRelDistanceId() + thisweight)) {
          nodequeue.get(currnode.getRelDistanceId() + thisweight).add(predecessor);
        } else {
          nodequeue.put(
              currnode.getRelDistanceId() + thisweight,
              new ArrayDeque<>(List.of(predecessor)));
        }
        reached.add(predecessor);
      }
    }

    return pcfa.getFunctionHead(pFname).getRelDistanceId();
  }

  public static String toStringAbsDist(CFANode prootnode) {
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
      retstr = retstr.concat(Integer.toString(currnode.getAbsDistanceId()));
      retstr = retstr.concat(System.lineSeparator());

      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();

      while (successors.hasNext()) {
        CFANode successor = successors.next();
        nodestack.push(successor);
      }
    }
    return retstr;
  }

  public static String toStringRelDist(CFANode prootnode) {
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
      retstr = retstr.concat(Integer.toString(currnode.getRelDistanceId()));
      retstr = retstr.concat(System.lineSeparator());

      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();

      while (successors.hasNext()) {
        CFANode successor = successors.next();
        nodestack.push(successor);
      }
    }
    return retstr;
  }
}
