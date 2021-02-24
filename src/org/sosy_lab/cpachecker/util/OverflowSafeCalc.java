// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.util;

public final class OverflowSafeCalc {
  public static Integer add(Integer left, Integer right) {
    Integer sum;

    try {
      sum = Math.addExact(left, right);
    } catch (ArithmeticException e) {
      sum = Integer.MAX_VALUE;
    }

    return sum;
  }
}
