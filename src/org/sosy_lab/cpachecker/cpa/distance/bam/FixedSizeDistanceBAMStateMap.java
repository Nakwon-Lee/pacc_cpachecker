// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance.bam;

import java.util.HashMap;

public class FixedSizeDistanceBAMStateMap extends HashMap<Integer, Integer> {

  private static final long serialVersionUID = -4725262276438555239L;
  // private final int limit;

  // public FixedSizeDistanceBAMStateMap(int pLimit) {
  // limit = pLimit;
  // }

  public Integer get(DistanceBAMState pState) {
    return super.get(pState.getStateId());
  }

  public Integer put(DistanceBAMState pState, Integer pValue) {
    // if (super.size() >= limit) {
    // assert false : "exceed the limit of calldist list";
    // return -1;
    // }else {
    // return super.put(pState.getStateId(), pValue);
    // }
    return super.put(pState.getStateId(), pValue);
  }

  public Integer remove(DistanceBAMState pState) {
    return super.remove(pState.getStateId());
  }

  public boolean constainsKey(DistanceBAMState pState) {
    return super.containsKey(pState.getStateId());
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
