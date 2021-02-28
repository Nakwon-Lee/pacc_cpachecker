// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance.bam;

import java.util.Objects;
import org.sosy_lab.common.UniqueIdGenerator;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.interfaces.AbstractStateWithLocation;
import org.sosy_lab.cpachecker.util.OverflowSafeCalc;

public class DistanceBAMState implements AbstractStateWithLocation {

  private final int stateId;
  private transient CFANode locNode;
  private DistanceBAMState entry;
  private DistanceBAMState call;
  private final DistanceBAMStateFactory factory;

  private static final UniqueIdGenerator idGenerator = new UniqueIdGenerator();

  DistanceBAMState(
      CFANode pLocNode,
      DistanceBAMState pEntry,
      DistanceBAMState pCall,
      DistanceBAMStateFactory pFactory) {
    stateId = idGenerator.getFreshId();
    locNode = pLocNode;
    entry = pEntry;
    call = pCall;
    factory = pFactory;
  }

  DistanceBAMState(
      CFANode pLocNode,
      DistanceBAMState pCall,
      DistanceBAMStateFactory pFactory) {
    stateId = idGenerator.getFreshId();
    locNode = pLocNode;
    entry = this;
    call = pCall;
    factory = pFactory;
  }

  public int getDistance() {

    if (factory.getCalldist(entry) < 0) {
      assert false : "no calldist for distancestate";
    }

    return Math.min(
        locNode.getAbsDistanceId(),
        OverflowSafeCalc.add(factory.getCalldist(entry), locNode.getRelDistanceId()));
  }

  public int getStateId() {
    return stateId;
  }

  public DistanceBAMState getEntryState() {
    return entry;
  }

  public DistanceBAMState getCallState() {
    return call;
  }

  @Override
  public CFANode getLocationNode() {
    return locNode;
  }

  @Override
  public boolean equals(Object pObj) {
    return locNode.equals(((DistanceBAMState) pObj).getLocationNode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getLocationNode());
  }

  @Override
  public String toString() {
    String loc = locNode.describeFileLocation();
    return locNode + (loc.isEmpty() ? "" : " (" + loc + ")");
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
