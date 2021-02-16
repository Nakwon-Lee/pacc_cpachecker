// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance;

import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.AbstractCPA;
import org.sosy_lab.cpachecker.core.defaults.AutomaticCPAFactory;
import org.sosy_lab.cpachecker.core.interfaces.CPAFactory;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysisWithBAM;
import org.sosy_lab.cpachecker.core.interfaces.StateSpacePartition;
import org.sosy_lab.cpachecker.core.interfaces.pcc.ProofChecker.ProofCheckerCPA;

public class DistanceCPA extends AbstractCPA
    implements ConfigurableProgramAnalysisWithBAM, ProofCheckerCPA {

  private final static DistanceStateFactory stateFactory = new DistanceStateFactory();

  private DistanceCPA() {
    super("SEP", "SEP", new DistanceAbstractDomain(), new DistanceTransferRelation(stateFactory));
  }

  public static CPAFactory factory() {
    return AutomaticCPAFactory.forType(DistanceCPA.class);
  }

  @Override
  public DistanceState getInitialState(CFANode pNode, StateSpacePartition pPartition) {
    return stateFactory.getInitState(pNode);
  }
}
