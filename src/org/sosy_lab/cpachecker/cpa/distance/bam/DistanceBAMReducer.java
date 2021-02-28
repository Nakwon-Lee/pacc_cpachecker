// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance.bam;

import org.sosy_lab.cpachecker.cfa.blocks.Block;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.FunctionExitNode;
import org.sosy_lab.cpachecker.core.defaults.GenericReducer;
import org.sosy_lab.cpachecker.core.interfaces.Precision;

public class DistanceBAMReducer extends GenericReducer<DistanceBAMState, Precision> {

  private final DistanceBAMStateFactory factory;

  DistanceBAMReducer(DistanceBAMStateFactory pFactory) {
    factory = pFactory;
  }

  @Override
  protected DistanceBAMState
      getVariableReducedState0(DistanceBAMState pExpandedState, Block pContext, CFANode pCallNode)
          throws InterruptedException {
    DistanceBAMState reducedentry = factory.getEntryState(pExpandedState.getLocationNode(), null);
    factory.putCalldist(reducedentry, pExpandedState.getCallState().getDistance());
    return reducedentry;
  }

  @Override
  protected DistanceBAMState getVariableExpandedState0(
      DistanceBAMState pRootState,
      Block pReducedContext,
      DistanceBAMState pReducedState)
      throws InterruptedException {
    DistanceBAMState expanded = factory.getState(
        pReducedState.getLocationNode(),
        pRootState.getEntryState(),
        pRootState.getCallState());
    // factory.rmCalldist(pReducedState.getEntryState());
    return expanded;
  }

  @Override
  protected Object getHashCodeForState0(DistanceBAMState pStateKey, Precision pPrecisionKey) {
    return pStateKey.hashCode();
  }

  @Override
  protected Precision getVariableReducedPrecision0(Precision pPrecision, Block pContext) {
    return pPrecision;
  }

  @Override
  protected Precision getVariableExpandedPrecision0(
      Precision pRootPrecision,
      Block pRootContext,
      Precision pReducedPrecision) {
    return pReducedPrecision;
  }

  @Override
  protected DistanceBAMState rebuildStateAfterFunctionCall0(
      DistanceBAMState pRootState,
      DistanceBAMState pEntryState,
      DistanceBAMState pExpandedState,
      FunctionExitNode pExitLocation) {
    return pExpandedState;
  }

}
