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
import java.util.Collections;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.SingleEdgeTransferRelation;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.util.CFAUtils;

public class DistanceTransferRelation extends SingleEdgeTransferRelation {
  private final DistanceStateFactory factory;

  public DistanceTransferRelation(DistanceStateFactory pFactory) {
    factory = pFactory;
  }

  @Override
  public Collection<DistanceState>
      getAbstractSuccessorsForEdge(AbstractState element, Precision prec, CFAEdge cfaEdge) {

    CFANode node = ((DistanceState) element).getLocationNode();

    if (CFAUtils.allLeavingEdges(node).contains(cfaEdge)) {
      return Collections.singleton(
          factory.getState(cfaEdge.getSuccessor(), ((DistanceState) element).getDistance()));
    }

    return ImmutableSet.of();
  }
}
