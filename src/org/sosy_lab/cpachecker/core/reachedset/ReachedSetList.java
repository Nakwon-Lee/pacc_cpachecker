/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2015  Dirk Beyer
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

import java.util.Iterator;
import java.util.LinkedList;

import org.sosy_lab.cpachecker.util.snapshot.Fitness;
import org.sosy_lab.cpachecker.util.snapshot.Pair;

public class ReachedSetList extends LinkedList<Pair<ReachedSetCloneable, Fitness>> implements Comparable<ReachedSetList> {

  private static final long serialVersionUID = 4196096771940032943L;

  @Override
  //return the negative integer value when this instance is more fitted than the given instance
  public int compareTo(ReachedSetList pO) {
    Pair<ReachedSetCloneable, Fitness> thisSet = this.getLast();
    Pair<ReachedSetCloneable, Fitness> pOSet = pO.getLast();

    //need to match the snapshot number
    if(thisSet.number > pOSet.number){
      Iterator<Pair<ReachedSetCloneable, Fitness>> it = this.descendingIterator();
      while(it.hasNext()){
        Pair<ReachedSetCloneable, Fitness> curr = it.next();
        if(pOSet.number == curr.number){
          thisSet = curr;
          break;
        }
      }
    }else if(thisSet.number < pOSet.number) {
      Iterator<Pair<ReachedSetCloneable, Fitness>> it = pO.descendingIterator();
      while(it.hasNext()){
        Pair<ReachedSetCloneable, Fitness> curr = it.next();
        if(thisSet.number == curr.number){
          pOSet = curr;
          break;
        }
      }
    }

    assert thisSet.number == pOSet.number : "two reachedsets must have pair of having same number";

    return compareFitness(thisSet.right, pOSet.right);
  }

  private int compareFitness(Fitness pThis, Fitness pO){
    if(pThis.refinementSuccessful && pO.refinementSuccessful){ //both are true
      return 0;
    }else if(pThis.refinementSuccessful && !pO.refinementSuccessful){
      return -1;
    }else if(!pThis.refinementSuccessful && pO.refinementSuccessful){
      return 1;
    }

    //if it reach here, both refinementSuccessful are false
    if(pThis.nOfRefinements < pO.nOfRefinements){
      return -1;
    }else if(pThis.nOfRefinements > pO.nOfRefinements){
      return 1;
    }

    //if it reach here, both nOfRefienemnts are same
    if( ){

    }
    return 0;
  }
}
