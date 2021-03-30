// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance.bam;

import java.io.PrintStream;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.CPAcheckerResult.Result;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.reachedset.UnmodifiableReachedSet;

public class DistanceBAMStateFactory implements Statistics {

  private static final FixedSizeDistanceBAMStateMap calldistmap =
      new FixedSizeDistanceBAMStateMap();

  public DistanceBAMState
      getState(CFANode node, DistanceBAMState pEntry, DistanceBAMState pCall) {
    return new DistanceBAMState(node, pEntry, pCall, this);
  }

  public DistanceBAMState getEntryState(CFANode node, DistanceBAMState pCall) {
    return new DistanceBAMState(node, pCall, this);
  }

  public void putCalldist(DistanceBAMState pState, int pCalldist) {
    calldistmap.put(pState, pCalldist);
  }

  public int getCalldist(DistanceBAMState pState) {
    if (!calldistmap.constainsKey(pState)) {
      return -1;
    } else {
      return calldistmap.get(pState);
    }
  }

  @Override
  public void printStatistics(PrintStream pOut, Result pResult, UnmodifiableReachedSet pReached) {
    pOut.println("Number of stored call distance:  " + calldistmap.size());
  }

  @Override
  public @Nullable String getName() {
    return "DistanceBAMCPA";
  }

  // public int rmCalldist(DistanceBAMState pState) {
  // if (calldistmap.constainsKey(pState)) {
  // return calldistmap.remove(pState);
  // } else {
  // return -1;
  // }
  // }
}
