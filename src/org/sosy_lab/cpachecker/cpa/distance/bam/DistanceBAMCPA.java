// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance.bam;

import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.AbstractCPA;
import org.sosy_lab.cpachecker.core.defaults.AutomaticCPAFactory;
import org.sosy_lab.cpachecker.core.interfaces.CPAFactory;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysisWithBAM;
import org.sosy_lab.cpachecker.core.interfaces.Reducer;
import org.sosy_lab.cpachecker.core.interfaces.StateSpacePartition;
import org.sosy_lab.cpachecker.core.interfaces.pcc.ProofChecker.ProofCheckerCPA;

public class DistanceBAMCPA extends AbstractCPA
    implements ConfigurableProgramAnalysisWithBAM, ProofCheckerCPA {

  private final static DistanceBAMStateFactory stateFactory = new DistanceBAMStateFactory();

  private DistanceBAMCPA() {
    super(
        "SEP",
        "SEP",
        new DistanceBAMTransferRelation(stateFactory));
  }

  public static CPAFactory factory() {
    return AutomaticCPAFactory.forType(DistanceBAMCPA.class);
  }

  @Override
  public Reducer getReducer() {
    return new DistanceBAMReducer(stateFactory);
  }

  @Override
  public DistanceBAMState getInitialState(CFANode pNode, StateSpacePartition pPartition) {
    DistanceBAMState init = stateFactory.getEntryState(pNode, null);
    stateFactory.putCalldist(init, Integer.MAX_VALUE);
    return init;
  }
}
