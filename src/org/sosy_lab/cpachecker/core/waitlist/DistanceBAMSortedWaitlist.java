// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.core.waitlist;

import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.cpa.distance.bam.DistanceBAMState;
import org.sosy_lab.cpachecker.util.AbstractStates;

public class DistanceBAMSortedWaitlist extends AbstractSortedWaitlist<Integer> {

  protected DistanceBAMSortedWaitlist(WaitlistFactory pSecondaryStrategy) {
    super(pSecondaryStrategy);
  }

  @Override
  public void add(AbstractState pState) {
    assert AbstractStates.extractStateByType(pState, DistanceBAMState.class) != null;
    super.add(pState);
  }

  @Override
  protected Integer getSortKey(AbstractState pState) {
    return 0 - AbstractStates.extractStateByType(pState, DistanceBAMState.class).getDistance();
  }

  public static WaitlistFactory factory(final WaitlistFactory pSecondaryStrategy) {
    return () -> new DistanceBAMSortedWaitlist(pSecondaryStrategy);
  }
}
