// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance.bam;

import org.sosy_lab.cpachecker.cfa.model.CFANode;

public class DistanceBAMStateFactory {

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

  // public int rmCalldist(DistanceBAMState pState) {
  // if (calldistmap.constainsKey(pState)) {
  // return calldistmap.remove(pState);
  // } else {
  // return -1;
  // }
  // }
}
