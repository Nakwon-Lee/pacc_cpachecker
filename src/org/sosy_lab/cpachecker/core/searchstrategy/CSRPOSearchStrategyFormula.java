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

import java.util.Set;

import org.sosy_lab.cpachecker.core.defaults.AbstractSearchStrategyFormula;
import org.sosy_lab.cpachecker.core.defaults.SimpleSearchInfo;
import org.sosy_lab.cpachecker.core.interfaces.SearchInfo;


public class CSRPOSearchStrategyFormula extends AbstractSearchStrategyFormula {

  private static final String[] varsUsed = {"isAbsSt","BlkDepth","CallStack","RPOrder","TreeDepth"};

  public CSRPOSearchStrategyFormula(Set<String> pVars){
    super(pVars, varsUsed);
  }

  @Override
  public int compare(SearchInfo pO1, SearchInfo pO2) {
    Integer ret = 0;

    assert pO1 instanceof SimpleSearchInfo : "parameters must be SimpleSearchInfo";
    assert pO2 instanceof SimpleSearchInfo : "parameters must be SimpleSearchInfo";

    SimpleSearchInfo spO1 = (SimpleSearchInfo)pO1;
    SimpleSearchInfo spO2 = (SimpleSearchInfo)pO2;

    if (spO1.getInfos().get("CallStack") > spO2.getInfos().get("CallStack")){
      ret = 1;
    }else if (spO1.getInfos().get("CallStack") < spO2.getInfos().get("CallStack")){
      ret = -1;
    }else{
      if (spO1.getInfos().get("RPOrder") > spO2.getInfos().get("RPOrder")){
        ret = 1;
      }else if (spO1.getInfos().get("RPOrder") < spO2.getInfos().get("RPOrder")){
        ret = -1;
        }
      }
    return ret;
  }
}
