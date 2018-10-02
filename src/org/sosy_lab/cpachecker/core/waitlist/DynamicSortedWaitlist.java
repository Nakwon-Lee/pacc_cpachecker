/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2016  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.core.waitlist;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.sosy_lab.common.configuration.ClassOption;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.SearchStrategyFormula;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.util.statistics.StatCounter;
import org.sosy_lab.cpachecker.util.statistics.StatInt;
import org.sosy_lab.cpachecker.util.statistics.StatKind;

@Options(prefix = "analysis.traversal.TS")
public class DynamicSortedWaitlist implements Waitlist {

  @Option(
      secure = true,
      name = "searchformula",
      description = "them name of using searchformula")
  @ClassOption(packagePrefix = "org.sosy_lab.cpachecker")
  private SearchStrategyFormula.Factory searchFormClass;

  private SearchStrategyFormula searchForm;

  private final WaitlistFactory wrappedWaitlist;

  private final NavigableMap<ARGState, Waitlist> waitlist;

  private int size = 0;

  private final StatCounter popCount;
  private final StatCounter delegationCount;
  private final Map<String, StatInt> delegationCounts = new HashMap<>();

  protected DynamicSortedWaitlist(WaitlistFactory pSecondaryStrategy, Configuration pConfig) throws InvalidConfigurationException {
    wrappedWaitlist = Preconditions.checkNotNull(pSecondaryStrategy);
    popCount = new StatCounter("Pop requests to waitlist (" + getClass().getSimpleName() + ")");
    delegationCount = new StatCounter(
        "Pops delegated to wrapped waitlists (" + wrappedWaitlist.getClass().getSimpleName() +
            ")");
    pConfig.inject(this, DynamicSortedWaitlist.class);
    searchForm = searchFormClass.create();
    waitlist = new TreeMap<>(searchForm);
  }

  @Override
  public AbstractState pop(){
    popCount.inc();
    Entry<ARGState, Waitlist> highestEntry = null;
    /*
    assert ret instanceof SearchInfoable : "poped state must be a SearchIfoable";
    SearchInfoable siaRet = (SearchInfoable) ret;
    SearchInfo siRet = siaRet.getSearchInfo();
    assert siRet instanceof SimpleSearchInfo : "poped state must have SimpleSearchInfo";
    SimpleSearchInfo ssiRet = (SimpleSearchInfo)siRet;
    System.out.println("sel! "+ssiRet.getInfos().get("BlkDepth"));
    if (ssiRet.getInfos().get("BlkDepth")==0){
      System.out.println("What!?");
    }

    PredicateAbstractState predicateState = AbstractStates.extractStateByType(ret, PredicateAbstractState.class);
    assert predicateState != null : "extractStateByType is failed! (predicateState)";

    if (predicateState.isAbstractionState()){
      System.out.println("AbstractionState!!!");
    }
    */

    highestEntry = waitlist.lastEntry();
    Waitlist localWaitlist = highestEntry.getValue();
    assert !localWaitlist.isEmpty();
    AbstractState result = localWaitlist.pop();
    if (localWaitlist.isEmpty()) {
      waitlist.remove(highestEntry.getKey());
      addStatistics(localWaitlist);
    } else {
      delegationCount.inc();
    }
    size--;
    return result;
  }

  protected ARGState getSortKey(AbstractState pState) {
    assert pState instanceof ARGState : "given state must be a ARGState";
    ARGState argstate = (ARGState)pState;

    return argstate;
  }

  public static WaitlistFactory factory(final WaitlistFactory pSecondaryStrategy, Configuration pConfig) {
    return new WaitlistFactory() {

      @Override
      public Waitlist createWaitlistInstance() {

          try {
            return new DynamicSortedWaitlist(pSecondaryStrategy, pConfig);
          } catch (InvalidConfigurationException e) {
            // TODO Auto-generated catch block
            throw new AssertionError(e);
          }
      }
    };
  }

  @Override
  public Iterator<AbstractState> iterator() {
    return Iterables.concat(waitlist.values()).iterator();
  }

  @Override
  public void add(AbstractState pState) {
    ARGState key = getSortKey(pState);
    Waitlist localWaitlist = waitlist.get(key);
    if (localWaitlist == null) {
      localWaitlist = wrappedWaitlist.createWaitlistInstance();
      waitlist.put(key, localWaitlist);
    } else {
      assert !localWaitlist.isEmpty();
    }
    localWaitlist.add(pState);
    size++;
  }

  @Override
  public void clear() {
    waitlist.clear();
    size = 0;
  }

  @Override
  public boolean contains(AbstractState pState) {
    ARGState key = getSortKey(pState);
    Waitlist localWaitlist = waitlist.get(key);
    if (localWaitlist == null) {
      return false;
    }
    assert !localWaitlist.isEmpty();
    return localWaitlist.contains(pState);
  }

  @Override
  public boolean isEmpty() {
    assert waitlist.isEmpty() == (size == 0);
    return waitlist.isEmpty();
  }

  @Override
  public boolean remove(AbstractState pState) {
    ARGState key = getSortKey(pState);
    Waitlist localWaitlist = waitlist.get(key);
    if (localWaitlist == null) {
      return false;
    }
    assert !localWaitlist.isEmpty();
    boolean result = localWaitlist.remove(pState);
    if (result) {
      if (localWaitlist.isEmpty()) {
        waitlist.remove(key);
      }
      size--;
    }
    return result;
  }

  @Override
  public int size() {
    return size;
  }

  private void addStatistics(Waitlist pWaitlist) {
    if (pWaitlist instanceof AbstractSortedWaitlist) {
      Map<String, StatInt> delegCount =
          ((AbstractSortedWaitlist<?>) pWaitlist).getDelegationCounts();

      for (Entry<String, StatInt> e : delegCount.entrySet()) {
        String key = e.getKey();
        if (!delegationCounts.containsKey(key)) {
          delegationCounts.put(key, e.getValue());

        } else {
          delegationCounts.get(key).add(e.getValue());
        }
      }
    }
  }

  public Map<String, StatInt> getDelegationCounts() {
    String waitlistName = this.getClass().getSimpleName();
    StatInt directDelegations = new StatInt(StatKind.AVG, waitlistName);
    assert delegationCount.getValue() <= Integer.MAX_VALUE;
    directDelegations.setNextValue((int) delegationCount.getValue());
    delegationCounts.put(waitlistName, directDelegations);
    return delegationCounts;
  }

  public WaitlistFactory getWLF(){
    return wrappedWaitlist;
  }

  @Override
  public String toString() {
    return waitlist.toString();
  }
}
