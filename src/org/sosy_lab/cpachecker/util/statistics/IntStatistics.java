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
  private double sumOfSquareCompensation; // Low order bits of sum
  private double simpleSumOfSquare; // Used to compute right sum for non-finite inputs

  @Override
  public void accept(int value) {
    super.accept(value);
    double squareValue = value * value;
    simpleSumOfSquare += squareValue;
    sumOfSquareWithCompensation(squareValue);
  }

  public IntStatistics combine(IntStatistics other) {
    super.combine(other);
    simpleSumOfSquare += other.simpleSumOfSquare;
    sumOfSquareWithCompensation(other.sumOfSquare);
    sumOfSquareWithCompensation(other.sumOfSquareCompensation);
    return this;
  }

  private void sumOfSquareWithCompensation(double value) {
    double tmp = value - sumOfSquareCompensation;
    double velvel = sumOfSquare + tmp; // Little wolf of rounding error
    sumOfSquareCompensation = (velvel - sumOfSquare) - tmp;
    sumOfSquare = velvel;
  }

  public double getSumOfSquare() {
    double tmp = sumOfSquare + sumOfSquareCompensation;
    if (Double.isNaN(tmp) && Double.isInfinite(simpleSumOfSquare)) {
      return simpleSumOfSquare;
    }
    return tmp;
  }

  public final double getStandardDeviation() {
    return getCount() > 0
        ? Math.sqrt((getSumOfSquare() / getCount()) - Math.pow(getAverage(), 2))
        : 0.0d;
  }
}
