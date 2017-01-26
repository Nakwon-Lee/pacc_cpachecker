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
package org.sosy_lab.cpachecker.core.reachedset;

import org.sosy_lab.common.Classes;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.cpachecker.core.defaults.SimpleSearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfoable;
import org.sosy_lab.cpachecker.core.interfaces.SearchStrategyFormula;
import org.sosy_lab.cpachecker.core.waitlist.Waitlist.WaitlistFactory;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.cpa.callstack.CallstackState;
import org.sosy_lab.cpachecker.cpa.predicate.PredicateAbstractState;
import org.sosy_lab.cpachecker.util.AbstractStates;


public class PartitionedReachedSetSearchInfo extends PartitionedReachedSet implements SearchInfoReachedSet {

  private int nOfVars;

  private SearchStrategyFormula searchForm;

  public PartitionedReachedSetSearchInfo(WaitlistFactory pWaitlistFactory, int nVars, Class<? extends SearchStrategyFormula> pSSForm) {
    super(pWaitlistFactory);
    // TODO Auto-generated constructor stub
    nOfVars = nVars;

    assert pSSForm != null : "pSSForm must not be null!";

    try {
      searchForm = Classes.createInstance(SearchStrategyFormula.class, pSSForm,
          new Class<?>[] {Integer.class},
          new Object[] {nOfVars});
    } catch (InvalidConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void add(AbstractState pState, Precision pPrecision) {
    super.add(pState, pPrecision);
    assert pState instanceof SearchInfoable : "given state must be a SearchInfoable";
    makeSearchInfo(pState);
  }

  //temporal solution... SearchInfo should be ARG base! (SimpleSearchInfo)
  @Override
  public void makeSearchInfo(AbstractState pState) {
    // TODO Auto-generated method stub
    assert pState instanceof SearchInfoable : "given state must be a SearchInfoable";
    SearchInfoable tSIState = (SearchInfoable)pState;

    //TODO SeachInfo should be calculated in here
    ARGState tARGState = AbstractStates.extractStateByType(pState, ARGState.class);
    assert tARGState != null : "extractStateByType is failed! (ARGState)";

    int tDep = tARGState.getTreeDepth();
    int tRPOrder = AbstractStates.extractLocation(pState).getReversePostorderId();

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
    newInfo.getInfos().put("TreeDepth", tDep);
    newInfo.getInfos().put("RPOrder", tRPOrder);
    newInfo.getInfos().put("CallStack", tCStack);
    newInfo.getInfos().put("BlkDepth", tBlkDepth);

    assert newInfo.getInfos().size() == nOfVars : "number of variables and size of info list should be same";

    tSIState.setSearchInfo(newInfo);
  }

}
