// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.composite;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.AbstractWrapperState;
import org.sosy_lab.cpachecker.core.interfaces.Graphable;
import org.sosy_lab.cpachecker.core.interfaces.Partitionable;
import org.sosy_lab.cpachecker.core.interfaces.Property;
import org.sosy_lab.cpachecker.core.interfaces.PseudoPartitionable;
import org.sosy_lab.cpachecker.core.interfaces.Targetable;
import org.sosy_lab.cpachecker.core.searchstrategy.ARGW;
import org.sosy_lab.cpachecker.cpa.arg.Splitable;

public class CompositeState
    implements AbstractWrapperState, Targetable, Partitionable, PseudoPartitionable, Serializable,
    Graphable, Splitable, ARGW {
  private static final long serialVersionUID = -5143296331663510680L;
  private final ImmutableList<AbstractState> states;
  private transient Object partitionKey; // lazily initialized
  private transient Comparable<?> pseudoPartitionKey; // lazily initialized
  private transient Object pseudoHashCode; // lazily initialized

  // DEBUG
  private boolean isProcessed = false;
  private int stateId = 0;
  private int isAbsSt = 0;
  private int callStack = 0;
  private int revpostodr = -1;
  private int distoerr = Integer.MAX_VALUE;
  private int distoend = Integer.MAX_VALUE;
  // GUBED

  public CompositeState(List<AbstractState> elements) {
    this.states = ImmutableList.copyOf(elements);
  }

  int getNumberOfStates() {
    return states.size();
  }

  @Override
  public boolean isTarget() {
    for (AbstractState element : states) {
      if ((element instanceof Targetable) && ((Targetable)element).isTarget()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Set<Property> getViolatedProperties() throws IllegalStateException {
    checkState(isTarget());
    ImmutableSet.Builder<Property> properties = ImmutableSet.builder();
    for (AbstractState element : states) {
      if ((element instanceof Targetable) && ((Targetable)element).isTarget()) {
        properties.addAll(((Targetable)element).getViolatedProperties());
      }
    }
    return properties.build();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append('(');
    for (AbstractState element : states) {
      builder.append(element.getClass().getSimpleName());
      builder.append(": ");
      builder.append(element.toString());
      builder.append("\n ");
    }
    builder.replace(builder.length() - 1, builder.length(), ")");

    return builder.toString();
  }

  @Override
  public String toDOTLabel() {
    StringBuilder builder = new StringBuilder();
    for (AbstractState element : states) {
      if (element instanceof Graphable) {
        String label = ((Graphable)element).toDOTLabel();
        if (!label.isEmpty()) {
          builder.append(element.getClass().getSimpleName());
          builder.append(": ");
          builder.append(label);
          builder.append("\\n ");
        }
      }
    }

    return builder.toString();
  }

  @Override
  public boolean shouldBeHighlighted() {
    for (AbstractState element : states) {
      if (element instanceof Graphable) {
        if (((Graphable)element).shouldBeHighlighted()) {
          return true;
        }
      }
    }
    return false;
  }

  public AbstractState get(int idx) {
    return states.get(idx);
  }

  @Override
  public ImmutableList<AbstractState> getWrappedStates() {
    return states;
  }


  @Override
  public Object getPartitionKey() {
    if (partitionKey == null) {
      Object[] keys = new Object[states.size()];

      int i = 0;
      for (AbstractState element : states) {
        if (element instanceof Partitionable) {
          keys[i] = ((Partitionable)element).getPartitionKey();
        }
        i++;
      }

      // wrap array of keys in object to enable overriding of equals and hashCode
      partitionKey = new CompositePartitionKey(keys);
    }

    return partitionKey;
  }

  @Override
  public Comparable<?> getPseudoPartitionKey() {
    if (pseudoPartitionKey == null) {
      Comparable<?>[] keys = new Comparable<?>[states.size()];

      int i = 0;
      for (AbstractState element : states) {
        if (element instanceof PseudoPartitionable) {
          keys[i] = ((PseudoPartitionable) element).getPseudoPartitionKey();
        }
        i++;
      }

      // wrap array of keys in object to enable overriding of equals and hashCode
      pseudoPartitionKey = new CompositePseudoPartitionKey(keys);
    }

    return pseudoPartitionKey;
  }

  @Override
  public Object getPseudoHashCode() {
    if (pseudoHashCode == null) {
      Object[] keys = new Object[states.size()];

      int i = 0;
      for (AbstractState element : states) {
        if (element instanceof PseudoPartitionable) {
          keys[i] = ((PseudoPartitionable)element).getPseudoHashCode();
        }
        i++;
      }

      // wrap array of keys in object to enable overriding of equals and hashCode
      pseudoHashCode = new CompositePartitionKey(keys);
    }

    return pseudoHashCode;
  }

  private static final class CompositePartitionKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Object[] keys;

    private CompositePartitionKey(Object[] pElements) {
      keys = pElements;
    }

    @Override
    public boolean equals(Object pObj) {
      if (this == pObj) {
        return true;
      }

      if (!(pObj instanceof CompositePartitionKey)) {
        return false;
      }

      return Arrays.equals(this.keys, ((CompositePartitionKey)pObj).keys);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(keys);
    }

    @Override
    public String toString() {
      return "[" + Joiner.on(", ").skipNulls().join(keys) + "]";
    }
  }

  @SuppressWarnings("rawtypes")
  private static final class CompositePseudoPartitionKey
      implements Comparable<CompositePseudoPartitionKey>, Serializable {

    private static final long serialVersionUID = 1L;

    private final Comparable<?>[] keys;

    private CompositePseudoPartitionKey(Comparable<?>[] pElements) {
      keys = pElements;
    }

    @Override
    public boolean equals(Object pObj) {
      if (this == pObj) {
        return true;
      }

      if (!(pObj instanceof CompositePseudoPartitionKey)) {
        return false;
      }

      return Arrays.equals(this.keys, ((CompositePseudoPartitionKey) pObj).keys);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(keys);
    }

    @Override
    public String toString() {
      return "[" + Joiner.on(", ").skipNulls().join(keys) + "]";
    }

    @Override
    public int compareTo(CompositePseudoPartitionKey other) {
      Preconditions.checkArgument(keys.length == other.keys.length);
      ComparisonChain c = ComparisonChain.start();
      for (int i = 0; i < keys.length; i++) {
        c = c.compare(keys[i], other.keys[i], Ordering.natural().nullsFirst());
      }
      return c.result();
    }
  }

  @Override
  public AbstractState forkWithReplacements(Collection<AbstractState> pReplacementStates) {
    List<AbstractState> wrappedStates = getWrappedStates();
    List<AbstractState> newWrappedStates = new ArrayList<>(wrappedStates.size());

    for (AbstractState state : wrappedStates) {
      int targetSize = newWrappedStates.size()+1;
      // if state was not replaced, add it:
      if (targetSize>newWrappedStates.size()) {
        //recursion might end here if state is not splitable:
        if (state instanceof Splitable) {
        newWrappedStates.add(((Splitable) state).forkWithReplacements(pReplacementStates));
        } else {
          newWrappedStates.add(state);
        }
      }
    }
    CompositeState newState = new CompositeState(newWrappedStates);
    return newState;
  }

  // DEBUG

  @Override
  public void sisAbs(int pAbs) {
    isAbsSt = pAbs;
  }

  @Override
  public int isAbs() {
    return isAbsSt;
  }

  @Override
  public void sCS(int pCS) {
    callStack = pCS;
  }

  @Override
  public int CS() {
    return callStack;
  }

  @Override
  public void sRPO(int pRPO) {
    revpostodr = pRPO;
  }

  @Override
  public int RPO() {
    return revpostodr;
  }

  @Override
  public void suID(int puId) {
    stateId = puId;
  }

  @Override
  public int uID() {
    return stateId;
  }

  @Override
  public void sdistE(int pDistE) {
    distoerr = pDistE;
  }

  @Override
  public int distE() {
    return distoerr;
  }

  @Override
  public void sdEnd(int pDend) {
    distoend = pDend;
  }

  @Override
  public int dEnd() {
    return distoend;
  }

  @Override
  public void setIsP() {
    isProcessed = true;
  }

  @Override
  public boolean isP() {
    return isProcessed;
  }

  // GUBED
}
