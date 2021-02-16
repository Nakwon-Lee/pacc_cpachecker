// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance;

import java.util.Collection;
import java.util.Collections;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.SingleEdgeTransferRelation;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;

public class DistanceTransferRelation extends SingleEdgeTransferRelation {
  private final DistanceStateFactory factory;

  public DistanceTransferRelation(DistanceStateFactory pFactory) {
    factory = pFactory;
  }

  @Override
  public Collection<DistanceState>
      getAbstractSuccessorsForEdge(AbstractState element, Precision prec, CFAEdge cfaEdge) {

    DistanceState e = (DistanceState) element;
    final CFANode pred = cfaEdge.getPredecessor();
    assert e.getLocationNode().equals(pred) : "no matched e and pred";
    final CFANode succ = cfaEdge.getSuccessor();

    switch (cfaEdge.getEdgeType()) {

      case FunctionCallEdge: {
        return Collections.singleton(factory.getState(succ, e.getDistance(), e));
      }

      case FunctionReturnEdge: {
        return Collections.singleton(
              factory
                  .getState(succ, e.getCallstate().getCalldist(), e.getCallstate().getCallstate()));
      }

      default:
        return Collections.singleton(factory.getState(succ, e.getCalldist(), e.getCallstate()));
    }
  }
}
