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
import org.sosy_lab.cpachecker.cpa.arg.ARGState;


public class ABESearchStrategyFormula extends AbstractSearchStrategyFormula {

  private static final String[] varsUsed = {"isAbs","blkD","CS","RPO","uID"};

  public ABESearchStrategyFormula(Set<String> pVars){
    super(pVars, varsUsed);
  }

  @Override
  public int compare(WARGState p1, WARGState p2) {

    ARGState e1 = p1.getState();
    ARGState e2 = p2.getState();

// compare start 
if(e1.isAbs() < 1 && e2.isAbs() >= 1 ){
 return -1;
}
else if(e1.isAbs() >= 1 && e2.isAbs() < 1 ){
 return 1;
}
else{
int thePhi = e1.isAbs();
if(thePhi<1){
 if(e1.CS() < e2.CS()){
  return 1;
 }
 else if(e1.CS() > e2.CS()){
  return -1;
 }
 else{
  if(e1.RPO() < e2.RPO()){
   return 1;
  }
  else if(e1.RPO() > e2.RPO()){
   return -1;
  }
  else{
   return 0;
  }
 }
 }
 else {
 if(e1.loopD() < e2.loopD()){
  return 1;
 }
 else if(e1.loopD() > e2.loopD()){
  return -1;
 }
 else{
  if(e1.CS() < e2.CS()){
   return 1;
  }
  else if(e1.CS() > e2.CS()){
   return -1;
  }
  else{
   if(e1.RPO() < e2.RPO()){
    return 1;
   }
   else if(e1.RPO() > e2.RPO()){
    return -1;
   }
   else{
    return 0;
   }
  }
 }
}
}
// compare end

  }

}
