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
package org.sosy_lab.cpachecker.core.defaults;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.sosy_lab.cpachecker.core.interfaces.SearchInfo;


public class SimpleSearchInfo implements SearchInfo<String, Integer> {

  private final LinkedHashMap<String, Integer> vars;

  private final Comparator<SearchInfo<String, Integer>> searchForm;

  public SimpleSearchInfo(Comparator<SearchInfo<String, Integer>> pSForm) {
    // TODO Auto-generated constructor stub
    vars = new LinkedHashMap<>();
    searchForm = pSForm;
  }

   @Override
  public String toString(){
     String str = "";

     for(Entry<String, Integer> ent : vars.entrySet()){
       str = str.concat(ent.getKey()+": "+ent.getValue()+" ");
     }

     return str;
   }

  @Override
  public LinkedHashMap<String, Integer> getInfos() {
    // TODO Auto-generated method stub
    return vars;
  }

  @Override
  public int compareTo(SearchInfo<String, Integer> pO) {
    // TODO Auto-generated method stub
    return searchForm.compare(this, pO);
  }

}
