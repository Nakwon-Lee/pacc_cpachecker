// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.AbstractCPA;
import org.sosy_lab.cpachecker.core.defaults.AutomaticCPAFactory;
import org.sosy_lab.cpachecker.core.defaults.SingletonPrecision;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.CPAFactory;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysisWithBAM;
import org.sosy_lab.cpachecker.core.interfaces.StateSpacePartition;
import org.sosy_lab.cpachecker.core.interfaces.pcc.ProofChecker.ProofCheckerCPA;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;

public class DistanceCPA extends AbstractCPA
    implements ConfigurableProgramAnalysisWithBAM, ProofCheckerCPA {

  private final DistanceStateFactory stateFactory;

  private DistanceCPA(DistanceStateFactory pStateFactory) {
    super("sep", "sep", new DistanceTransferRelation(pStateFactory));
    stateFactory = pStateFactory;
  }

  public static CPAFactory factory() {
    return AutomaticCPAFactory.forType(DistanceCPA.class);
  }

  public static DistanceCPA create(Configuration pConfig)
      throws InvalidConfigurationException {
    return new DistanceCPA(new DistanceStateFactory(pConfig));
  }

  @Override
  public DistanceState getInitialState(CFANode pNode, StateSpacePartition pPartition) {
    return stateFactory.getInitState(pNode);
  }

  @Override
  public boolean areAbstractSuccessors(
      AbstractState pElement,
      CFAEdge pCfaEdge,
      Collection<? extends AbstractState> pSuccessors)
      throws CPATransferException, InterruptedException {
    ImmutableSet<? extends AbstractState> successors = ImmutableSet.copyOf(pSuccessors);
    ImmutableSet<? extends AbstractState> actualSuccessors =
        ImmutableSet.copyOf(
            getTransferRelation().getAbstractSuccessorsForEdge(
                pElement,
                SingletonPrecision.getInstance(),
                pCfaEdge));
    return successors.equals(actualSuccessors);
  }
}
