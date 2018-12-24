/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
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
package org.sosy_lab.cpachecker.core.waitlist;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.LinkedList;
import java.util.Random;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;

/** Waitlist implementation that considers states in a random order for pop(). */
@SuppressFBWarnings(
  value = "BC_BAD_CAST_TO_CONCRETE_COLLECTION",
  justification = "warnings is only because of casts introduced by generics"
)
@SuppressWarnings("JdkObsolete")
@Options(prefix = "analysis.traversal.randwaitlist")
public class RandomWaitlistSeed extends AbstractWaitlist<LinkedList<AbstractState>> {

  private static final long serialVersionUID = 1L;

  @Option(secure = true, description = "Seed for random values.")
  private long seed = 0;

  private Random rand;

  private Configuration config;

  protected RandomWaitlistSeed(Configuration pConfig) throws InvalidConfigurationException {
    super(new LinkedList<>());
    pConfig.inject(this, RandomWaitlistSeed.class);
    config = pConfig;
    rand = new Random(seed);
  }

  @Override
  public AbstractState pop() {
    int r = rand.nextInt(waitlist.size());
    return waitlist.remove(r);
  }

  public static WaitlistFactory factory(Configuration pConfig) {
    return () -> {
      try {
        return new RandomWaitlistSeed(pConfig);

      } catch (InvalidConfigurationException pE) {
        throw new AssertionError(pE);
      }
    };
  }
}
