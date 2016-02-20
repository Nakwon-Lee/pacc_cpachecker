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
package org.sosy_lab.cpachecker.util.snapshot;


public class Fitness {
  public long eachRunTime;
  public int nOfRefinements;
  public int nOfAttemptedFC;
  public int nOfSuccessfulFC;
  public boolean refinementSuccessful;

  public Fitness(){
    eachRunTime = 0;
    nOfRefinements = 0;
    nOfAttemptedFC = 0;
    nOfSuccessfulFC = 0;
    refinementSuccessful = false;
  }

  public Fitness(Fitness pFitness){
    eachRunTime = pFitness.eachRunTime;
    nOfRefinements = pFitness.nOfRefinements;
    nOfAttemptedFC = pFitness.nOfAttemptedFC;
    nOfSuccessfulFC = pFitness.nOfSuccessfulFC;
    refinementSuccessful = false;
  }

  public void printFitness(){
    System.out.println("Fitness: "+ eachRunTime + ", " + nOfRefinements+", "+nOfAttemptedFC+", "+nOfSuccessfulFC);
  }
}
