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

import com.google.common.collect.Multimap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.sosy_lab.cpachecker.cfa.export.FunctionCallDumper;
import org.sosy_lab.cpachecker.cfa.model.AStatementEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdgeType;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.FunctionEntryNode;
import org.sosy_lab.cpachecker.cfa.model.FunctionExitNode;
import org.sosy_lab.cpachecker.cfa.model.FunctionSummaryEdge;
import org.sosy_lab.cpachecker.cfa.model.c.CFunctionSummaryEdge;
import org.sosy_lab.cpachecker.cfa.model.c.CFunctionSummaryStatementEdge;
import org.sosy_lab.cpachecker.util.CFAUtils;
import org.sosy_lab.cpachecker.util.OverflowSafeCalc;

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

        thisweight = isEdgeWeighted(anode, edge, pScheme);

        if (thisweight) {
          edgeWeights.put(edge, 1);
        } else {
          edgeWeights.put(edge, 0);
        }

        CFAEdgeType edgetype = edge.getEdgeType();

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

  public static void calcAbsDistanceToError(DistanceScheme pScheme) {

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

        if (currnode.getEnteringSummaryEdge() != null) {
          CFAEdge preedge = currnode.getEnteringSummaryEdge();
          CFANode predecessor = preedge.getPredecessor();

          if (reached.contains(predecessor)) {
            continue;
          }

          String predfunc = ((FunctionSummaryEdge) preedge).getFunctionEntry().getFunctionName();

          assert functiondist.containsKey(predfunc) : "No precomputed dist of the given function";

          int thisweight =
              OverflowSafeCalc.add(currnode.getAbsDistanceId(), functiondist.get(predfunc));

          predecessor.setAbsDistanceId(thisweight);
          if (nodequeue.containsKey(thisweight)) {
            nodequeue.get(thisweight).add(predecessor);
          } else {
            nodequeue.put(thisweight, new ArrayDeque<>(List.of(predecessor)));
          }
          reached.add(predecessor);
        } else {
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

            if (!edgeWeights.containsKey(preedge)) {

              boolean tweight = isEdgeWeighted(predecessor, preedge, pScheme);

              if (tweight) {
                edgeWeights.put(preedge, 1);
              } else {
                edgeWeights.put(preedge, 0);
              }
            }

            int thisweight =
                OverflowSafeCalc.add(currnode.getAbsDistanceId(), edgeWeights.get(preedge));
            predecessor.setAbsDistanceId(thisweight);
            if (nodequeue.containsKey(thisweight)) {
              nodequeue.get(thisweight).add(predecessor);
            } else {
              nodequeue.put(thisweight, new ArrayDeque<>(List.of(predecessor)));
            }
            reached.add(predecessor);
          }
        }
      }

      i++;
    }
  }

  public static void calcRelDistanceToError(CFA pcfa, DistanceScheme pScheme) {

    Multimap<String, String> depmap = FunctionCallDumper.findfunctioncalls(pcfa);
    Set<String> headlessfuncs = new HashSet<>();
    for (String callee : depmap.values()) {
      if (pcfa.getFunctionHead(callee) == null) {
        headlessfuncs.add(callee);
      }
    }
    Set<String> callernames = new HashSet<>(depmap.keySet());
    for (String caller : callernames) {
      for (String callee : headlessfuncs) {
        depmap.remove(caller, callee);
      }
    }

    Set<String> functionnames = new HashSet<>(pcfa.getAllFunctionNames());
    while (depmap.containsKey(pcfa.getMainFunction().getFunctionName())) {
      String currfunction = null;
      for (String caller : functionnames) {
        if (!depmap.containsKey(caller)) {
          currfunction = caller;
          break;
        }
      }

      if (currfunction == null) {
        boolean resolved = false;
        List<Set<String>> recursions =
            recursionDetection(depmap, pcfa.getMainFunction().getFunctionName());
        for (int i = 0; i < recursions.size(); i++) {
          Set<String> currset = recursions.get(i);
          // find a function with the smallest length of base case
          NavigableMap<Integer, Deque<String>> basecaselen = new TreeMap<>();
          for (String currfunc : currset) {
            int thisdist = calcRelBaseDistForSingleFunction(pcfa, currfunc, pScheme);
            if (thisdist >= 0) {
              if (basecaselen.containsKey(thisdist)) {
                basecaselen.get(thisdist).add(currfunc);
              } else {
                basecaselen.put(thisdist, new ArrayDeque<>(List.of(currfunc)));
              }
            }
          }

          if (!basecaselen.isEmpty()) {
            resolved = true;
            Entry<Integer, Deque<String>> tempcurrent = basecaselen.firstEntry();

            if (!functiondist.containsKey(tempcurrent.getValue().peek())) {
              functiondist.put(tempcurrent.getValue().peek(), tempcurrent.getKey());

              Set<String> rmarray = new HashSet<>();
              Iterator<String> depit = depmap.keySet().iterator();
              while (depit.hasNext()) {
                String fname = depit.next();
                if (depmap.get(fname).contains(tempcurrent.getValue().peek())) {
                  rmarray.add(fname);
                }
              }

              for (String rmfunc : rmarray) {
                depmap.remove(rmfunc, tempcurrent.getValue().peek());
              }
            }
          }
        }

        if (!resolved) {
          assert false : "non-resolvable recursion";
        }

        continue;
      }

      functiondist.put(currfunction, calcRelDistForSingleFunction(pcfa, currfunction, pScheme));

      functionnames.remove(currfunction);
      Set<String> rmarray = new HashSet<>();
      Iterator<String> depit = depmap.keySet().iterator();
      while (depit.hasNext()) {
        String fname = depit.next();
        if (depmap.get(fname).contains(currfunction)) {
          rmarray.add(fname);
        }
      }

      for (String rmfunc : rmarray) {
        depmap.remove(rmfunc, currfunction);
      }

    }

    Iterator<String> fnameit = functionnames.iterator();
    while (fnameit.hasNext()) {
      String fname = fnameit.next();
      functiondist.put(fname, Integer.MAX_VALUE);
    }
  }

  private static int calcRelDistForSingleFunction(CFA pcfa, String pFname, DistanceScheme pScheme) {

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

          if (functiondist.containsKey(edgefunction)) {
            thisweight = functiondist.get(edgefunction);
          } else {
            thisweight = Integer.MAX_VALUE;
            assert false : "function with no precomputed dist in RelDistforSinglefunction";
          }

        }else {
          preedge = predecessor.getEdgeTo(currnode);

          if (!edgeWeights.containsKey(preedge)) {

            boolean tweight = isEdgeWeighted(predecessor, preedge, pScheme);

            if (tweight) {
              edgeWeights.put(preedge, 1);
            } else {
              edgeWeights.put(preedge, 0);
            }
          }

          thisweight = edgeWeights.get(preedge);
        }

        int nextdist = OverflowSafeCalc.add(currnode.getRelDistanceId(), thisweight);
        predecessor.setRelDistanceId(nextdist);
        if (nodequeue.containsKey(nextdist)) {
          nodequeue.get(nextdist).add(predecessor);
        } else {
          nodequeue.put(
              nextdist,
              new ArrayDeque<>(List.of(predecessor)));
        }
        reached.add(predecessor);
      }
    }

    return pcfa.getFunctionHead(pFname).getRelDistanceId();
  }

  private static int
      calcRelBaseDistForSingleFunction(CFA pcfa, String pFname, DistanceScheme pScheme) {

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
        return pcfa.getFunctionHead(pFname).getRelDistanceId();
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

        if (predecessor.getLeavingSummaryEdge() != null) {
          preedge = predecessor.getLeavingSummaryEdge();
          CFAEdge tedge = currnode.getEnteringSummaryEdge();
          assert preedge.equals(tedge);

          String edgefunction =
              ((FunctionSummaryEdge) preedge).getFunctionEntry().getFunctionName();

          if (functiondist.containsKey(edgefunction)) {
            thisweight = functiondist.get(edgefunction);
          } else {
            continue;
          }

        } else {

          preedge = predecessor.getEdgeTo(currnode);

          if (!edgeWeights.containsKey(preedge)) {

            boolean tweight = isEdgeWeighted(predecessor, preedge, pScheme);

            if (tweight) {
              edgeWeights.put(preedge, 1);
            } else {
              edgeWeights.put(preedge, 0);
            }
          }

          thisweight = edgeWeights.get(preedge);
        }

        int nextdist = OverflowSafeCalc.add(currnode.getRelDistanceId(), thisweight);

        predecessor.setRelDistanceId(nextdist);
        if (nodequeue.containsKey(nextdist)) {
          nodequeue.get(nextdist).add(predecessor);
        } else {
          nodequeue.put(
              nextdist,
              new ArrayDeque<>(List.of(predecessor)));
        }
        reached.add(predecessor);
      }
    }

    return -1;
  }

  private static List<Set<String>>
      recursionDetection(final Multimap<String, String> functionCalls, final String mainFunction) {
    List<Set<String>> retset = new ArrayList<>();
    Deque<Deque<String>> worklist = new ArrayDeque<>();
    Deque<String> currstack = new ArrayDeque<>();
    Set<String> visited = new HashSet<>();
    currstack.push("dummystring");
    worklist.push(new ArrayDeque<>(List.of(mainFunction)));

    while (!worklist.isEmpty()) {

      String nextFunction = worklist.peek().pop();

      if (currstack.contains(nextFunction)) {
        // recursion detected
        retset.add(new HashSet<String>());
        Iterator<String> recit = currstack.iterator();
        while (recit.hasNext()) {
          String topfunc = recit.next();
          if (!topfunc.equals(nextFunction)) {
            retset.get(retset.size() - 1).add(topfunc);
          } else {
            break;
          }
        }
        retset.get(retset.size() - 1).add(nextFunction);

      } else {
        if (visited.add(nextFunction)) {
          if (functionCalls.containsKey(nextFunction)) {
            worklist.push(new ArrayDeque<>(functionCalls.get(nextFunction)));
            currstack.push(nextFunction);
          }
        }
      }

      while (!worklist.isEmpty() && worklist.peek().isEmpty()) {
        worklist.pop();
        currstack.pop();
      }
    }

    return retset;
  }

  private static boolean isEdgeWeighted(CFANode pPred, CFAEdge pEdge, DistanceScheme pScheme) {
    boolean tweight = false;
    CFAEdgeType edgetype = pEdge.getEdgeType();
    switch (pScheme) {
      case STATEMENTS:
        tweight = true;
        break;
      case BASICBLOCKS:
        if (edgetype == CFAEdgeType.AssumeEdge) {
          tweight = true;
        }
        break;
      case LOOPHEADS:
        if (pPred.isLoopStart()) {
          tweight = true;
        }
        break;
      case LOOPSANDFUNCS:
        if (pPred.isLoopStart()) {
          tweight = true;
        }
        if (pPred instanceof FunctionEntryNode || pPred.getEnteringSummaryEdge() != null) {
          tweight = true;
        }
        break;
    }

    return tweight;
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
