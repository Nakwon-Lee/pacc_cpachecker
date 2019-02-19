/*
 * CPAchecker is a tool for configurable software verification.
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
package org.sosy_lab.cpachecker.util.predicates.interpolation.strategy;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.common.time.Timer;
import org.sosy_lab.cpachecker.cpa.predicate.BlockFormulaStrategy.BlockFormulas;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.Solver;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.FloatingPointRoundingModeFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaType;
import org.sosy_lab.java_smt.api.FormulaType.ArrayFormulaType;
import org.sosy_lab.java_smt.api.InterpolatingProverEnvironment;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.NumeralFormula.RationalFormula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

@SuppressWarnings("rawtypes")
public class DomainSpecificAbstraction<T> {
  protected final FormulaManagerView fmgr;
  protected FormulaManagerView oldFmgr;
  private String[] arrayVariablesThatAreUsedInBothParts;
  private String[] arrayVariablesThatAreNotUsedInBothParts;
  private List<BooleanFormula> formulas;
  private final Timer findingCommonVariablesTimer;
  private final Timer buildingLatticeNamesAndLatticeTypesTimer;
  private final Timer renamingTimer;
  private final Timer buildingAbstractionsTimer;
  private final Timer interpolationTimer;
  private final Timer initialVariableExtractionTimer;
  private final Timer feasibilityCheckTimer;
  private final Timer maximisationTimer;
  LogManager logger;
  private HashMap<String, FormulaType> latticeNamesTypes = new HashMap<>();
  protected boolean inequalityInterpolationAbstractions;

  public DomainSpecificAbstraction(
                                   FormulaManagerView pFmgr,
                                   FormulaManagerView oldFmgr0,
                                     LogManager pLogger, Timer pFindingCommonVariablesTimer,
                                   Timer pBuildingLatticeNamesAndLatticeTypesTimer, Timer
                                       pRenamingTimer, Timer pBuildingAbstractionsTimer, Timer
                                       pInterpolationTimer, Timer
                                       pInitialVariableExtractionTimer, Timer
                                       pFeasibilityCheckTimer, Timer pMaximisationTimer, boolean
                                       pInequalityInterpolationAbstractions) {
    fmgr = pFmgr;
    oldFmgr = oldFmgr0;
    logger = pLogger;
    findingCommonVariablesTimer = pFindingCommonVariablesTimer;
    buildingLatticeNamesAndLatticeTypesTimer = pBuildingLatticeNamesAndLatticeTypesTimer;
    renamingTimer = pRenamingTimer;
    buildingAbstractionsTimer = pBuildingAbstractionsTimer;
    interpolationTimer = pInterpolationTimer;
    initialVariableExtractionTimer = pInitialVariableExtractionTimer;
    feasibilityCheckTimer = pFeasibilityCheckTimer;
    maximisationTimer = pMaximisationTimer;
    inequalityInterpolationAbstractions = pInequalityInterpolationAbstractions;
  }


  @SuppressWarnings({"rawtypes", "unchecked"})
  public List<BooleanFormula> domainSpecificAbstractionsCheck(
      Solver mySolver,
      List<BooleanFormula> oldFormulas)
      throws SolverException, InterruptedException {


    ProverEnvironment prover = mySolver
        .newProverEnvironment(ProverOptions.GENERATE_MODELS);
    List<BooleanFormula> interpolants = Lists.newArrayListWithExpectedSize(oldFormulas.size()
        - 1);
    for (int it = 0; it < oldFormulas.size() - 1; it = it + 1) {
      BooleanFormula oldInterpolant;
      formulas = Lists.newArrayListWithExpectedSize(oldFormulas.size
          ());
      final List<Set<String>> variablesInFormulas =
          Lists.newArrayListWithExpectedSize(formulas.size());

      final List<Map<String, Formula>> variableTypes =
          Lists.newArrayListWithExpectedSize(oldFormulas.size()
              - 1);
      initialVariableExtractionTimer.start();
      try {
        if (it == 0) {
          formulas.add(oldFormulas.get(it));
          formulas.add(oldFormulas.get(it + 1));
        } else {
          oldInterpolant = oldFmgr.translateFrom(interpolants.get(it - 1), fmgr);
          formulas.add(oldInterpolant);
          formulas.add(oldFormulas.get(it));
          formulas.add(oldFormulas.get(it + 1));
          variablesInFormulas.add(oldFmgr.extractVariableNames(oldInterpolant));
          variableTypes.add(oldFmgr.extractVariables(oldInterpolant));
        }

        for (int i = it; i < oldFormulas.size(); i++) {
          variablesInFormulas.add(oldFmgr.extractVariableNames(oldFormulas.get(i)));
          variableTypes.add(oldFmgr.extractVariables(oldFormulas.get(i)));
        }
      } finally {
        initialVariableExtractionTimer.stop();
      }
      List<List<Formula>> frontierList = Lists.newArrayListWithExpectedSize(formulas.size
          ());
      Set<String> variables1 = Sets.newHashSet();
      Set<String> variables2 = Sets.newHashSet();
      findingCommonVariablesTimer.start();
      try {
        if (it == 0) {
          variables1 = variablesInFormulas.get(0);
          for (int i = 1; i < variablesInFormulas.size(); i++) {
            for (String f : variablesInFormulas.get(i)) {
              variables2.add(f);
            }
          }
        } else {
          for (String f : variablesInFormulas.get(0)) {
            variables1.add(f);
          }
          for (String f : variablesInFormulas.get(1)) {
            variables1.add(f);
          }
          for (int i = 2; i < variablesInFormulas.size(); i++) {
            for (String f : variablesInFormulas.get(i)) {
              variables2.add(f);
            }
          }
        }
      } finally {
        findingCommonVariablesTimer.stop();
      }
      Set<String> variablesThatAreUsedInBothParts = Sets.intersection(variables1, variables2)
          .immutableCopy();
      Set<String> variablesThatAreNotUsedInBothParts = Sets.difference(variables1, variables2)
          .immutableCopy();
      HashMap<String, FormulaType> variablesUsedInBothPartsClasses = new HashMap<>();

      arrayVariablesThatAreUsedInBothParts = variablesThatAreUsedInBothParts.toArray(new
          String[variablesThatAreUsedInBothParts.size
          ()]);
      arrayVariablesThatAreNotUsedInBothParts = variablesThatAreNotUsedInBothParts.toArray(new
          String[variablesThatAreNotUsedInBothParts.size
          ()]);


      for (int i = 0; i < arrayVariablesThatAreUsedInBothParts.length; i++) {
        Formula helperFormula;
        FormulaType helperFormulaType;
        for (Map<String, Formula> f : variableTypes) {
          helperFormula = f.get(arrayVariablesThatAreUsedInBothParts[i]);
          if (helperFormula != null) {
            helperFormulaType = oldFmgr.getFormulaType(helperFormula);
            variablesUsedInBothPartsClasses.put(arrayVariablesThatAreUsedInBothParts[i],
                helperFormulaType);
          }
        }
      }
      String[] latticeNames = new String[arrayVariablesThatAreUsedInBothParts.length];
      List<BooleanFormula> relationAbstraction1Formula =
          Lists.newArrayListWithExpectedSize(variablesThatAreUsedInBothParts
              .size());
      List<BooleanFormula> relationAbstraction2Formula =
          Lists.newArrayListWithExpectedSize(variablesThatAreUsedInBothParts
              .size());


      String[] relationAbstraction1 = new String[variablesThatAreUsedInBothParts.size()];
      String[] relationAbstraction2 = new String[variablesThatAreUsedInBothParts.size()];

      buildingLatticeNamesAndLatticeTypesTimer.start();
      try {

        if ((arrayVariablesThatAreUsedInBothParts.length % 2) == 0) {
          for (int i = 0; i < arrayVariablesThatAreUsedInBothParts.length; i = i + 2) {
            FormulaType resultType1 = variablesUsedInBothPartsClasses.get
                (arrayVariablesThatAreUsedInBothParts[i]);
            FormulaType resultType2 = variablesUsedInBothPartsClasses.get
                (arrayVariablesThatAreUsedInBothParts[i + 1]);
            if (!resultType1.equals(resultType2)) {
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "#";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              if (resultType1.isArrayType()){
                ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
                //FormulaType indexType1 = resultType1Array.getIndexType();
                //FormulaType elementType1 = resultType1Array.getElementType();
                //helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                //    indexType1),
                //    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula1 = fmgr.makeVariable(resultType1Array,
                    arrayVariablesThatAreUsedInBothParts[i]);
               // helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                //    indexType1),
                //    arrayVariablesThatAreUsedInBothParts[i] + "#");
                helperFormula7 = fmgr.makeVariable(resultType1Array,
                        arrayVariablesThatAreUsedInBothParts[i] + "#");
              } else if (resultType1.isBitvectorType()){
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
                int size = bitv.getSize();
                helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula7 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
              } else {
                helperFormula1 = fmgr.makeVariable(resultType1,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula7 = fmgr.makeVariable(resultType1,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
              }
              if (resultType2.isArrayType()){
                ArrayFormulaType resultType2Array = (ArrayFormulaType) resultType2;
                /*helperFormula2 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                    indexType2),
                    arrayVariablesThatAreUsedInBothParts[i+1]); */
                helperFormula2 = fmgr.makeVariable(resultType2Array,
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                /*helperFormula8 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                    indexType2),
                    arrayVariablesThatAreUsedInBothParts[i+1] + "#"); */
                helperFormula8 = fmgr.makeVariable(resultType2Array,
                    arrayVariablesThatAreUsedInBothParts[i+1] + "#");
              } else if (resultType2.isBitvectorType()){
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType2;
                int size = bitv.getSize();
                helperFormula2 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                helperFormula8 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i+1] + "#");
              } else {
                helperFormula2 = fmgr.makeVariable(resultType2,
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                helperFormula8 = fmgr.makeVariable(resultType2,
                    arrayVariablesThatAreUsedInBothParts[i+1] + "#");
              }
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType2);
            } else if (resultType1.isArrayType() && resultType2.isArrayType()) {
              ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
              ArrayFormulaType resultType2Array = (ArrayFormulaType) resultType2;
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "#";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                  indexType1),
                  arrayVariablesThatAreUsedInBothParts[i]); */
              helperFormula1 = fmgr.makeVariable(resultType1Array,
                  arrayVariablesThatAreUsedInBothParts[i]);
              /*helperFormula2 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                  indexType2),
                  arrayVariablesThatAreUsedInBothParts[i + 1]); */
              helperFormula2 = fmgr.makeVariable(resultType2Array,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                  indexType1),
                  arrayVariablesThatAreUsedInBothParts[i] + "#"); */
              helperFormula7 = fmgr.makeVariable(resultType1Array,
                  arrayVariablesThatAreUsedInBothParts[i] + "#");
              /*helperFormula8 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                  indexType2),
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");*/
              helperFormula8 = fmgr.makeVariable(resultType2Array,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType1);
            } else if (resultType1.isBooleanType() && resultType2.isBooleanType()) {
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "#";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              BooleanFormula helperFormula1, helperFormula2, helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i] + "#");
              helperFormula8 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType1);
            } else if (resultType1.isBitvectorType() &&
                resultType2.isBitvectorType()) {
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
                int size = bitv.getSize();
                FormulaType.BitvectorType bitv2 = (FormulaType.BitvectorType) resultType2;
                int size2 = bitv2.getSize();
                Formula helperFormula1, helperFormula2,
                    helperFormula7, helperFormula8;
                BooleanFormula helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula7 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
                helperFormula8 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
              if (!inequalityInterpolationAbstractions) {
                helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
                helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                    arrayVariablesThatAreUsedInBothParts[i] + "#";
                relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              } else {
                helperFormula5 = fmgr.makeLessOrEqual(helperFormula2, helperFormula8, true);
                helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                    arrayVariablesThatAreUsedInBothParts[i] + "#";
                relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              }
                relationAbstraction1Formula.add(helperFormula6);
                relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType1);
            } else if (resultType1 == resultType2
                && !resultType1.isIntegerType()
                && !resultType1.isFloatingPointRoundingModeType()
                && !resultType1.isRationalType()) {
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "#";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(resultType1,
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(resultType2,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(resultType1,
                  arrayVariablesThatAreUsedInBothParts[i] + "#");
              helperFormula8 = fmgr.makeVariable(resultType2,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType2);
            } else {
              if (resultType1.isIntegerType() && resultType2.isIntegerType()) {
                IntegerFormula helperFormula1, helperFormula2, helperFormula3, helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
                helperFormula8 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                }
                relationAbstraction1Formula.add(helperFormula6);
                relationAbstraction1Formula.add(helperFormula5);
              } else if (resultType1.isRationalType() && resultType2.isRationalType()) {
                RationalFormula helperFormula1, helperFormula2, helperFormula3, helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
                helperFormula8 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                }
                relationAbstraction1Formula.add(helperFormula6);
                relationAbstraction1Formula.add(helperFormula5);
              } else if (resultType1.isFloatingPointRoundingModeType() &&
                  resultType2.isFloatingPointRoundingModeType()) {
                FloatingPointRoundingModeFormula helperFormula1, helperFormula2, helperFormula3,
                    helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
                helperFormula8 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                }
                relationAbstraction1Formula.add(helperFormula6);
                relationAbstraction1Formula.add(helperFormula5);
              }
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType1);
            }
          }
          for (int i = 0; i < arrayVariablesThatAreUsedInBothParts.length; i = i + 2) {
            FormulaType resultType1 = variablesUsedInBothPartsClasses.get
                (arrayVariablesThatAreUsedInBothParts[i]);
            FormulaType resultType2 = variablesUsedInBothPartsClasses.get
                (arrayVariablesThatAreUsedInBothParts[i + 1]);
            if (!resultType1.equals(resultType2)) {
              relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              if (resultType1.isArrayType()){
                ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
                /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                    indexType1),
                    arrayVariablesThatAreUsedInBothParts[i]); */
                helperFormula1 = fmgr.makeVariable(resultType1Array,
                    arrayVariablesThatAreUsedInBothParts[i]);
                /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                    indexType1),
                    arrayVariablesThatAreUsedInBothParts[i] + "##"); */
                helperFormula7 = fmgr.makeVariable(resultType1Array,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
              } else if (resultType1.isBitvectorType()){
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
                int size = bitv.getSize();
                helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula7 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
              } else {
                helperFormula1 = fmgr.makeVariable(resultType1,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula7 = fmgr.makeVariable(resultType1,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
              }
              if (resultType2.isArrayType()){
                ArrayFormulaType resultType2Array = (ArrayFormulaType) resultType2;
                /*helperFormula2 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                    indexType2),
                    arrayVariablesThatAreUsedInBothParts[i+1]); */
                helperFormula2 = fmgr.makeVariable(resultType2Array,
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                /*helperFormula8 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                    indexType2),
                    arrayVariablesThatAreUsedInBothParts[i+1] + "##");*/
                helperFormula8 = fmgr.makeVariable(resultType2Array,
                    arrayVariablesThatAreUsedInBothParts[i+1] + "##");
              } else if (resultType2.isBitvectorType()){
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType2;
                int size = bitv.getSize();
                helperFormula2 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                helperFormula8 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i+1] + "##");
              } else {
                helperFormula2 = fmgr.makeVariable(resultType2,
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                helperFormula8 = fmgr.makeVariable(resultType2,
                    arrayVariablesThatAreUsedInBothParts[i+1] + "##");
              }
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else if (resultType1.isArrayType() && resultType2.isArrayType()) {
              ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
              ArrayFormulaType resultType2Array = (ArrayFormulaType) resultType2;
              relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                  indexType1),
                  arrayVariablesThatAreUsedInBothParts[i]); */
              helperFormula1 = fmgr.makeVariable(resultType1Array,
                  arrayVariablesThatAreUsedInBothParts[i]);
              /*helperFormula2 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                  indexType2),
                  arrayVariablesThatAreUsedInBothParts[i + 1]);*/
              helperFormula2 = fmgr.makeVariable(resultType2Array,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                  indexType1),
                  arrayVariablesThatAreUsedInBothParts[i] + "##");*/
              helperFormula7 = fmgr.makeVariable(resultType1Array,
                  arrayVariablesThatAreUsedInBothParts[i] + "##");
              /*helperFormula8 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                  indexType2),
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");*/
              helperFormula8 = fmgr.makeVariable(resultType2Array,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else if (resultType1.isBooleanType() && resultType2.isBooleanType()) {
              relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              BooleanFormula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i] + "##");
              helperFormula8 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else if (resultType1.isBitvectorType() &&
                resultType2.isBitvectorType()) {
              relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
              int size = bitv.getSize();
              FormulaType.BitvectorType bitv2 = (FormulaType.BitvectorType) resultType2;
              int size2 = bitv2.getSize();
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                  arrayVariablesThatAreUsedInBothParts[i] + "##");
              helperFormula8 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
              if (!inequalityInterpolationAbstractions) {
                helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
                helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                    arrayVariablesThatAreUsedInBothParts[i] + "##";
                relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              } else {
                helperFormula5 = fmgr.makeLessOrEqual(helperFormula2, helperFormula8, true);
                helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                    arrayVariablesThatAreUsedInBothParts[i] + "##";
                relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              }
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else if (resultType1 == resultType2
                && !resultType1.isIntegerType()
                && !resultType1.isFloatingPointRoundingModeType()
                && !resultType1.isRationalType()) {
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(resultType1,
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(resultType2,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(resultType1,
                  arrayVariablesThatAreUsedInBothParts[i] + "##");
              helperFormula8 = fmgr.makeVariable(resultType2,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else {
              if (resultType1.isIntegerType() && resultType2.isIntegerType()) {
                IntegerFormula helperFormula1, helperFormula2, helperFormula3, helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula
                    helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
                helperFormula8 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                }
                relationAbstraction2Formula.add(helperFormula6);
                relationAbstraction2Formula.add(helperFormula5);
              } else if (resultType1.isRationalType() && resultType2.isRationalType()) {
                RationalFormula helperFormula1, helperFormula2, helperFormula3, helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula
                    helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
                helperFormula8 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                }
                relationAbstraction2Formula.add(helperFormula6);
                relationAbstraction2Formula.add(helperFormula5);
              } else if (resultType1.isFloatingPointRoundingModeType() &&
                  resultType2.isFloatingPointRoundingModeType()) {
                FloatingPointRoundingModeFormula helperFormula1, helperFormula2, helperFormula3,
                    helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula
                    helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
                helperFormula8 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                }
                relationAbstraction2Formula.add(helperFormula6);
                relationAbstraction2Formula.add(helperFormula5);
              }
            }
          }
        } else {
          int i;
          for (i = 0; i < arrayVariablesThatAreUsedInBothParts.length - 1; i = i + 2) {
            FormulaType resultType1 = variablesUsedInBothPartsClasses.get
                (arrayVariablesThatAreUsedInBothParts[i]);
            FormulaType resultType2 = variablesUsedInBothPartsClasses.get
                (arrayVariablesThatAreUsedInBothParts[i + 1]);
            if (!resultType1.equals(resultType2)) {
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "#";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              if (resultType1.isArrayType()){
                ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
                /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                    indexType1),
                    arrayVariablesThatAreUsedInBothParts[i]); */
                helperFormula1 = fmgr.makeVariable(resultType1Array,
                    arrayVariablesThatAreUsedInBothParts[i]);
                /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                    indexType1),
                    arrayVariablesThatAreUsedInBothParts[i] + "#");*/
                helperFormula7 = fmgr.makeVariable(resultType1Array,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
              } else if (resultType1.isBitvectorType()){
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
                int size = bitv.getSize();
                helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula7 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
              } else {
                helperFormula1 = fmgr.makeVariable(resultType1,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula7 = fmgr.makeVariable(resultType1,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
              }
              if (resultType2.isArrayType()){
                ArrayFormulaType resultType2Array = (ArrayFormulaType) resultType2;
                /*helperFormula2 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                    indexType2),
                    arrayVariablesThatAreUsedInBothParts[i+1]);*/
                helperFormula2 = fmgr.makeVariable(resultType2Array,
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                /*helperFormula8 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                    indexType2),
                    arrayVariablesThatAreUsedInBothParts[i+1] + "#"); */
                helperFormula8 = fmgr.makeVariable(resultType2Array,
                    arrayVariablesThatAreUsedInBothParts[i+1] + "#");
              } else if (resultType2.isBitvectorType()){
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType2;
                int size = bitv.getSize();
                helperFormula2 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                helperFormula8 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i+1] + "#");
              } else {
                helperFormula2 = fmgr.makeVariable(resultType2,
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                helperFormula8 = fmgr.makeVariable(resultType2,
                    arrayVariablesThatAreUsedInBothParts[i+1] + "#");
              }
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType2);
            } else if (resultType1.isArrayType() && resultType2.isArrayType()) {
              ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
              ArrayFormulaType resultType2Array = (ArrayFormulaType) resultType2;
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "#";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                  indexType1),
                  arrayVariablesThatAreUsedInBothParts[i]); */
              helperFormula1 = fmgr.makeVariable(resultType1Array,
                  arrayVariablesThatAreUsedInBothParts[i]);
              /*helperFormula2 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                  indexType2),
                  arrayVariablesThatAreUsedInBothParts[i + 1]);*/
              helperFormula2 = fmgr.makeVariable(resultType2Array,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                  indexType1),
                  arrayVariablesThatAreUsedInBothParts[i] + "#");*/
              helperFormula7 = fmgr.makeVariable(resultType1Array,
                  arrayVariablesThatAreUsedInBothParts[i] + "#");
              /*helperFormula8 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                  indexType2),
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");*/
              helperFormula8 = fmgr.makeVariable(resultType2Array,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType1);
            } else if (resultType1.isBooleanType() && resultType2.isBooleanType()) {
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "#";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              BooleanFormula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i] + "#");
              helperFormula8 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType1);
            } else if (resultType1.isBitvectorType() &&
                resultType2.isBitvectorType()) {
              FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
              int size = bitv.getSize();
              FormulaType.BitvectorType bitv2 = (FormulaType.BitvectorType) resultType2;
              int size2 = bitv2.getSize();
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                  arrayVariablesThatAreUsedInBothParts[i] + "#");
              helperFormula8 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
              if (!inequalityInterpolationAbstractions) {
                helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
                helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                    arrayVariablesThatAreUsedInBothParts[i] + "#";
                relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              } else {
                helperFormula5 = fmgr.makeLessOrEqual(helperFormula2, helperFormula8, true);
                helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                    arrayVariablesThatAreUsedInBothParts[i] + "#";
                relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              }
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType1);
            } else if (resultType1 == resultType2
                && !resultType1.isIntegerType()
                && !resultType1.isFloatingPointRoundingModeType()
                && !resultType1.isRationalType()) {
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "#";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(resultType1,
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(resultType2,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(resultType1,
                  arrayVariablesThatAreUsedInBothParts[i] + "#");
              helperFormula8 = fmgr.makeVariable(resultType2,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction1Formula.add(helperFormula6);
              relationAbstraction1Formula.add(helperFormula5);
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] =
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType2);
            } else {
              if (resultType1.isIntegerType() && resultType2.isIntegerType()) {
                IntegerFormula helperFormula1, helperFormula2, helperFormula3, helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
                helperFormula8 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                }
                relationAbstraction1Formula.add(helperFormula6);
                relationAbstraction1Formula.add(helperFormula5);
              } else if (resultType1.isRationalType() && resultType2.isRationalType()) {
                RationalFormula helperFormula1, helperFormula2, helperFormula3, helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
                helperFormula8 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                }
                relationAbstraction1Formula.add(helperFormula6);
                relationAbstraction1Formula.add(helperFormula5);
              } else if (resultType1.isFloatingPointRoundingModeType() &&
                  resultType2.isFloatingPointRoundingModeType()) {
                FloatingPointRoundingModeFormula helperFormula1, helperFormula2, helperFormula3,
                    helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i] + "#");
                helperFormula8 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "#");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#";
                  relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "#" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "#";
                }
                relationAbstraction1Formula.add(helperFormula6);
                relationAbstraction1Formula.add(helperFormula5);
              }
              latticeNames[i] = arrayVariablesThatAreUsedInBothParts[i];
              latticeNames[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                  arrayVariablesThatAreUsedInBothParts[i + 1];
              latticeNamesTypes.put(latticeNames[i], resultType1);
              latticeNamesTypes.put(latticeNames[i + 1], resultType1);
            }
          }
          for (i = 0; i < arrayVariablesThatAreUsedInBothParts.length - 1; i = i + 2) {
            FormulaType resultType1 = variablesUsedInBothPartsClasses.get
                (arrayVariablesThatAreUsedInBothParts[i]);
            FormulaType resultType2 = variablesUsedInBothPartsClasses.get
                (arrayVariablesThatAreUsedInBothParts[i + 1]);
            if (!resultType1.equals(resultType2)) {
              relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              if (resultType1.isArrayType()){
                ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
                /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                    indexType1),
                    arrayVariablesThatAreUsedInBothParts[i]); */
                helperFormula1 = fmgr.makeVariable(resultType1Array,
                    arrayVariablesThatAreUsedInBothParts[i]);
                /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                    indexType1),
                    arrayVariablesThatAreUsedInBothParts[i] + "##");*/
                helperFormula7 = fmgr.makeVariable(resultType1Array,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
              } else if (resultType1.isBitvectorType()){
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
                int size = bitv.getSize();
                helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula7 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
              } else {
                helperFormula1 = fmgr.makeVariable(resultType1,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula7 = fmgr.makeVariable(resultType1,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
              }
              if (resultType2.isArrayType()){
                ArrayFormulaType resultType2Array = (ArrayFormulaType) resultType2;
                /*helperFormula2 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                    indexType2),
                    arrayVariablesThatAreUsedInBothParts[i+1]);*/
                helperFormula2 = fmgr.makeVariable(resultType2Array,
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                /*helperFormula8 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                    indexType2),
                    arrayVariablesThatAreUsedInBothParts[i+1] + "##");*/
                helperFormula8 = fmgr.makeVariable(resultType2Array,
                    arrayVariablesThatAreUsedInBothParts[i+1] + "##");
              } else if (resultType2.isBitvectorType()){
                FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType2;
                int size = bitv.getSize();
                helperFormula2 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                helperFormula8 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                    arrayVariablesThatAreUsedInBothParts[i+1] + "##");
              } else {
                helperFormula2 = fmgr.makeVariable(resultType2,
                    arrayVariablesThatAreUsedInBothParts[i+1]);
                helperFormula8 = fmgr.makeVariable(resultType2,
                    arrayVariablesThatAreUsedInBothParts[i+1] + "##");
              }
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else if (resultType1.isArrayType() && resultType2.isArrayType()) {
              ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
              ArrayFormulaType resultType2Array = (ArrayFormulaType) resultType2;
              relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                  indexType1),
                  arrayVariablesThatAreUsedInBothParts[i]);*/
              helperFormula1 = fmgr.makeVariable(resultType1Array,
                  arrayVariablesThatAreUsedInBothParts[i]);
              /*helperFormula2 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                  indexType2),
                  arrayVariablesThatAreUsedInBothParts[i + 1]);*/
              helperFormula2 = fmgr.makeVariable(resultType2Array,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1,
                  indexType1),
                  arrayVariablesThatAreUsedInBothParts[i] + "##");*/
              helperFormula7 = fmgr.makeVariable(resultType1Array,
                  arrayVariablesThatAreUsedInBothParts[i] + "##");
              /*helperFormula8 = fmgr.makeVariable(FormulaType.getArrayType(elementType2,
                  indexType2),
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##"); */
              helperFormula8 = fmgr.makeVariable(resultType2Array,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else if (resultType1.isBooleanType() && resultType2.isBooleanType()) {
              relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              BooleanFormula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i] + "##");
              helperFormula8 = fmgr.makeVariable(FormulaType.BooleanType,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else if (resultType1.isBitvectorType() &&
                resultType2.isBitvectorType()) {
              FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
              int size = bitv.getSize();
              FormulaType.BitvectorType bitv2 = (FormulaType.BitvectorType) resultType2;
              int size2 = bitv2.getSize();
              Formula helperFormula1, helperFormula2, helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                  arrayVariablesThatAreUsedInBothParts[i] + "##");
              helperFormula8 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
              if (!inequalityInterpolationAbstractions) {
                helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
                helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                    arrayVariablesThatAreUsedInBothParts[i] + "##";
                relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              } else {
                helperFormula5 = fmgr.makeLessOrEqual(helperFormula2, helperFormula8, true);
                helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                    arrayVariablesThatAreUsedInBothParts[i] + "##";
                relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              }
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else if (resultType1 == resultType2
                && !resultType1.isIntegerType()
                && !resultType1.isFloatingPointRoundingModeType()
                && !resultType1.isRationalType()) {
              relationAbstraction1[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i] + "##";
              relationAbstraction1[i + 1] = arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
              Formula helperFormula1, helperFormula2,
                  helperFormula7, helperFormula8;
              BooleanFormula helperFormula5, helperFormula6;
              helperFormula1 = fmgr.makeVariable(resultType1,
                  arrayVariablesThatAreUsedInBothParts[i]);
              helperFormula2 = fmgr.makeVariable(resultType2,
                  arrayVariablesThatAreUsedInBothParts[i + 1]);
              helperFormula7 = fmgr.makeVariable(resultType1,
                  arrayVariablesThatAreUsedInBothParts[i] + "##");
              helperFormula8 = fmgr.makeVariable(resultType2,
                  arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
              helperFormula5 = fmgr.makeEqual(helperFormula2, helperFormula8);
              helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
              relationAbstraction2Formula.add(helperFormula6);
              relationAbstraction2Formula.add(helperFormula5);
            } else {
              if (resultType1.isIntegerType() && resultType2.isIntegerType()) {
                IntegerFormula helperFormula1, helperFormula2, helperFormula3, helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula
                    helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
                helperFormula8 = fmgr.makeVariable(FormulaType.IntegerType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                }
                relationAbstraction2Formula.add(helperFormula6);
                relationAbstraction2Formula.add(helperFormula5);
              } else if (resultType1.isRationalType() && resultType2.isRationalType()) {
                RationalFormula helperFormula1, helperFormula2, helperFormula3, helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula
                    helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
                helperFormula8 = fmgr.makeVariable(FormulaType.RationalType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                }
                relationAbstraction2Formula.add(helperFormula6);
                relationAbstraction2Formula.add(helperFormula5);
              } else if (resultType1.isFloatingPointRoundingModeType() &&
                  resultType2.isFloatingPointRoundingModeType()) {
                FloatingPointRoundingModeFormula helperFormula1, helperFormula2, helperFormula3,
                    helperFormula4,
                    helperFormula7, helperFormula8;
                BooleanFormula
                    helperFormula5, helperFormula6;
                helperFormula1 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i]);
                helperFormula2 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i + 1]);
                helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);
                helperFormula7 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i] + "##");
                helperFormula8 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts[i + 1] + "##");
                helperFormula4 = fmgr.makeMinus(helperFormula7, helperFormula8);
                if (!inequalityInterpolationAbstractions) {
                  helperFormula5 = fmgr.makeEqual(helperFormula3, helperFormula4);
                  helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " = " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                } else {
                  helperFormula5 = fmgr.makeLessOrEqual(helperFormula3, helperFormula4, true);
                  helperFormula6 = fmgr.makeLessOrEqual(helperFormula1, helperFormula7, true);
                  relationAbstraction2[i] = arrayVariablesThatAreUsedInBothParts[i] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##";
                  relationAbstraction2[i + 1] = arrayVariablesThatAreUsedInBothParts[i] + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + " < " +
                      arrayVariablesThatAreUsedInBothParts[i] + "##" + " - " +
                      arrayVariablesThatAreUsedInBothParts[i + 1] + "##";
                }
                relationAbstraction2Formula.add(helperFormula6);
                relationAbstraction2Formula.add(helperFormula5);
              }
            }
          }
          relationAbstraction1[arrayVariablesThatAreUsedInBothParts.length - 1] =
              arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                  + ""
                  + " = " + arrayVariablesThatAreUsedInBothParts
                  [arrayVariablesThatAreUsedInBothParts.length - 1] + "#";
          FormulaType resultType1 = variablesUsedInBothPartsClasses.get
              (arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                  - 1]);
          FormulaType resultType2 = variablesUsedInBothPartsClasses.get
              (arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                  - 1]);
          if (resultType1.isArrayType() && resultType2.isArrayType()) {
            ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
            Formula helperFormula1,
                helperFormula7;
            BooleanFormula helperFormula6;
            /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1, indexType1),
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length -
                    1]);*/
            helperFormula1 = fmgr.makeVariable(resultType1Array,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length -
                    1]);
            /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1, indexType1),
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]
                    + "#");*/
            helperFormula7 = fmgr.makeVariable(resultType1Array,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]
                    + "#");
            helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
            relationAbstraction1Formula.add(helperFormula6);
          } else if (resultType1.isIntegerType() && resultType2.isIntegerType()) {
            IntegerFormula helperFormula1, helperFormula2;
            BooleanFormula helperFormula3;
            helperFormula1 = fmgr.makeVariable(FormulaType.IntegerType,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula2 =
                fmgr.makeVariable(FormulaType.IntegerType, arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "#");
            if (!inequalityInterpolationAbstractions) {
              helperFormula3 = fmgr.makeEqual(helperFormula1, helperFormula2);
            } else {
              helperFormula3 = fmgr.makeLessOrEqual(helperFormula1, helperFormula2, true);
              relationAbstraction1[arrayVariablesThatAreUsedInBothParts.length - 1] =
                  arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                      + ""
                      + " < " + arrayVariablesThatAreUsedInBothParts
                      [arrayVariablesThatAreUsedInBothParts.length - 1] + "#";
            }
            relationAbstraction1Formula.add(helperFormula3);
          } else if (resultType1.isRationalType() && resultType2.isRationalType()) {
            RationalFormula helperFormula1, helperFormula2;
            BooleanFormula helperFormula3;
            helperFormula1 = fmgr.makeVariable(FormulaType.RationalType,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula2 =
                fmgr.makeVariable(FormulaType.RationalType, arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "#");
            if (!inequalityInterpolationAbstractions) {
              helperFormula3 = fmgr.makeEqual(helperFormula1, helperFormula2);
            } else {
              helperFormula3 = fmgr.makeLessOrEqual(helperFormula1, helperFormula2, true);
              relationAbstraction1[arrayVariablesThatAreUsedInBothParts.length - 1] =
                  arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                      + ""
                      + " < " + arrayVariablesThatAreUsedInBothParts
                      [arrayVariablesThatAreUsedInBothParts.length - 1] + "#";
            }
            relationAbstraction1Formula.add(helperFormula3);
          } else if (resultType1.isFloatingPointRoundingModeType() && resultType2
              .isFloatingPointRoundingModeType()) {
            FloatingPointRoundingModeFormula helperFormula1, helperFormula2;
            BooleanFormula helperFormula3;
            helperFormula1 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula2 =
                fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                    arrayVariablesThatAreUsedInBothParts
                        [arrayVariablesThatAreUsedInBothParts.length - 1] + "#");
            if (!inequalityInterpolationAbstractions) {
              helperFormula3 = fmgr.makeEqual(helperFormula1, helperFormula2);
            } else {
              helperFormula3 = fmgr.makeLessOrEqual(helperFormula1, helperFormula2, true);
              relationAbstraction1[arrayVariablesThatAreUsedInBothParts.length - 1] =
                  arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                      + ""
                      + " < " + arrayVariablesThatAreUsedInBothParts
                      [arrayVariablesThatAreUsedInBothParts.length - 1] + "#";
            }
            relationAbstraction1Formula.add(helperFormula3);
          } else if (resultType1.isBooleanType() && resultType2.isBooleanType()) {
            BooleanFormula helperFormula1, helperFormula2;
            BooleanFormula helperFormula3;
            helperFormula1 = fmgr.makeVariable(FormulaType.BooleanType,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula2 =
                fmgr.makeVariable(FormulaType.BooleanType, arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "#");
            helperFormula3 = fmgr.makeEqual(helperFormula1, helperFormula2);
            relationAbstraction1Formula.add(helperFormula3);
          } else if (resultType1.isBitvectorType() && resultType2.isBitvectorType()) {
            FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
            int size = bitv.getSize();
            FormulaType.BitvectorType bitv2 = (FormulaType.BitvectorType) resultType2;
            int size2 = bitv2.getSize();
            Formula helperFormula1, helperFormula2;
            BooleanFormula helperFormula3;
            helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula2 =
                fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                    arrayVariablesThatAreUsedInBothParts
                        [arrayVariablesThatAreUsedInBothParts.length - 1] + "#");
            if (!inequalityInterpolationAbstractions) {
              helperFormula3 = fmgr.makeEqual(helperFormula1, helperFormula2);
            } else {
              helperFormula3 = fmgr.makeLessOrEqual(helperFormula1, helperFormula2, true);
              relationAbstraction1[arrayVariablesThatAreUsedInBothParts.length - 1] =
                  arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                      + ""
                      + " < " + arrayVariablesThatAreUsedInBothParts
                      [arrayVariablesThatAreUsedInBothParts.length - 1] + "#";
            }
            relationAbstraction1Formula.add(helperFormula3);
          } else if (resultType1 == resultType2) {
            Formula helperFormula1, helperFormula2;
            BooleanFormula helperFormula3;
            helperFormula1 = fmgr.makeVariable(resultType1,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula2 =
                fmgr.makeVariable(resultType2, arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "#");
            helperFormula3 = fmgr.makeEqual(helperFormula1, helperFormula2);
            relationAbstraction1Formula.add(helperFormula3);
          }
          relationAbstraction2[arrayVariablesThatAreUsedInBothParts.length - 1] =
              arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                  + ""
                  + " = " + arrayVariablesThatAreUsedInBothParts
                  [arrayVariablesThatAreUsedInBothParts.length - 1] + "##";
           if (resultType1.isArrayType() && resultType2.isArrayType()) {
            ArrayFormulaType resultType1Array = (ArrayFormulaType) resultType1;
            Formula helperFormula1,
                helperFormula7;
            BooleanFormula helperFormula6;
            /*helperFormula1 = fmgr.makeVariable(FormulaType.getArrayType(elementType1, indexType1),
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length -
                    1]);*/
             helperFormula1 = fmgr.makeVariable(resultType1Array,
                 arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length -
                     1]);
            /*helperFormula7 = fmgr.makeVariable(FormulaType.getArrayType(elementType1, indexType1),
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]
                    + "##");*/
             helperFormula7 = fmgr.makeVariable(resultType1Array,
                 arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                     - 1]
                     + "##");
            helperFormula6 = fmgr.makeEqual(helperFormula1, helperFormula7);
            relationAbstraction2Formula.add(helperFormula6);
          } else if (resultType2.isIntegerType() && resultType1.isIntegerType()) {
            IntegerFormula helperFormula4, helperFormula5;
            BooleanFormula helperFormula6;
            helperFormula4 = fmgr.makeVariable(FormulaType.IntegerType,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula5 = fmgr.makeVariable(FormulaType.IntegerType,
                arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "##");
            if (!inequalityInterpolationAbstractions) {
              helperFormula6 = fmgr.makeEqual(helperFormula4, helperFormula5);
            } else {
              helperFormula6 = fmgr.makeLessOrEqual(helperFormula4, helperFormula5, true);
              relationAbstraction2[arrayVariablesThatAreUsedInBothParts.length - 1] =
                  arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                      + ""
                      + " < " + arrayVariablesThatAreUsedInBothParts
                      [arrayVariablesThatAreUsedInBothParts.length - 1] + "##";
            }
            relationAbstraction2Formula.add(helperFormula6);
          } else if (resultType2.isRationalType() && resultType1.isRationalType()) {
            RationalFormula helperFormula4, helperFormula5;
            BooleanFormula helperFormula6;
            helperFormula4 = fmgr.makeVariable(FormulaType.RationalType,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula5 = fmgr.makeVariable(FormulaType.RationalType,
                arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "##");
            if (!inequalityInterpolationAbstractions) {
              helperFormula6 = fmgr.makeEqual(helperFormula4, helperFormula5);
            } else {
              helperFormula6 = fmgr.makeLessOrEqual(helperFormula4, helperFormula5, true);
              relationAbstraction2[arrayVariablesThatAreUsedInBothParts.length - 1] =
                  arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                      + ""
                      + " < " + arrayVariablesThatAreUsedInBothParts
                      [arrayVariablesThatAreUsedInBothParts.length - 1] + "##";
            }
            relationAbstraction2Formula.add(helperFormula6);
          } else if (resultType2.isFloatingPointRoundingModeType() && resultType1
              .isFloatingPointRoundingModeType()) {
            FloatingPointRoundingModeFormula helperFormula4, helperFormula5;
            BooleanFormula helperFormula6;
            helperFormula4 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula5 = fmgr.makeVariable(FormulaType.FloatingPointRoundingModeType,
                arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "##");
            if (!inequalityInterpolationAbstractions) {
              helperFormula6 = fmgr.makeEqual(helperFormula4, helperFormula5);
            } else {
              helperFormula6 = fmgr.makeLessOrEqual(helperFormula4, helperFormula5, true);
              relationAbstraction2[arrayVariablesThatAreUsedInBothParts.length - 1] =
                  arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                      + ""
                      + " < " + arrayVariablesThatAreUsedInBothParts
                      [arrayVariablesThatAreUsedInBothParts.length - 1] + "##";
            }
            relationAbstraction2Formula.add(helperFormula6);
          } else if (resultType2.isBooleanType() && resultType1.isBooleanType()) {
            BooleanFormula helperFormula4, helperFormula5;
            BooleanFormula helperFormula6;
            helperFormula4 = fmgr.makeVariable(FormulaType.BooleanType,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula5 = fmgr.makeVariable(FormulaType.BooleanType,
                arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "##");
            helperFormula6 = fmgr.makeEqual(helperFormula4, helperFormula5);
            relationAbstraction2Formula.add(helperFormula6);
          } else if (resultType1.isBitvectorType() && resultType2.isBitvectorType()) {
            FormulaType.BitvectorType bitv = (FormulaType.BitvectorType) resultType1;
            int size = bitv.getSize();
            FormulaType.BitvectorType bitv2 = (FormulaType.BitvectorType) resultType2;
            int size2 = bitv2.getSize();
            Formula helperFormula1, helperFormula2;
            BooleanFormula helperFormula3;
            helperFormula1 = fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size),
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula2 =
                fmgr.makeVariable(FormulaType.getBitvectorTypeWithSize(size2),
                    arrayVariablesThatAreUsedInBothParts
                        [arrayVariablesThatAreUsedInBothParts.length - 1] + "##");
            if (!inequalityInterpolationAbstractions) {
              helperFormula3 = fmgr.makeEqual(helperFormula1, helperFormula2);
            } else {
              helperFormula3 = fmgr.makeLessOrEqual(helperFormula1, helperFormula2, true);
              relationAbstraction2[arrayVariablesThatAreUsedInBothParts.length - 1] =
                  arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1]
                      + ""
                      + " < " + arrayVariablesThatAreUsedInBothParts
                      [arrayVariablesThatAreUsedInBothParts.length - 1] + "##";
            }
            relationAbstraction2Formula.add(helperFormula3);
          } else if (resultType1 == resultType2) {
            Formula helperFormula1, helperFormula2;
            BooleanFormula helperFormula3;
            helperFormula1 = fmgr.makeVariable(resultType1,
                arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length
                    - 1]);
            helperFormula2 =
                fmgr.makeVariable(resultType2, arrayVariablesThatAreUsedInBothParts
                    [arrayVariablesThatAreUsedInBothParts.length - 1] + "##");
            helperFormula3 = fmgr.makeEqual(helperFormula1, helperFormula2);
            relationAbstraction2Formula.add(helperFormula3);
          }
          latticeNames[latticeNames.length - 1] =
              arrayVariablesThatAreUsedInBothParts[arrayVariablesThatAreUsedInBothParts.length - 1];
          latticeNamesTypes.put(latticeNames[latticeNames.length - 1], resultType1);
        }
      } finally {
        buildingLatticeNamesAndLatticeTypesTimer.stop();
      }

      for(Formula x : relationAbstraction1Formula){
        logger.log(Level.WARNING, x.toString());
      }

      for(Formula y : relationAbstraction2Formula){
        logger.log(Level.WARNING, y.toString());
      }

      for(int i=0; i < latticeNames.length; i++){
        logger.log(Level.WARNING, "LatticeName: " + latticeNames[i] + " "
            + "LatticeNameType: " + latticeNamesTypes.get
            (latticeNames[i]));
      }

      FirstPartRenamingFct renamer1 = new FirstPartRenamingFct
          (arrayVariablesThatAreUsedInBothParts, arrayVariablesThatAreNotUsedInBothParts);
      ScndPartRenamingFct renamer2 = new ScndPartRenamingFct
          (arrayVariablesThatAreUsedInBothParts, arrayVariablesThatAreNotUsedInBothParts);
      BooleanFormula firstPart;
      BooleanFormula scndPart;

      if (it == 0) {
        firstPart = formulas.get(0);
        scndPart = formulas.get(1);
      } else {
        firstPart = formulas.get(1);
        scndPart = formulas.get(2);
      }
      BooleanFormula firstPartChanged;
      BooleanFormula scndPartChanged;
      if (it == 0) {
        firstPartChanged = oldFmgr.renameFreeVariablesAndUFs(firstPart, renamer1);
        scndPartChanged = oldFmgr.renameFreeVariablesAndUFs(scndPart, renamer2);
      } else {
        firstPartChanged = oldFmgr.renameFreeVariablesAndUFs(firstPart, renamer1);
        scndPartChanged = oldFmgr.renameFreeVariablesAndUFs(scndPart, renamer2);
      }
      List<BooleanFormula> changedFomulasRest1 =
          Lists.newArrayListWithExpectedSize(formulas.size() - 1);
      List<BooleanFormula> changedFomulasRest2 =
          Lists.newArrayListWithExpectedSize(formulas.size() - 1);

      BooleanFormula helperFormula1;
      BooleanFormula helperFormula2;

      renamingTimer.start();
      try {

        firstPartChanged = fmgr.translateFrom(firstPartChanged, oldFmgr);
        scndPartChanged = fmgr.translateFrom(scndPartChanged, oldFmgr);
        if (it != 0) {
          BooleanFormula addFormula = interpolants.get(it - 1);
          BooleanFormula changedFormula = fmgr.renameFreeVariablesAndUFs(addFormula, renamer1);
          changedFomulasRest1.add(changedFormula);
        }
        for (int i = it + 2; i < oldFormulas.size(); i++) {
          BooleanFormula addFormula = oldFormulas.get(i);
          BooleanFormula changedFormula = oldFmgr.renameFreeVariablesAndUFs(addFormula, renamer2);
          changedFormula = fmgr.translateFrom(changedFormula, oldFmgr);
          changedFomulasRest2.add(changedFormula);
        }
      } finally {
        renamingTimer.stop();
      }


      boolean abstractionFeasible = false;
      boolean isIncomparable = false;
      helperFormula1 = firstPartChanged;
      helperFormula2 = scndPartChanged;
      String latticenamesH = "";
      buildingAbstractionsTimer.start();
      try {
        for (int h = 0; h < latticeNames.length;
             h++) {
          for (int k = 0; k < relationAbstraction1.length; k++) {
            if (relationAbstraction1[k] != null && latticeNames[h] != null) {
              if (relationAbstraction1[k].contains(latticeNames[h] + " = ")
                  || relationAbstraction1[k].contains(latticeNames[h] + " < ")) {
                helperFormula1 = fmgr.makeAnd(helperFormula1, relationAbstraction1Formula.get
                    (k));
                if (latticenamesH.isEmpty()) {
                  latticenamesH = latticeNames[h];
                } else {
                  latticenamesH = latticenamesH + " ," + latticeNames[h];
                }

              }
            }
            if (relationAbstraction2[k] != null && latticeNames[h] != null) {
              if (relationAbstraction2[k].contains(latticeNames[h] + " = ")
                  || relationAbstraction2[k].contains(latticeNames[h] + " < ")) {
                helperFormula2 = fmgr.makeAnd(helperFormula2, relationAbstraction2Formula.get
                    (k));

              }
            }
          }

          if (!latticenamesH.isEmpty()) {
          BooleanFormula toCheckFormula = fmgr.makeAnd(helperFormula1, helperFormula2);
          List<BooleanFormula> toCheckFormulaList =
              Lists.newArrayListWithExpectedSize(formulas.size() - 1);
          for (BooleanFormula f : changedFomulasRest1) {
            toCheckFormulaList.add(f);
          }
          toCheckFormulaList.add(toCheckFormula);
          for (BooleanFormula f : changedFomulasRest2) {
            toCheckFormulaList.add(f);
          }
          BlockFormulas toCheckFormulaBlocked = new BlockFormulas(toCheckFormulaList);
          feasibilityCheckTimer.start();
          try {
            abstractionFeasible = prove(toCheckFormulaBlocked, prover);
          } finally {
            feasibilityCheckTimer.stop();
          }
          if (abstractionFeasible) {
            List<List<Formula>> frontierListCopy = Lists
                .newArrayListWithExpectedSize(oldFormulas.size() - 1);
            for ( List<Formula> s : frontierList) {
              frontierListCopy.add(s);
            }
            isIncomparable = checkComparability(frontierListCopy,
                latticenamesH, latticeNames);

            if (isIncomparable) {
              maximisationTimer.start();
              try {
                List<Formula> new_frontier_elem = maximise(firstPartChanged,
                    scndPartChanged,
                    relationAbstraction1,
                    relationAbstraction2, relationAbstraction1Formula,
                    relationAbstraction2Formula, latticeNames,
                    latticenamesH,
                     prover);
                frontierList.add(new_frontier_elem);
              } finally {
                maximisationTimer.stop();
              }
            }
          }
        }

    }
  } finally

  {
    buildingAbstractionsTimer.stop();
  }

      helperFormula1 = firstPartChanged;
      helperFormula2 = scndPartChanged;
      if (frontierList != null && (frontierList.size() >= 1)) {
        for (Formula y : frontierList.get(0)) {
          for (int k = 0; k < relationAbstraction1.length; k++) {

            if (relationAbstraction1Formula.get(k).toString().contains("= " + y.toString())
                || relationAbstraction1Formula.get(k).toString().contains("<= " + y.toString())) {
              helperFormula1 = fmgr.makeAnd(helperFormula1, relationAbstraction1Formula.get
                  (k));


            }

            if (relationAbstraction2Formula.get(k).toString().contains("= " + y.toString())
                || relationAbstraction1Formula.get(k).toString().contains("<= " + y.toString())) {
              helperFormula2 = fmgr.makeAnd(helperFormula2, relationAbstraction2Formula.get
                  (k));


            }
          }
        }
      }

      try (@SuppressWarnings("unchecked")
           InterpolatingProverEnvironment<T> myItpProver =
               (InterpolatingProverEnvironment<T>) mySolver.newProverEnvironmentWithInterpolation
                   ()) {


        List<T> myItpGroupIds = new ArrayList<>(formulas.size());

    interpolationTimer.start();
    try {
      if (it != 0) {
        for (BooleanFormula f : changedFomulasRest1) {
          myItpGroupIds.add(myItpProver.push(f));
        }
      }

      myItpGroupIds.add(myItpProver.push(helperFormula1));
      myItpProver.push(helperFormula2);
      if (!changedFomulasRest2.isEmpty()) {
        for (BooleanFormula f : changedFomulasRest2) {
          myItpProver.push(f);
        }
      }

      if (!myItpProver.isUnsat()) {
        throw new SolverException("Interpolant kann nicht berechnet werden!");

      } else {

        BooleanFormula myInterpolant = myItpProver.getInterpolant
            (myItpGroupIds);

        if (myInterpolant != null) {
          interpolants.add(myInterpolant);
          fmgr.translateFrom(myInterpolant, mySolver.getFormulaManager());
        }
      }
    } finally {
      interpolationTimer.stop();
    }
      }

    }
    if (interpolants != null && !interpolants.isEmpty()) {
      return interpolants;
    } else {
      return Collections.emptyList();
    }
  }

  @SuppressWarnings("rawtypes")
  private Boolean prove(BlockFormulas toCheckFormulaBlocked, ProverEnvironment prover){
    Boolean abstractionFeasible = false;
    try  {
      for (BooleanFormula block : toCheckFormulaBlocked.getFormulas()) {
        prover.push(block);
      }
      if (!prover.isUnsat()) {


        abstractionFeasible = false;

      } else {

        abstractionFeasible = true;
      }
      for (@SuppressWarnings("unused") BooleanFormula block : toCheckFormulaBlocked.getFormulas()) {
        prover.pop();
      }
    } catch (InterruptedException pE) {
      logger.log(Level.WARNING, "Interrupted Exception!");
    } catch (SolverException pE) {
      logger.log(Level.WARNING, "Solver Exception!");
    }
    return abstractionFeasible;
  }
  @SuppressWarnings("rawtypes")
  private List<Formula> maximise(BooleanFormula firstPartChanged,
                                                           BooleanFormula
                                                               scndPartChanged, String[] relationAbstraction1, String[] relationAbstraction2,
                                                           List<BooleanFormula> relationAbstraction1Formula,
                                                           List<BooleanFormula>
                                                               relationAbstraction2Formula,
                                                           String[]
       latticeNames,  String latticenamesH, ProverEnvironment
                                                               prover){

    String[] middleElement = new String[latticeNames.length];
    int middleElemIndex = 0;
    Boolean isFeasible = true;
    BooleanFormula helperFormula1;
    BooleanFormula helperFormula2;
     List<Formula> maximumFeasibleAbstraction = Lists
        .newArrayListWithExpectedSize
            (formulas
                .size() - 1);

    for (int i = 0; i < latticeNames.length; i++) {
         if (!latticenamesH.contains
          (latticeNames[i])){
        latticenamesH = latticenamesH + " ," + latticeNames[i];
        helperFormula1 = firstPartChanged;
        helperFormula2 = scndPartChanged;
        Iterable<String> splitOperator = Splitter.on(" ,").split(latticenamesH);
        for (String s : splitOperator) {

          for (int k = 0; k < relationAbstraction1.length; k++) {
            if (relationAbstraction1[k] != null && !(s == null)) {
              if (relationAbstraction1[k].contains(s + " = ")
                  || relationAbstraction1[k].contains(s + " < ")) {
                helperFormula1 = fmgr.makeAnd(helperFormula1, relationAbstraction1Formula.get
                    (k));


              }
            }
            if (relationAbstraction2[k] != null && !(s == null)) {
              if (relationAbstraction2[k].contains(s + " = ")
                  || relationAbstraction2[k].contains(s + " < ")) {
                helperFormula2 = fmgr.makeAnd(helperFormula2, relationAbstraction2Formula.get
                    (k));


              }
            }
          }
        }
        BooleanFormula toCheckFormula = fmgr.makeAnd(helperFormula1, helperFormula2);
        List<BooleanFormula> toCheckFormulaList =
            Lists.newArrayListWithExpectedSize(formulas.size() - 1);
        toCheckFormulaList.add(toCheckFormula);
        BlockFormulas toCheckFormulaBlocked = new BlockFormulas(toCheckFormulaList);
        isFeasible = prove(toCheckFormulaBlocked, prover);
        if (isFeasible){
          for (int m = 0; m < latticeNames.length; m++){
            if (!latticenamesH.isEmpty()  &&!latticenamesH.contains
                (latticeNames[m])){
              middleElement[middleElemIndex] = latticenamesH + " ," + latticeNames[m];
              latticenamesH = middleElement[middleElemIndex];
              middleElemIndex++;
            }
          }
          break;
        }
      }
    }

    if (middleElement[0] == null){
      List<FormulaType> formulaTypes = Lists.newArrayListWithExpectedSize(latticeNamesTypes.size()
          - 1);
      Iterable<String> splitOperator = Splitter.on(" ,").split(latticenamesH);
      for (String s : splitOperator) {
        for (int i = 0; i < latticeNames.length; i++) {
          if (latticeNames[i] != null && !(s == null) && s.equals(latticeNames[i])){
            formulaTypes.add(latticeNamesTypes.get(latticeNames[i]));
          }
        }
      }

        maximumFeasibleAbstraction = StringtoIntegerFormulaList
            ( latticenamesH, formulaTypes);



    } else {
      int counter = 0;
      for (int i = 0; i < middleElement.length; i++) {
        if (middleElement[i] != null) {
          counter++;
        }
      }
      Boolean middleElemFeasible;
      String middleElementString = middleElement[counter / 2];
      helperFormula1 = firstPartChanged;
      helperFormula2 = scndPartChanged;
      Iterable<String> splitOperator = Splitter.on(" ,").split(middleElementString);
      for (String s : splitOperator) {
        for (int k = 0; k < relationAbstraction1.length; k++) {
          if (relationAbstraction1[k] != null) {
            if (relationAbstraction1[k].contains(s + " = ")
                || relationAbstraction1[k].contains(s + " < ")) {
              helperFormula1 = fmgr.makeAnd(helperFormula1, relationAbstraction1Formula.get
                  (k));


            }
          }
          if (relationAbstraction2[k] != null) {
            if (relationAbstraction2[k].contains(s + " = ")
                || relationAbstraction2[k].contains(s + " < ")) {
              helperFormula2 = fmgr.makeAnd(helperFormula2, relationAbstraction2Formula.get
                  (k));


            }
          }
        }
      }
      BooleanFormula toCheckFormula = fmgr.makeAnd(helperFormula1, helperFormula2);
      List<BooleanFormula> toCheckFormulaList =
          Lists.newArrayListWithExpectedSize(formulas.size() - 1);
      toCheckFormulaList.add(toCheckFormula);
      BlockFormulas toCheckFormulaBlocked = new BlockFormulas(toCheckFormulaList);
      middleElemFeasible = prove(toCheckFormulaBlocked, prover);
      if (middleElemFeasible) {
        List<FormulaType> formulaTypes2 = Lists.newArrayListWithExpectedSize(latticeNamesTypes
            .size()
            - 1);
        Iterable<String> splitOperator2 = Splitter.on(" ,").split(middleElement[counter/2]);
        for (String s : splitOperator2) {
          for (int i = 0; i < latticeNames.length; i++) {
            if (latticeNames[i] != null && s.equals(latticeNames[i])){
              formulaTypes2.add(latticeNamesTypes.get(latticeNames[i]));
            }
          }
        }
        maximumFeasibleAbstraction = StringtoIntegerFormulaList
            (middleElement[counter/2],
                formulaTypes2);

      } else {
        List<FormulaType> formulaTypes = Lists.newArrayListWithExpectedSize(latticeNamesTypes.size()
            - 1);
        Iterable<String> splitOperator3 = Splitter.on(" ,").split(latticenamesH);
        for (String s : splitOperator3) {
          for (int i = 0; i < latticeNames.length; i++) {
            if (latticeNames[i] != null && s.equals(latticeNames[i])){
              formulaTypes.add(latticeNamesTypes.get(latticeNames[i]));
            }
          }
        }

        maximumFeasibleAbstraction = StringtoIntegerFormulaList
            (latticenamesH, formulaTypes);

      }
    }
    return maximumFeasibleAbstraction;
  }
  @SuppressWarnings({"rawtype", "unchecked"})
  private List<Formula> StringtoIntegerFormulaList(String input, List<FormulaType>
      formulaTypes){
    Formula helperFormula1, helperFormula2, helperFormula3;
    List<Formula> maximumFeasibleAbstraction = Lists.newArrayListWithExpectedSize(formulas.size() - 1);

    if (input.equals("root")){
      return Collections.emptyList();
    }
    if (input.isEmpty()){
      return Collections.emptyList();
    }
    String[] helperArray = new String[2];
    int j = 0;
    Iterable<String> splitOperator = Splitter.on(" ,").split(input);
    for (String s : splitOperator) {
      FormulaType currentType = formulaTypes.get(j);
      if (s.contains(" - ")){
        int i = 0;
        Iterable<String> splitOperator2 = Splitter.on(" - ").split(s);
        for (String t : splitOperator2){
          helperArray[i] = t;
          i++;
        }
        helperFormula1 = fmgr.makeVariable(latticeNamesTypes.get(helperArray[0]),
            helperArray[0]);
        helperFormula2 = fmgr.makeVariable(latticeNamesTypes.get(helperArray[0]), helperArray[1]);
        helperFormula3 = fmgr.makeMinus(helperFormula1, helperFormula2);

      }
      else {
        helperFormula3 = fmgr.makeVariable(currentType, s);
      }
      maximumFeasibleAbstraction.add(helperFormula3);
      j++;
    }
    return maximumFeasibleAbstraction;
  }

  private Boolean checkComparability( List<List<Formula>>
      frontierListCopy,
                                     String
       latticenamesH, String[] latticeNames){
    List<FormulaType> formulaTypes = Lists.newArrayListWithExpectedSize(latticeNamesTypes.size()
        - 1);
    Iterable<String> splitOperator = Splitter.on(" ,").split(latticenamesH);
    for (String s : splitOperator) {
      for (int i = 0; i < latticeNames.length; i++) {
        if (latticeNames[i] != null && s.equals(latticeNames[i])){
          formulaTypes.add(latticeNamesTypes.get(latticeNames[i]));
        }
      }
    }

    List<Formula> toCompareWith = StringtoIntegerFormulaList(latticenamesH,
         formulaTypes);
    List<List<Formula>> compareList = Lists.newArrayListWithExpectedSize(formulas.size() -
        1);
    Boolean isIncomparable = false;
    Boolean comparable = false;
    while (frontierListCopy.size() != 0) {
       List<Formula> smallestList = frontierListCopy.get(0);


      for ( List<Formula> f : frontierListCopy) {
        if (f.size() < smallestList.size()) {
          smallestList = f;
        }
      }
      compareList.add(smallestList);
      frontierListCopy.remove(smallestList);
      for (Formula f : toCompareWith) {
        comparable = false;
        for ( List<Formula> g : compareList) {
          for ( Formula h : g) {

            if (h.equals(f)) {

              comparable = true;
            }
          }

        }
        if (comparable == false) {
          break;
        }
      }

    }
    if (comparable == false) {
      isIncomparable = true;
    }
    return isIncomparable;
  }


}
