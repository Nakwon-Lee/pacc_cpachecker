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

import org.sosy_lab.common.Classes;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.cpachecker.core.defaults.SimpleSearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.SearchStrategyFormula;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.util.AbstractStates;

public class DynamicSortedWaitlist extends AbstractSortedWaitlist<SearchInfo<String, Integer>> {

  private int nOfVars;

  private final LinkedHashMap<AbstractState, SearchInfo<String, Integer>> searchInfoReached;

  private SearchStrategyFormula<String, Integer> searchForm;

  protected DynamicSortedWaitlist(WaitlistFactory pSecondaryStrategy, int nVars, Class<? extends SearchStrategyFormula<String, Integer>> pSSForm) throws InvalidConfigurationException {
    super(pSecondaryStrategy);
    searchInfoReached = new LinkedHashMap<>();

    nOfVars = nVars;

    assert pSSForm != null : "pSSForm must not be null!";

    searchForm = Classes.createInstance(SearchStrategyFormula.class, pSSForm,
        new Class<?>[] {Integer.class},
        new Object[] {nOfVars});
  }

  @Override
  public void add(AbstractState pState) {
    assert AbstractStates.extractStateByType(pState, ARGState.class) != null;
    super.add(pState);
    SearchInfo<String, Integer> tSearchInfo = makeSearchInfo(pState);
    searchInfoReached.put(pState, tSearchInfo);
  }

  @Override
  public AbstractState pop(){
    AbstractState ret = super.pop();
    System.out.println("sel! "+getSearchInfo(ret).getInfos().get("RPOrder"));
    searchInfoReached.remove(ret);
    return ret;
  }

  @Override
  protected SearchInfo<String, Integer> getSortKey(AbstractState pState) {
    SearchInfo<String, Integer> tSInfo = getSearchInfo(pState);
    if (tSInfo == null) {
      tSInfo = makeSearchInfo(pState);
    }
    return tSInfo;
  }

  @Override
  public boolean remove(AbstractState pState){
    boolean ret = super.remove(pState);
    searchInfoReached.remove(pState);
    return ret;
  }

  public SearchInfo<String,Integer> getSearchInfo(AbstractState pState){
    return searchInfoReached.get(pState);
  }

  //temporal solution... SearchInfo should be ARG base! (SimpleSearchInfo)
  public SearchInfo<String, Integer> makeSearchInfo(AbstractState pState) {
    //TODO SeachInfo should be calculated in here
    ARGState tARGState = AbstractStates.extractStateByType(pState, ARGState.class);


    assert tARGState != null : "extractStateByType is failed!";

    int tDep = tARGState.getTreeDepth();
    int tBran = tARGState.getNOfBranches();
    int tBranMine = tARGState.getNOfBranchesMine();
    int tRPOrder = AbstractStates.extractLocation(pState).getReversePostorderId();

    SimpleSearchInfo newInfo = new SimpleSearchInfo(searchForm);
    newInfo.getInfos().put("TreeDepth", tDep);
    newInfo.getInfos().put("NofBranches", tBran+tBranMine);
    newInfo.getInfos().put("RPOrder", tRPOrder);

    assert newInfo.getInfos().size() == nOfVars : "number of variables and size of info list should be same";

    return newInfo;
  }

  public static WaitlistFactory factory(final WaitlistFactory pSecondaryStrategy, final int pNOfVars, final Class<? extends SearchStrategyFormula<String,Integer>> pSSForm) {
    return new WaitlistFactory() {

      @Override
      public Waitlist createWaitlistInstance() {
        try {
          return new DynamicSortedWaitlist(pSecondaryStrategy, pNOfVars, pSSForm);
        } catch (InvalidConfigurationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        return null;
      }
    };
  }
}
