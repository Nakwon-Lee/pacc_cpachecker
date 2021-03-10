// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cfa.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.sosy_lab.cpachecker.cfa.ast.AFunctionDeclaration;
import org.sosy_lab.cpachecker.cfa.ast.AParameterDeclaration;
import org.sosy_lab.cpachecker.cfa.ast.AReturnStatement;
import org.sosy_lab.cpachecker.cfa.ast.AVariableDeclaration;
import org.sosy_lab.cpachecker.cfa.ast.FileLocation;
import org.sosy_lab.cpachecker.util.CFAUtils;

public abstract class FunctionEntryNode extends CFANode {

  private static final long serialVersionUID = 1837494813423960670L;
  private final FileLocation location;
  private final AFunctionDeclaration functionDefinition;
  private final Optional<? extends AVariableDeclaration> returnVariable;

  private ImmutableSet<CFANode> functionnodes = null;
  private ImmutableSet<CFAEdge> functionedges = null;

  // Check if call edges are added in the second pass
  private final FunctionExitNode exitNode;

  protected FunctionEntryNode(
      final FileLocation pFileLocation,
      FunctionExitNode pExitNode,
      final AFunctionDeclaration pFunctionDefinition,
      final Optional<? extends AVariableDeclaration> pReturnVariable) {

    super(pFunctionDefinition);
    location = checkNotNull(pFileLocation);
    functionDefinition = pFunctionDefinition;
    exitNode = pExitNode;
    returnVariable = checkNotNull(pReturnVariable);
  }

  public FileLocation getFileLocation() {
    return location;
  }

  public FunctionExitNode getExitNode() {
    return exitNode;
  }

  public AFunctionDeclaration getFunctionDefinition() {
    return functionDefinition;
  }

  public List<String> getFunctionParameterNames() {
    return Lists.transform(functionDefinition.getParameters(), AParameterDeclaration::getName);
  }

  public Optional<ImmutableSet<CFANode>> getFunctionNodes() {
    if (functionnodes == null) {
      collectFunctionNodes();
    }
    return Optional.of(functionnodes);
  }

  public Optional<ImmutableSet<CFAEdge>> getFunctionEdges() {
    if (functionedges == null) {
      collectFunctionNodes();
    }
    return Optional.of(functionedges);
  }

  public int getCyclomaticComplexity() {
    if (functionnodes == null || functionedges == null) {
      collectFunctionNodes();
    }
    return functionedges.size() - functionnodes.size() + 2;
  }

  private void collectFunctionNodes() {
    Set<CFANode> visited = new HashSet<>();
    Set<CFAEdge> edges = new HashSet<>();
    Deque<CFANode> stack = new ArrayDeque<>();

    stack.push(this);

    while (!stack.isEmpty()) {
      CFANode currnode = stack.pop();

      if (visited.add(currnode)) {

        if (currnode.getLeavingSummaryEdge() != null) {
          CFAEdge curredge2 = currnode.getLeavingSummaryEdge();
          CFANode successor2 = curredge2.getSuccessor();

          stack.push(successor2);
          edges.add(curredge2);

        } else {
          Iterator<CFANode> successors = CFAUtils.allSuccessorsOf(currnode).iterator();

          while (successors.hasNext()) {
            CFANode successor = successors.next();
            CFAEdge curredge = currnode.getEdgeTo(successor);

            if (!currnode.getFunctionName().equals(successor.getFunctionName())) {
              continue;
            }

            stack.push(successor);
            edges.add(curredge);
          }
        }
      }
    }

    functionnodes = ImmutableSet.copyOf(visited);
    functionedges = ImmutableSet.copyOf(edges);
  }

  public abstract List<? extends AParameterDeclaration> getFunctionParameters();

  /**
   * Return a declaration for a pseudo variable that can be used to store
   * the return value of this function (if it has one).
   * This variable is the same as the one used by {@link AReturnStatement#asAssignment()}.
   */
  public Optional<? extends AVariableDeclaration> getReturnVariable() {
    return returnVariable;
  }
}