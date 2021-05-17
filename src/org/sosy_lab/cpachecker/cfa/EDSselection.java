// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cfa;

public class EDSselection {
  public static DistanceScheme selection(EDSfeatures pfts) {
    DistanceScheme ret = DistanceScheme.BASICBLOCKS;
// rule start 
if(pfts.FUNCS<847){
 if(pfts.VARSINC>=11.5){
  if(pfts.AVNDFN>=19.60978423){
   if(pfts.AVEGFN<33.16461539){
    ret = DistanceScheme.STATEMENTS;
   }
   else{
    ret = DistanceScheme.LOOPHEADS;
   }
  }
  else{
   ret = DistanceScheme.LOOPSANDFUNCS;
  }
 }
 else{
  if(pfts.AVNDFN>=39.05386307){
   ret = DistanceScheme.STATEMENTS;
  }
  else{
   if(pfts.MXCC>=39){
    if(pfts.MXNDFN<1249.5){
     ret = DistanceScheme.BASICBLOCKS;
    }
    else{
     if(pfts.VARSLOOP<20.5){
      ret = DistanceScheme.BASICBLOCKS;
     }
     else{
      ret = DistanceScheme.LOOPHEADS;
     }
    }
   }
   else{
    if(pfts.MXCC<26.5){
     ret = DistanceScheme.BASICBLOCKS;
    }
    else{
     ret = DistanceScheme.LOOPSANDFUNCS;
    }
   }
  }
 }
}
else{
 ret = DistanceScheme.LOOPSANDFUNCS;
}
    // rule end
    return ret;
  }
}
