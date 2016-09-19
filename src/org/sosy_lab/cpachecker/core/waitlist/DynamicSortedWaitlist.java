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

import org.sosy_lab.common.Classes;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.cpachecker.core.defaults.SimpleSearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfoable;
import org.sosy_lab.cpachecker.core.interfaces.SearchStrategyFormula;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.cpa.callstack.CallstackState;
import org.sosy_lab.cpachecker.cpa.predicate.PredicateAbstractState;
import org.sosy_lab.cpachecker.util.AbstractStates;

public class DynamicSortedWaitlist extends AbstractSortedWaitlist<SearchInfo> {

  private int nOfVars;

  private SearchStrategyFormula searchForm;

  protected DynamicSortedWaitlist(WaitlistFactory pSecondaryStrategy, int nVars, Class<? extends SearchStrategyFormula> pSSForm) throws InvalidConfigurationException {
    super(pSecondaryStrategy);

    nOfVars = nVars;

    assert pSSForm != null : "pSSForm must not be null!";

    searchForm = Classes.createInstance(SearchStrategyFormula.class, pSSForm,
        new Class<?>[] {Integer.class},
        new Object[] {nOfVars});
  }

  @Override
  public AbstractState pop(){
    AbstractState ret = super.pop();
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

    return ret;
  }

  @Override
  protected SearchInfo getSortKey(AbstractState pState) {
    assert pState instanceof SearchInfoable : "given state must be a SearchInfoable";
    SearchInfoable siPstate = (SearchInfoable)pState;
    SearchInfo tSInfo = siPstate.getSearchInfo();
    if (tSInfo == null) {
      tSInfo = makeSearchInfo(pState);
    }
    return tSInfo;
  }

//temporal solution... SearchInfo should be ARG base! (SimpleSearchInfo)
  public SearchInfo makeSearchInfo(AbstractState pState) {
    // TODO Auto-generated method stub
    assert pState instanceof SearchInfoable : "given state must be a SearchInfoable";
    SearchInfoable tSIState = (SearchInfoable)pState;

    //TODO SeachInfo should be calculated in here
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

    SimpleSearchInfo newInfo = new SimpleSearchInfo(searchForm);
    newInfo.getInfos().put("TreeDepth", tDep);
    newInfo.getInfos().put("RPOrder", tRPOrder);
    newInfo.getInfos().put("CallStack", tCStack);
    newInfo.getInfos().put("BlkDepth", tBlkDepth);
    newInfo.getInfos().put("isAbsSt", tAbsSt);

    assert newInfo.getInfos().size() == nOfVars : "number of variables and size of info list should be same";

    tSIState.setSearchInfo(newInfo);

    return newInfo;
  }

  public static WaitlistFactory factory(final WaitlistFactory pSecondaryStrategy, final int pNOfVars, final Class<? extends SearchStrategyFormula> pSSForm) {
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
