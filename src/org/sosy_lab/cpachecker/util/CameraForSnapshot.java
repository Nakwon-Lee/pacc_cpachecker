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
package org.sosy_lab.cpachecker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSetCloneable;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSetList;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.cpa.arg.ARGUtils;

public class CameraForSnapshot {
  public static ReachedSetCloneable takeSnapshot(ReachedSetCloneable pReached) throws Exception{

    ReachedSetCloneable clonedReached = null;

    clonedReached = pReached.clone();

    AbstractState clonedRoot;

    Set<ARGState> tset = ARGUtils.getRootStates(pReached);

    AbstractState root;

    root = tset.iterator().next();

    assert tset.size() == 1:"roots must be only one";

    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      oos = new ObjectOutputStream(bos);

      oos.writeObject(root);
      oos.flush();

      ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
      ois = new ObjectInputStream(bin);

      clonedRoot = (AbstractState)(ois.readObject());

    } catch (Exception e) {
        // TODO Auto-generated catch block
      e.printStackTrace();
    }finally{
      oos.close();
      ois.close();
    }

    return clonedReached;
  }

  public static ReachedSetList takeSnapshot(ReachedSetList pReachedList){
    return null;
  }

  private void DFS(){

  }

}
