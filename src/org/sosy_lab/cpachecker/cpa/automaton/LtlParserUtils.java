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
package org.sosy_lab.cpachecker.cpa.automaton;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import jhoafparser.ast.AtomLabel;
import jhoafparser.ast.BooleanExpression;
import jhoafparser.ast.BooleanExpression.Type;
import jhoafparser.storage.StoredAutomaton;
import jhoafparser.storage.StoredEdgeWithLabel;
import jhoafparser.storage.StoredHeader;
import jhoafparser.storage.StoredHeader.NameAndExtra;
import jhoafparser.storage.StoredState;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.CParser;
import org.sosy_lab.cpachecker.cfa.CProgramScope;
import org.sosy_lab.cpachecker.cfa.ast.AExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CAstNode;
import org.sosy_lab.cpachecker.cfa.ast.c.CBinaryExpressionBuilder;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpressionStatement;
import org.sosy_lab.cpachecker.cfa.parser.Scope;
import org.sosy_lab.cpachecker.cfa.types.MachineModel;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCodeException;
import org.sosy_lab.cpachecker.util.ltl.LtlParseException;

public class LtlParserUtils {

  /**
   * Takes a {@link jhoafparser.storage.StoredAutomaton} (i.e., an automaton in HOA-format) as
   * argument and transforms that into an {@link org.sosy_lab.cpachecker.cpa.automaton.Automaton}.
   *
   * @param pStoredAutomaton the storedAutomaton created by parsing an input in HOA-format.
   * @return an automaton from the automaton-framework in CPAchecker
   * @throws LtlParseException if the transformation fails either due to some false values in {@code
   *     pStoredAutomaton} or because of an erroneous config.
   */
  public static Automaton transform(
      StoredAutomaton pStoredAutomaton,
      Configuration pConfig,
      LogManager pLogger,
      MachineModel pMachineModel,
      Scope pScope)
      throws LtlParseException {
    return new HoaToAutomatonTransformer(pStoredAutomaton, pConfig, pLogger, pMachineModel, pScope)
        .doTransform();
  }

  /**
   * Produces an {@link Automaton} from an HOA-formatted {@link StoredAutomaton}, without having to
   * provide a logger, machine-model and scope.
   *
   * <p>This method is mainly used for testing the transformation outside of CPAchecker.
   */
  public static Automaton transform(StoredAutomaton pStoredAutomaton) throws LtlParseException {
    return new HoaToAutomatonTransformer(pStoredAutomaton).doTransform();
  }

  private static class HoaToAutomatonTransformer {

    private static final String BUECHI_AUTOMATON = "Buechi_Automaton";
    private static final String FALSE = "0";
    private static final String TRUE = "1";

    private final LogManager logger;
    private final MachineModel machineModel;
    private final Scope scope;
    private final CParser parser;

    private final StoredAutomaton storedAutomaton;

    private HoaToAutomatonTransformer(StoredAutomaton pStoredAutomaton) {
      storedAutomaton = checkNotNull(pStoredAutomaton);

      logger = LogManager.createNullLogManager();
      machineModel = MachineModel.LINUX64;
      scope = CProgramScope.empty();
      parser = CParser.Factory.getParser(logger, CParser.Factory.getDefaultOptions(), machineModel);
    }

    private HoaToAutomatonTransformer(
        StoredAutomaton pStoredAutomaton,
        Configuration pConfig,
        LogManager pLogger,
        MachineModel pMachineModel,
        Scope pScope)
        throws LtlParseException {
      storedAutomaton = checkNotNull(pStoredAutomaton);

      try {
        logger = checkNotNull(pLogger);
        machineModel = checkNotNull(pMachineModel);
        scope = checkNotNull(pScope);
        parser =
            CParser.Factory.getParser(pLogger, CParser.Factory.getOptions(pConfig), machineModel);
      } catch (InvalidConfigurationException e) {
        throw new LtlParseException(e.getMessage(), e);
      }
    }

    private Automaton doTransform() throws LtlParseException {

      StoredHeader storedHeader = storedAutomaton.getStoredHeader();

      List<NameAndExtra<List<Object>>> accNames = storedHeader.getAcceptanceNames();
      if (!(Iterables.getOnlyElement(accNames).name.equals("Buchi"))) {
        throw new LtlParseException(
            String.format(
                "Only 'Buchi'-acceptance is allowed, but instead the following was found: %s",
                accNames));
      }

      String accCond = storedHeader.getAcceptanceCondition().toStringInfix();
      if (!accCond.equals("Inf(0)")) {
        throw new LtlParseException(
            String.format(
                "The only allowed acceptance-condition is %s, but instead the following was found: %s",
                "Inf(0)", accCond));
      }

      ImmutableSet<String> requiredProperties =
          ImmutableSet.of("trans-labels", "explicit-labels", "state-acc", "no-univ-branch");
      if (!storedHeader.getProperties().stream().allMatch(x -> requiredProperties.contains(x))) {
        throw new LtlParseException(
            String.format(
                "The storedAutomaton-param may only contain %s as properties, but instead the following were found: %s",
                requiredProperties, storedHeader.getProperties()));
      }

      List<Integer> initStateList = Iterables.getOnlyElement(storedHeader.getStartStates());
      if (initStateList.size() != 1) {
        throw new LtlParseException("Univeral initial states are not supported");
      }

      try {

        List<AutomatonInternalState> stateList = new ArrayList<>();
        for (int i = 0; i < storedAutomaton.getNumberOfStates(); i++) {
          StoredState storedState = storedAutomaton.getStoredState(i);
          List<AutomatonTransition> transitionList = new ArrayList<>();

          for (StoredEdgeWithLabel edge : storedAutomaton.getEdgesWithLabel(i)) {
            int successorStateId = Iterables.getOnlyElement(edge.getConjSuccessors()).intValue();
            String successorName = storedAutomaton.getStoredState(successorStateId).getInfo();

            transitionList.addAll(getTransitions(edge.getLabelExpr(), successorName));
          }

          stateList.add(
              new AutomatonInternalState(storedState.getInfo(), transitionList, false, true));
        }

        StoredState initState =
            storedAutomaton.getStoredState(Iterables.getOnlyElement(initStateList).intValue());
        return new Automaton(BUECHI_AUTOMATON, ImmutableMap.of(), stateList, initState.getInfo());

      } catch (InvalidAutomatonException e) {
        throw new RuntimeException(
            "The passed storedAutomaton-parameter produces an inconsistent automaton", e);
      } catch (UnrecognizedCodeException e) {
        throw new LtlParseException(e.getMessage(), e);
      }
    }

    private List<AutomatonTransition> getTransitions(
        BooleanExpression<AtomLabel> pLabelExpr, String pSuccessorName)
        throws LtlParseException, UnrecognizedCodeException {
      Builder<AutomatonTransition> transitions = ImmutableList.builder();

      switch (pLabelExpr.getType()) {
        case EXP_OR:
          transitions.addAll(getTransitions(pLabelExpr.getLeft(), pSuccessorName));
          transitions.addAll(getTransitions(pLabelExpr.getRight(), pSuccessorName));
          break;
        default:
          Builder<AExpression> expressions = ImmutableList.builder();
          expressions.addAll(getExpressions(pLabelExpr));
          transitions.add(createTransition(expressions.build(), pSuccessorName));
          break;
      }

      return transitions.build();
    }

    private List<CExpression> getExpressions(BooleanExpression<AtomLabel> pLabelExpr)
        throws LtlParseException, UnrecognizedCodeException {
      Builder<CExpression> expBuilder = ImmutableList.builder();

      Type type = pLabelExpr.getType();
      switch (type) {
        case EXP_TRUE:
          return ImmutableList.of(assume(TRUE));
        case EXP_FALSE:
          return ImmutableList.of(assume(FALSE));
        case EXP_ATOM:
          int apIndex = pLabelExpr.getAtom().getAPIndex();
          return ImmutableList.of(assume(storedAutomaton.getStoredHeader().getAPs().get(apIndex)));
        case EXP_OR:
          expBuilder.addAll(getExpressions(pLabelExpr.getLeft()));
          expBuilder.addAll(getExpressions(pLabelExpr.getRight()));
          break;
        case EXP_AND:
          expBuilder.addAll(getExpressions(pLabelExpr.getLeft()));
          expBuilder.addAll(getExpressions(pLabelExpr.getRight()));
          break;
        case EXP_NOT:
          CBinaryExpressionBuilder b = new CBinaryExpressionBuilder(machineModel, logger);
          CExpression exp = Iterables.getOnlyElement(getExpressions(pLabelExpr.getLeft()));
          expBuilder.add(b.negateExpressionAndSimplify(exp));
          break;
        default:
          throw new RuntimeException("Unhandled expression type: " + type);
      }

      return expBuilder.build();
    }

    private CExpression assume(String pExpression) throws LtlParseException {
      CAstNode sourceAST;
      try {
        sourceAST = CParserUtils.parseSingleStatement(pExpression, parser, scope);
      } catch (InvalidAutomatonException e) {
        throw new LtlParseException(
            String.format("Error in literal of ltl-formula: %s", e.getMessage()), e);
      }
      return ((CExpressionStatement) sourceAST).getExpression();
    }

    private AutomatonTransition createTransition(
        List<AExpression> pAssumptions, String pFollowStateName) {
      return new AutomatonTransition(
          AutomatonBoolExpr.TRUE,
          ImmutableList.of(),
          pAssumptions,
          ImmutableList.of(),
          pFollowStateName);
    }
  }
}
