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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.ast.AStatement;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallStatement;
import org.sosy_lab.cpachecker.cfa.model.AStatementEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdgeType;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.c.CFunctionSummaryStatementEdge;
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

  private final LogManager logger;
  private Set<CFANode> visitedByForward;

  public BlockedABElbprime(Configuration pConfig, LogManager pLogger)
      throws InvalidConfigurationException {
    pConfig.inject(this);

    this.logger = checkNotNull(pLogger);
  }

  private boolean isAbstractionNode(ReducedNode pNode) {
    return pNode.isLoopHead() && !allowReduceLoopHeads;
  }

  @Override
  public ImmutableSet<CFANode> computeAbstractionNodes(CFA pCfa) {
    Set<CFANode> endlocs = findEndLocations(pCfa.getMainFunction());

    return null;
  }

  // it only finds error locations
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

          AStatementEdge stmtedge = (AStatementEdge) edge;
          AStatement stmt = stmtedge.getStatement();
          if (stmt instanceof CFunctionCallStatement) {
            CFunctionCallStatement cfcstmt = (CFunctionCallStatement) stmt;
            String errorfunname =
                cfcstmt.getFunctionCallExpression()
                    .getFunctionNameExpression()
                    .toQualifiedASTString();
            if (errorfunname.compareTo(errorlocindi) == 0) {
              endlocs.add(successor);
            }
          }
        }
        nodestack.push(successor);
      }
    }

    return endlocs;
  }

  private Set<CFANode> calcABEbpAbstractionNodes(Set<CFANode> pEndlocs) {
    Set<CFANode> absNodes = new HashSet<>();
    absNodes.addAll(pEndlocs);

    Deque<CFANode> nodestack = new ArrayDeque<>();
    Map<CFANode,Integer> branchmap = new HashMap<>();
    Set<CFANode> visited = new HashSet<>();

    nodestack.addAll(pEndlocs);
    visited.addAll(pEndlocs);

    while (!nodestack.isEmpty()) {
      CFANode currnode = nodestack.poll();

    }

    return absNodes;
  }

}
