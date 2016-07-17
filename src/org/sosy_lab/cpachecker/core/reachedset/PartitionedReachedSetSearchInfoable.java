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

import java.util.Collection;
import java.util.LinkedHashMap;

import org.sosy_lab.cpachecker.core.defaults.SimpleSearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfo;
import org.sosy_lab.cpachecker.core.waitlist.Waitlist.WaitlistFactory;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.cpa.arg.ARGUtils;
import org.sosy_lab.cpachecker.util.AbstractStates;

public class PartitionedReachedSetSearchInfoable extends PartitionedReachedSet implements SearchInfoReachedSet {

  private final LinkedHashMap<AbstractState, SearchInfo<Integer>> searchInfoReached;

  public PartitionedReachedSetSearchInfoable(WaitlistFactory pWaitlistFactory) {
    super(pWaitlistFactory);
    // TODO Auto-generated constructor stub
    searchInfoReached = new LinkedHashMap<>();
  }

  @Override
  public void add(AbstractState pState, Precision pPrecision) {
    super.add(pState, pPrecision);

    SearchInfo<Integer> tSearchInfo = makeSearchInfo(pState);

    searchInfoReached.put(pState, tSearchInfo);
  }

  @Override
  public void remove(AbstractState pState) {
    super.remove(pState);

    searchInfoReached.remove(pState);
  }

  @Override
  public void clear() {
    super.clear();

    searchInfoReached.clear();
  }

  @Override
  public SearchInfo<Integer> getSearchInfo(AbstractState pState){
    return searchInfoReached.get(pState);
  }

  //temporal solution... SearchInfo should be ARG base! (SimpleSearchInfo)
  @Override
  public SearchInfo<Integer> makeSearchInfo(AbstractState pState) {
    //TODO SeachInfo should be calculated in here
    ARGState tARGState = AbstractStates.extractStateByType(pState, ARGState.class);

    assert tARGState != null : "extractStateByType is failed!";

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
    SimpleSearchInfo newInfo = new SimpleSearchInfo();
    newInfo.getInfos().add(tDep);

    return newInfo;
  }

}
