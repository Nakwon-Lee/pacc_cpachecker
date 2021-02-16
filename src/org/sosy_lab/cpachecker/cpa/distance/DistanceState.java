// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance;

import java.util.Objects;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.interfaces.AbstractStateWithLocation;

public class DistanceState
    implements AbstractStateWithLocation {
  private transient CFANode locNode;
  private final int distance;
  private final int calldist;
  private final DistanceState callstate;

  DistanceState(CFANode pDistanceNode, int pCalldist, DistanceState pCallstate, int pDistance) {
    locNode = pDistanceNode;
    distance = pDistance;
    calldist = pCalldist;
    callstate = pCallstate;
  }

  public int getDistance() {
    return distance;
  }

  public int getCalldist() {
    return calldist;
  }

  public DistanceState getCallstate() {
    return callstate;
  }

  @Override
  public CFANode getLocationNode() {
    return locNode;
  }

  @Override
  public boolean equals(Object pObj) {
    return locNode.equals(((DistanceState) pObj).getLocationNode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getLocationNode());
  }

  @Override
  public String toString() {
    String loc = locNode.describeFileLocation();
    return locNode + (loc.isEmpty() ? "" : " (" + loc + ")") + " distance : " + this.distance;
  }

  @Override
  public Iterable<CFANode> getLocationNodes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<CFAEdge> getOutgoingEdges() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Iterable<CFAEdge> getIngoingEdges() {
    // TODO Auto-generated method stub
    return null;
  }
}
