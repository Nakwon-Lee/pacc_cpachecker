/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
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

import java.util.Comparator;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;

public class WARGState implements Comparable<WARGState>{

    private ARGState state;
    private Comparator<WARGState> comp;

    public WARGState(ARGState e, Comparator<WARGState> pcomp) {
      state = e;
      comp = pcomp;
    }

    public ARGState getState() {
      return state;
    }

    @Override
    public int compareTo(WARGState pArg0) {
      return comp.compare(this, pArg0);
    }

    @Override
    public final boolean equals(Object pObj) {
      if(pObj instanceof WARGState) {
        if(this.compareTo((WARGState) pObj) == 0) {
          return true;
        }else {
          return false;
        }
      }else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      // TODO Auto-generated method stub
      return super.hashCode();
    }
}
