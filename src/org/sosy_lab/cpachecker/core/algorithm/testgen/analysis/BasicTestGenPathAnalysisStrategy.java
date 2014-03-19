/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
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
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.core.algorithm.testgen.analysis;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.sosy_lab.common.LogManager;
import org.sosy_lab.common.Pair;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.algorithm.testgen.StartupConfig;
import org.sosy_lab.cpachecker.core.algorithm.testgen.TestGenStatistics;
import org.sosy_lab.cpachecker.core.algorithm.testgen.model.PredicatePathAnalysisResult;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysis;
import org.sosy_lab.cpachecker.cpa.arg.ARGPath;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;
import org.sosy_lab.cpachecker.util.CFAUtils;
import org.sosy_lab.cpachecker.util.predicates.PathChecker;
import org.sosy_lab.cpachecker.util.predicates.interpolation.CounterexampleTraceInfo;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;


public class BasicTestGenPathAnalysisStrategy implements TestGenPathAnalysisStrategy {

  private PathChecker pathChecker;
  private List<AbstractState> handledDecisions;
  private TestGenStatistics stats;
  ConfigurableProgramAnalysis cpa;
  private LogManager logger;

  public BasicTestGenPathAnalysisStrategy(PathChecker pPathChecker, StartupConfig config, TestGenStatistics pStats) {
    super();
    pathChecker = pPathChecker;
    this.logger = config.getLog();
    stats = pStats;
    handledDecisions = Lists.newLinkedList();
  }


  @Override
  public PredicatePathAnalysisResult findNewFeasiblePathUsingPredicates(final ARGPath pExecutedPath)
      throws CPATransferException, InterruptedException {
    /*
     * create copy of the given path, because it will be modified with this algorithm.
     * represents the current new valid path.
     */
    ARGPath newARGPath = new ARGPath();
    for (Pair<ARGState, CFAEdge> pair : pExecutedPath) {
      newARGPath.add(pair);
    }
    /*
     * only by edge representation of the new path.
     */
    List<CFAEdge> newPath = Lists.newArrayList(newARGPath.asEdgesList());
    /*
     * element removed from the path in the previous iteration
     */
    Pair<ARGState, CFAEdge> lastElement = null;
    Pair<ARGState, CFAEdge> currentElement;
    /*
     * create a descending consuming iterator to iterate through the path from last to first, while consuming elements.
     * Elements are consumed because the new path is a subpath of the original.
     */
    long branchCounter = 0;
    long nodeCounter = 0;
    Iterator<Pair<ARGState, CFAEdge>> branchingEdges = Iterators.consumingIterator(newARGPath.descendingIterator());
    while (branchingEdges.hasNext())
    {
      nodeCounter++;
      currentElement = branchingEdges.next();
      CFAEdge edge = currentElement.getSecond();
      if (edge == null) {
        lastElement = currentElement;
        continue;
      }
      CFANode node = edge.getPredecessor();
      //num of leaving edges does not include a summary edge, so the check is valid.
      if (node.getNumLeavingEdges() != 2) {
        lastElement = currentElement;
        continue;
      }
      //current node is a branching / deciding node. select the edge that isn't represented with the current path.
      CFANode decidingNode = node;

      if (handledDecisions.contains(currentElement.getFirst()))
      {
        logger.log(Level.INFO, "Branch on path was handled in an earlier iteration -> skipping branching.");
        lastElement = currentElement;
        continue;
      }

      if(lastElement == null)
      {
        //if the last element is not set, we encountered a branching node where both paths are infeasible for the current value mapping.
        logger.log(Level.INFO, "encountered an executed path that might be spurious.");
        lastElement = currentElement;
        continue;
      }
      CFAEdge wrongEdge = edge;
      CFAEdge otherEdge = null;
      for (CFAEdge cfaEdge : CFAUtils.leavingEdges(decidingNode)) {
        if (cfaEdge.equals(wrongEdge)) {
          continue;
        } else {
          otherEdge = cfaEdge;
          break;
        }
      }
      logger.logf(Level.INFO, "identified valid branching (skipped branching count: %d, nodes: %d)", branchCounter++, nodeCounter);
      //no edge found should not happen; If it does make it visible.
      assert otherEdge != null;
      /*
       * identified a decision node and selected a new edge.
       * extract the edge-list of the path and add the new edge to it.
       * Don't modify the ARGPath yet, because it is possible that the current decision is infeasible
       */
      newPath = Lists.newArrayList(newARGPath.asEdgesList());
      newPath.add(otherEdge);
      /*
       * check if path is feasible. If it's not continue to identify another decision node
       * If path is feasible, add the ARGState belonging to the decision node and the new edge to the ARGPath. Exit and Return result.
       */
      stats.beforePathCheck();
      CounterexampleTraceInfo traceInfo = pathChecker.checkPath(newPath);
      stats.afterPathCheck();

      if (!traceInfo.isSpurious())
      {
        newARGPath.add(Pair.of(currentElement.getFirst(), otherEdge));
        if (lastElement == null) {
          throw new IllegalStateException("" + newPath.toString()); }
        //        handledDecisions.add(decidingNode);
        logger.logf(Level.INFO, "selected new path %s", newPath.toString());
        handledDecisions.add(currentElement.getFirst());
        return new PredicatePathAnalysisResult(traceInfo, currentElement.getFirst(), lastElement.getFirst(), newARGPath);
      }
      else {
        lastElement = currentElement;
        continue;
      }

    }
    return PredicatePathAnalysisResult.INVALID;
  }


  @Override
  public CounterexampleTraceInfo computePredicateCheck(ARGPath pExecutedPath) throws CPATransferException, InterruptedException {
    return pathChecker.checkPath(pExecutedPath.asEdgesList()
        );
  }

}
