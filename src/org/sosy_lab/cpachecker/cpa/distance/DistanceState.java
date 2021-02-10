// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.distance;

import static org.sosy_lab.cpachecker.util.CFAUtils.enteringEdges;
import static org.sosy_lab.cpachecker.util.CFAUtils.leavingEdges;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.sosy_lab.cpachecker.cfa.ast.FileLocation;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFAEdgeType;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.c.CLabelNode;
import org.sosy_lab.cpachecker.core.interfaces.AbstractQueryableState;
import org.sosy_lab.cpachecker.core.interfaces.AbstractStateWithLocation;
import org.sosy_lab.cpachecker.core.interfaces.Partitionable;
import org.sosy_lab.cpachecker.exceptions.InvalidQueryException;
import org.sosy_lab.cpachecker.util.CFAUtils;
import org.sosy_lab.cpachecker.util.globalinfo.CFAInfo;
import org.sosy_lab.cpachecker.util.globalinfo.GlobalInfo;

public class DistanceState
    implements AbstractStateWithLocation, AbstractQueryableState, Partitionable, Serializable {
  private static final long serialVersionUID = -801176497691618779L;
  private transient CFANode distanceNode;
  private int distance;

  DistanceState(CFANode pDistanceNode, int pDistance) {
    distanceNode = pDistanceNode;
    distance = pDistance;
  }

  public int getDistance() {
    return distance;
  }

  @Override
  public CFANode getLocationNode() {
    return distanceNode;
  }

  @Override
  public Iterable<CFANode> getLocationNodes() {
    return Collections.singleton(distanceNode);
  }

  @Override
  public Iterable<CFAEdge> getOutgoingEdges() {
    return leavingEdges(distanceNode);
  }

  @Override
  public Iterable<CFAEdge> getIngoingEdges() {
    return enteringEdges(distanceNode);
  }

  @Override
  public String toString() {
    String loc = distanceNode.describeFileLocation();
    return distanceNode + (loc.isEmpty() ? "" : " (" + loc + ")");
  }

  @Override
  public boolean checkProperty(String pProperty) throws InvalidQueryException {
    List<String> parts = Splitter.on("==").trimResults().splitToList(pProperty);
    if (parts.size() != 2) {
      throw new InvalidQueryException(
          "The Query \""
              + pProperty
              + "\" is invalid. Could not split the property string correctly.");
    } else {
      switch (parts.get(0).toLowerCase()) {
        case "line":
          try {
            int queryLine = Integer.parseInt(parts.get(1));
            for (CFAEdge edge : CFAUtils.enteringEdges(this.distanceNode)) {
              if (edge.getLineNumber() == queryLine) {
                return true;
              }
            }
            return false;
          } catch (NumberFormatException nfe) {
            throw new InvalidQueryException(
                "The Query \""
                    + pProperty
                    + "\" is invalid. Could not parse the integer \""
                    + parts.get(1)
                    + "\"");
          }
        case "functionname":
          return this.distanceNode.getFunctionName().equals(parts.get(1));
        case "label":
          return this.distanceNode instanceof CLabelNode
              ? ((CLabelNode) this.distanceNode).getLabel().equals(parts.get(1))
              : false;
        case "nodenumber":
          try {
            int queryNumber = Integer.parseInt(parts.get(1));
            return this.distanceNode.getNodeNumber() == queryNumber;
          } catch (NumberFormatException nfe) {
            throw new InvalidQueryException(
                "The Query \""
                    + pProperty
                    + "\" is invalid. Could not parse the integer \""
                    + parts.get(1)
                    + "\"");
          }
        case "mainentry":
          if (distanceNode.getNumEnteringEdges() == 1
              && distanceNode.getFunctionName().equals(parts.get(1))) {
            CFAEdge enteringEdge = distanceNode.getEnteringEdge(0);
            if (enteringEdge.getDescription().equals("Function start dummy edge")
                && enteringEdge.getEdgeType() == CFAEdgeType.BlankEdge
                && FileLocation.DUMMY.equals(enteringEdge.getFileLocation())) {
              return true;
            }
          }
          return false;
        default:
          throw new InvalidQueryException(
              "The Query \""
                  + pProperty
                  + "\" is invalid. \""
                  + parts.get(0)
                  + "\" is no valid keyword");
      }
    }
  }

  @Override
  public String getCPAName() {
    return "distance";
  }

  @Override
  public Object evaluateProperty(String pProperty) throws InvalidQueryException {
    if (pProperty.equalsIgnoreCase("lineno")) {
      if (this.distanceNode.getNumEnteringEdges() > 0) {
        return this.distanceNode.getEnteringEdge(0).getLineNumber();
      }
      return 0; // DUMMY
    } else {
      return checkProperty(pProperty);
    }
  }

  @Override
  public Object getPartitionKey() {
    return this;
  }

  // no equals and hashCode because there is always only one element per CFANode

  private Object writeReplace() {
    return new SerialProxy(distanceNode.getNodeNumber());
  }

  /**
   * javadoc to remove unused parameter warning
   *
   * @param in the input stream
   */
  @SuppressWarnings("UnusedVariable") // parameter is required by API
  private void readObject(ObjectInputStream in) throws IOException {
    throw new InvalidObjectException("Proxy required");
  }

  private static class SerialProxy implements Serializable {
    private static final long serialVersionUID = 6889568471468710163L;
    private final int nodeNumber;

    public SerialProxy(int nodeNumber) {
      this.nodeNumber = nodeNumber;
    }

    private Object readResolve() {
      CFAInfo cfaInfo = GlobalInfo.getInstance().getCFAInfo().orElseThrow();
      return cfaInfo.getLocationStateFactory().getState(cfaInfo.getNodeByNodeNumber(nodeNumber));
    }
  }
}
