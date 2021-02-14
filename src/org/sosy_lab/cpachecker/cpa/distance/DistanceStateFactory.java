// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.cpachecker.cfa.model.CFANode;

public class DistanceStateFactory {

  public DistanceStateFactory(Configuration config) throws InvalidConfigurationException {
    config.inject(this);
  }

  public DistanceState getInitState(CFANode node) {
    int absdistance = checkNotNull(node).getAbsDistanceId();
    return createDistanceState(node, 0, absdistance);
  }

  public DistanceState getState(CFANode node, int calldist) {

    int absdistance = checkNotNull(node).getAbsDistanceId();
    int reldistance = checkNotNull(node).getRelDistanceId() + calldist;

    if (absdistance <= reldistance) {
      return createDistanceState(node, calldist, absdistance);
    } else {
      return createDistanceState(node, calldist, reldistance);
    }
  }

  private DistanceState createDistanceState(CFANode node, int calldist, int distance) {
    return new DistanceState(node, calldist, distance);
  }
}
