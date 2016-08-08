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

import org.sosy_lab.cpachecker.core.defaults.AbstractSearchStrategyFormula;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfo;

public class DFSearchStrategyFormula extends AbstractSearchStrategyFormula {

  public DFSearchStrategyFormula(Integer nOfVars){
    super();
    correlations.put("TreeDepth", 1);
  }

  @Override
  public int compare(SearchInfo<String, Integer> pO1, SearchInfo<String, Integer> pO2) {
    Integer ret = 0;

    if (pO1.getInfos().get("CallStack") > pO2.getInfos().get("CallStack")){
      ret = 1;
    }else if (pO1.getInfos().get("CallStack") < pO2.getInfos().get("CallStack")){
      ret = -1;
    }else{
      if (pO1.getInfos().get("TreeDepth") > pO2.getInfos().get("TreeDepth")){
        ret = 1;
      }else if (pO1.getInfos().get("TreeDepth") < pO2.getInfos().get("TreeDepth")){
        ret = -1;
      }
    }
    return ret;
  }
}
