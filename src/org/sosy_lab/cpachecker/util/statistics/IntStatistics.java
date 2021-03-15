// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.util.statistics;

import java.util.IntSummaryStatistics;

public class IntStatistics extends IntSummaryStatistics {
  private double sumOfSquare = 0.0d;

  @Override
  public void accept(int value) {
    super.accept(value);
    double squareValue = value * value;
    sumOfSquare += squareValue;
  }

  public IntStatistics combine(IntStatistics other) {
    super.combine(other);
    sumOfSquare += other.sumOfSquare;
    return this;
  }

  public double getSumOfSquare() {
    return sumOfSquare;
  }

  public final double getStandardDeviation() {
    return getCount() > 0
        ? Math.sqrt(Math.abs((getSumOfSquare() / getCount()) - Math.pow(getAverage(), 2)))
        : 0.0d;
  }
}
