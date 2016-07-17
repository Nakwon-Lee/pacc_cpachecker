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
package org.sosy_lab.cpachecker.core.searchstrategy;

import java.util.List;

import org.sosy_lab.cpachecker.core.defaults.AbstractSearchStrategyFormula;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfo;


public class BFSearchStrategyFormula extends AbstractSearchStrategyFormula {

  public BFSearchStrategyFormula(Integer nOfVars){

    super();

    for (int i=0;i < nOfVars; i++){
      correlations.add(-1);
    }
    correlations.add(0);
  }

  @Override
  public List<Integer> getCorrelations(){
    return correlations;
  }

  @Override
  public int calcSearchFitness(SearchInfo<Integer> pSinfo) {
    assert correlations.size() == pSinfo.getInfos().size()+1 : "number of variables must be same";
    int result = 0;

    for (int i=0;i < correlations.size()-1;i++) {
      result = result + ( correlations.get(i) * pSinfo.getInfos().get(i) );
    }

    result = result + correlations.get(correlations.size()-1);

    return result;
   }

}
