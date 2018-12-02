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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.FunctionExitNode;
import org.sosy_lab.cpachecker.util.CFAUtils;

public class CFANoBranches {
  public int getNoBranches(CFANode pRootNode) {
    Deque<CFANode> nodestack = new ArrayDeque<>();
    Set<CFANode> visited = new HashSet<>();
    int nobranches = 0;

    // pNode must be the root node
    nodestack.push(pRootNode);

    while (!nodestack.isEmpty()) {
      CFANode currnode = nodestack.pop();

      if (visited.contains(currnode)) {
        continue;
      } else {
        visited.add(currnode);
      }

      if (currnode.getNumLeavingEdges() > 1) {
        if (!(currnode instanceof FunctionExitNode)) {
          nobranches = nobranches + 1;
        }
      }

      Iterator<CFANode> successors = CFAUtils.successorsOf(currnode).iterator();

      while (successors.hasNext()) {
        CFANode successor = successors.next();

        nodestack.push(successor);
      }
    }
    return nobranches;
  }
}
