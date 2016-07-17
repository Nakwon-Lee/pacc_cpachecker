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

import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.sosy_lab.common.Classes;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.cpachecker.core.defaults.SimpleSearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.SearchStrategyFormula;
import org.sosy_lab.cpachecker.cpa.arg.ARGPath;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.cpa.arg.ARGUtils;
import org.sosy_lab.cpachecker.util.AbstractStates;

public class DynamicWaitlist extends AbstractWaitlist<LinkedList<AbstractState>> {

  private int nOfVars;

  private final LinkedHashMap<AbstractState, SearchInfo<Integer>> searchInfoReached;

  private SearchStrategyFormula<Integer> searchForm;

  protected DynamicWaitlist(int nVars, Class<? extends SearchStrategyFormula<Integer>> pSSForm) throws InvalidConfigurationException {
    super(new LinkedList<AbstractState>());
    searchInfoReached = new LinkedHashMap<>();
    nOfVars = nVars;

    assert pSSForm != null : "pSSForm must not be null!";

    searchForm = Classes.createInstance(SearchStrategyFormula.class, pSSForm,
        new Class<?>[] {Integer.class},
        new Object[] {nOfVars});
  }

  @Override
  public AbstractState pop() {
    //TODO evaluate candidates using search strategy formula
    int bestFit = searchForm.getMinFitness();
    int result = -1;
    AbstractState tSt = null;

    for (int i=0; i < waitlist.size(); i++){
      AbstractState state = waitlist.get(i);
      int curr = searchForm.calcSearchFitness(getSearchInfo(state));

      System.out.print(curr+" ");

      if (curr > bestFit){
        bestFit = curr;
        result = i;
        tSt = state;
      }
    }

    assert bestFit > searchForm.getMinFitness() && result >= 0 : "bestFit and result must be bigger than equal to 0";

    System.out.println("sel! "+bestFit);

    searchInfoReached.remove(tSt);

    return waitlist.remove(result);
  }

  @Override
  public void add(AbstractState pStat) {
    super.add(pStat);

    SearchInfo<Integer> tSearchInfo = makeSearchInfo(pStat);

    searchInfoReached.put(pStat, tSearchInfo);
  }

  @Override
  public void clear() {
    super.clear();

    searchInfoReached.clear();
  }

  @Override
  public boolean remove(AbstractState pState) {
    boolean ret = super.remove(pState);
    searchInfoReached.remove(pState);

    return ret;
  }

  public SearchInfo<Integer> getSearchInfo(AbstractState pState){
    return searchInfoReached.get(pState);
  }

  //temporal solution... SearchInfo should be ARG base! (SimpleSearchInfo)
  public SearchInfo<Integer> makeSearchInfo(AbstractState pState) {
    //TODO SeachInfo should be calculated in here
    ARGState tARGState = AbstractStates.extractStateByType(pState, ARGState.class);

    assert tARGState != null : "extractStateByType is failed!";

    ARGPath tPath = ARGUtils.getOnePathTo(tARGState);

    int tDep = tPath.size();

    /*
    Collection<ARGState> tCol = ARGUtils.PARENTS_OF_STATE.apply(tARGState);

    int tDep = -1;
    for(ARGState state : tCol){

      SearchInfo<Integer> tSInfo = getSearchInfo(state);

      assert tSInfo instanceof SimpleSearchInfo : "tSinfo should be simpleSearchInfo!";

      SimpleSearchInfo tSSInfo = (SimpleSearchInfo)tSInfo;

      if (tDep < tSSInfo.getInfos().get(0)){
        tDep = tSSInfo.getInfos().get(0);
      }
    }
    tDep = tDep + 1;
    */
    SimpleSearchInfo newInfo = new SimpleSearchInfo();
    newInfo.getInfos().add(tDep);

    return newInfo;
  }

  public static WaitlistFactory factory(final int pNOfVars, final Class<? extends SearchStrategyFormula<Integer>> pSSForm) {
    return new WaitlistFactory() {

      @Override
      public Waitlist createWaitlistInstance() {
        try {
          return new DynamicWaitlist(pNOfVars, pSSForm);
        } catch (InvalidConfigurationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        return null;
      }
    };
  }
}
