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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.sosy_lab.common.Classes;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.SearchStrategyFormula;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

@Options(prefix="waitlist")
public class DynamicSortedWaitlist implements Waitlist {

  //invariant: all entries in this map are non-empty
  private NavigableMap<ARGState, Waitlist> waitlist;

  //DEBUG
  private final WaitlistFactory wrappedWaitlist;
  //protected final WaitlistFactory wrappedWaitlist;
  //GUBED

  private int size = 0;

  private Set<String> vars;

  private SearchStrategyFormula searchForm;

  protected DynamicSortedWaitlist(WaitlistFactory pSecondaryStrategy, String pVars, Class<? extends SearchStrategyFormula> pSSForm) throws InvalidConfigurationException {

    String[] tVars = pVars.split(",");

    vars = new HashSet<>();

    for(String tv : tVars){
      vars.add(tv);
    }

    assert pSSForm != null : "pSSForm must not be null!";

    searchForm = Classes.createInstance(SearchStrategyFormula.class, pSSForm,
        new Class<?>[] {Set.class},
        new Object[] {vars});

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
    Entry<ARGState, Waitlist> highestEntry = null;
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


    highestEntry = waitlist.lastEntry();
    Waitlist localWaitlist = highestEntry.getValue();
    assert !localWaitlist.isEmpty();
    AbstractState result = localWaitlist.pop();
    if (localWaitlist.isEmpty()) {
      waitlist.remove(highestEntry.getKey());
    }
    size--;
    return result;
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

  protected ARGState getSortKey(AbstractState pState) {
    assert pState instanceof ARGState : "given state must be a ARGState";
    ARGState argstate = (ARGState)pState;

    return argstate;
  }

  //temporal solution... SearchInfo should be ARG base! (SimpleSearchInfo)
  /*
  public SearchInfo makeSearchInfo(AbstractState pState) {
    // TODO Auto-generated method stub
    assert pState instanceof SearchInfoable : "given state must be a SearchInfoable";
    SearchInfoable tSIState = (SearchInfoable)pState;

    //TODO SeachInfo should be calculated in here
    //SearchInfo must not be modified after the generation of ARGState
    //Therefore, SearchInfo must be confirmed at generation time

    ARGState tARGState = AbstractStates.extractStateByType(pState, ARGState.class);
    assert tARGState != null : "extractStateByType is failed! (ARGState)";

    int tDep = tARGState.getTreeDepth();
    int tRPOrder = AbstractStates.extractLocation(pState).getReversePostorderId();
    int tAbsSt = tARGState.isAbsState();

    CallstackState callstackState =
        AbstractStates.extractStateByType(pState, CallstackState.class);
    assert callstackState != null : "extractStateByType is failed! (CallstackState)";
    int tCStack = (callstackState != null) ? callstackState.getDepth() : 0;

    PredicateAbstractState predicateState = AbstractStates.extractStateByType(pState, PredicateAbstractState.class);
    assert predicateState != null : "extractStateByType is failed! (predicateState)";

    if (predicateState.isAbstractionState()){
      tARGState.incBlkDepth();
    }

    int tBlkDepth = tARGState.getBlkDepth();

    SimpleSearchInfo newInfo = new SimpleSearchInfo();

    for (String var : vars){

      boolean key = false;

      if (var.equals("TreeDepth")){
        newInfo.getInfos().put("TreeDepth", tDep);
        key = true;
      }
      if (var.equals("CallStack")){
        newInfo.getInfos().put("CallStack", tCStack);
        key = true;
      }
      if (var.equals("RPOrder")){
        newInfo.getInfos().put("RPOrder", tRPOrder);
        key = true;
      }
      if (var.equals("BlkDepth")){
        newInfo.getInfos().put("BlkDepth", tBlkDepth);
        key = true;
      }
      if (var.equals("isAbsSt")){
        newInfo.getInfos().put("isAbsSt", tAbsSt);
        key = true;
      }

      assert key : "All search variables must have its calculated value.";
    }

    tSIState.setSearchInfo(newInfo);

    return newInfo;
  }
*/

  public static WaitlistFactory factory(final WaitlistFactory pSecondaryStrategy, final String pSearchVars, final Class<? extends SearchStrategyFormula> pSSForm) {
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
