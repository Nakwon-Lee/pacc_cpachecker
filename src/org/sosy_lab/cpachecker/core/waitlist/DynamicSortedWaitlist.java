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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.SearchStrategyFormula;
import org.sosy_lab.cpachecker.core.searchstrategy.WARGState;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;

public class DynamicSortedWaitlist implements Waitlist {

  //invariant: all entries in this map are non-empty
  private NavigableMap<WARGState, Waitlist> waitlist;

  //DEBUG
  private final WaitlistFactory wrappedWaitlist;
  //protected final WaitlistFactory wrappedWaitlist;
  //GUBED

  private int size = 0;

  private Set<String> vars;

  private SearchStrategyFormula searchForm;

  protected DynamicSortedWaitlist(WaitlistFactory pSecondaryStrategy, String pVars, SearchStrategyFormula.Factory pSSForm) throws InvalidConfigurationException{

    String[] tVars = pVars.split(",");

    vars = new HashSet<>();

    for(String tv : tVars){
      vars.add(tv);
    }

    assert pSSForm != null : "pSSForm must not be null!";

    searchForm = pSSForm.create(vars);

    wrappedWaitlist = Preconditions.checkNotNull(pSecondaryStrategy);

    waitlist = new TreeMap<>(searchForm);
  }

  @Override
  public void add(AbstractState pState) {

    /*
    ARGState ast = AbstractStates.extractStateByType(pState, ARGState.class);
    if (ast.getStateId() == 0){
      System.out.println("what?!");
    }
    */

    WARGState key = getSortKey(pState);
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
  public boolean contains(AbstractState pState) {
    WARGState key = getSortKey(pState);
    Waitlist localWaitlist = waitlist.get(key);
    if (localWaitlist == null) {
      return false;
    }
    assert !localWaitlist.isEmpty();
    return localWaitlist.contains(pState);
  }

  @Override
  public void clear() {
    waitlist.clear();
    size = 0;
  }

  @Override
  public boolean isEmpty() {
    assert waitlist.isEmpty() == (size == 0);
    return waitlist.isEmpty();
  }

  @Override
  public Iterator<AbstractState> iterator() {
    return Iterables.concat(waitlist.values()).iterator();
  }

  @Override
  //DEBUG
  //originally final method but I modify it as non-final
  //GUBED
  public AbstractState pop() {
    Entry<WARGState, Waitlist> highestEntry = null;
    /*
    //DEBUG
    if (this instanceof CallstackSortedWaitlist){
      if (waitlist.size() > 0){
        for (Entry<K, Waitlist> entry : waitlist.entrySet()){
          System.out.print(entry.getValue().getClass().getName());
          System.out.println(" "+entry.getValue().size()+"  key: "+entry.getKey());
          }
        }
    }
    boolean check = true;
    if (this instanceof DynamicSortedWaitlist){
      if (waitlist.size() > 0){
        for (Entry<K, Waitlist> entry : waitlist.entrySet()){
          K key = entry.getKey();
          if (key instanceof SimpleSearchInfo){
            SimpleSearchInfo skey = (SimpleSearchInfo) key;
            if (skey.getInfos().get("isAbsSt")==0){
              check = false;
            }
          }
        }
      }
    }
    if (check){
      System.out.println("good! only AbsSts");
    }
    //GUBED
     * * */

    highestEntry = waitlist.firstEntry();
    WARGState pkey = highestEntry.getKey();
    Waitlist localWaitlist = highestEntry.getValue();

    assert !localWaitlist.isEmpty();
    AbstractState result = localWaitlist.pop();
    if (localWaitlist.isEmpty()) {
      waitlist.remove(pkey);
    }
    size--;

    return result;
  }

  @Override
  public boolean remove(AbstractState pState) {

    WARGState key = getSortKey(pState);
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

  public WaitlistFactory getWLF(){
    return wrappedWaitlist;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return waitlist.toString();
  }

  protected WARGState getSortKey(AbstractState pState) {
    assert pState instanceof ARGState : "given state must be an ARGState";
    WARGState argstate = new WARGState((ARGState)pState,searchForm);

    return argstate;
  }

  public static WaitlistFactory factory(final WaitlistFactory pSecondaryStrategy, final String pSearchVars, final SearchStrategyFormula.Factory pSSForm) {
    return new WaitlistFactory() {

      @Override
      public Waitlist createWaitlistInstance() {
        try {
          return new DynamicSortedWaitlist(pSecondaryStrategy, pSearchVars, pSSForm);
        } catch (InvalidConfigurationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        return null;
      }
    };
  }
}
