// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.util.OverflowSafeCalc;

public class DistanceStateFactory {

  public DistanceStateFactory() {
  }

  public DistanceState getInitState(CFANode node) {
    int absdistance = checkNotNull(node).getAbsDistanceId();
    return createDistanceState(node, Integer.MAX_VALUE, null, absdistance);
  }

  public DistanceState getState(CFANode node, int calldist, DistanceState callstate) {

    int absdistance = checkNotNull(node).getAbsDistanceId();
    int reldistance = OverflowSafeCalc.add(checkNotNull(node).getRelDistanceId(), calldist);

    if (absdistance <= reldistance) {
      return createDistanceState(node, calldist, callstate, absdistance);
    } else {
      return createDistanceState(node, calldist, callstate, reldistance);
    }
  }

  private DistanceState
      createDistanceState(CFANode node, int calldist, DistanceState callstate, int distance) {
    return new DistanceState(node, calldist, callstate, distance);
  }
}
