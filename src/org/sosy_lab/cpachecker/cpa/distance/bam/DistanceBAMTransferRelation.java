// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance.bam;

import java.util.Collection;
import java.util.Collections;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.SingleEdgeTransferRelation;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;

public class DistanceBAMTransferRelation extends SingleEdgeTransferRelation {
  private final DistanceBAMStateFactory factory;

  public DistanceBAMTransferRelation(DistanceBAMStateFactory pFactory) {
    factory = pFactory;
  }

  @Override
  public Collection<DistanceBAMState>
      getAbstractSuccessorsForEdge(AbstractState element, Precision prec, CFAEdge cfaEdge) {

    DistanceBAMState e = (DistanceBAMState) element;
    final CFANode pred = cfaEdge.getPredecessor();
    assert e.getLocationNode().equals(pred) : "no matched e and pred";
    final CFANode succ = cfaEdge.getSuccessor();

    switch (cfaEdge.getEdgeType()) {

      case FunctionCallEdge: {
        DistanceBAMState newentry = factory.getEntryState(succ, e);
        factory.putCalldist(newentry, e.getDistance());
        return Collections.singleton(newentry);
      }

      case FunctionReturnEdge: {

        if (e.getCallState() == null) {
          System.out.println("What");
        }

        // factory.rmCalldist(e.getEntryState());

        return Collections
            .singleton(
                factory.getState(
                    succ,
                    e.getCallState().getEntryState(),
                    e.getCallState().getCallState()));
      }

      default:
        return Collections.singleton(factory.getState(succ, e.getEntryState(), e.getCallState()));
    }
  }
}
