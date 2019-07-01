/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
 */
package org.sosy_lab.cpachecker.cfa.model;

import java.util.Collections;
import java.util.Comparator;

public class DEndComparator implements Comparator<CFANode> {

  @Override
  public int compare(CFANode pArg0, CFANode pArg1) {
    int arg0min = Integer.MAX_VALUE;
    int arg1min = Integer.MAX_VALUE;
    if (!pArg0.getDistancetoendList().isEmpty()) {
      arg0min = Collections.min(pArg0.getDistancetoendList());
    }
    if (!pArg1.getDistancetoendList().isEmpty()) {
      arg1min = Collections.min(pArg1.getDistancetoendList());
    }

    return Integer.compare(arg1min, arg0min);
  }

}
