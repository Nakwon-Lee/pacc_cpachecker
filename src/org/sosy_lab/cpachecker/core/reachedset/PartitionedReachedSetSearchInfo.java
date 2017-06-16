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
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.SearchStrategyFormula;
import org.sosy_lab.cpachecker.core.waitlist.Waitlist.WaitlistFactory;


public class PartitionedReachedSetSearchInfo extends PartitionedReachedSet{

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
  }

}
