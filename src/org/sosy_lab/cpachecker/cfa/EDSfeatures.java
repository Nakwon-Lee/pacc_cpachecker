// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2021 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cfa;

import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.FunctionEntryNode;
import org.sosy_lab.cpachecker.util.statistics.IntStatistics;

public class EDSfeatures {
  public int NODES;
  public int MXNDFN;
  public double AVNDFN;
  public double SDNDFN;
  public int EDGES;
  public int MXEGFN;
  public double AVEGFN;
  public double SDEGFN;
  public int LOOPS = 0;
  public int MXLPFN;
  public double AVLPFN;
  public double SDLPFN;
  public int FUNCS;
  public int MXCALLS;
  public double AVCALLS;
  public double SDCALLS;
  public int VARS = 0;
  public int VARSASM = 0;
  public int VARSLOOP = 0;
  public int VARSINC = 0;
  public int FIELDS = 0;
  public int MXCC;
  public long SMCC;
  public double AVCC;
  public double SDCC;

  public EDSfeatures(CFA cfa) {
    NODES = cfa.getAllNodes().size();
    MXNDFN =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(node -> node.getFunctionNodes().get().size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getMax();
    AVNDFN =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(node -> node.getFunctionNodes().get().size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getAverage();
    SDNDFN =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(node -> node.getFunctionNodes().get().size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getStandardDeviation();
    EDGES = cfa.getAllNodes().stream().mapToInt(CFANode::getNumLeavingEdges).sum();
    MXEGFN =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(node -> node.getFunctionEdges().get().size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getMax();
    AVEGFN =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(node -> node.getFunctionEdges().get().size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getAverage();
    SDEGFN =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(node -> node.getFunctionEdges().get().size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getStandardDeviation();
    cfa.getLoopStructure().ifPresent(lps -> {
      LOOPS = lps.getCount();
    });
    MXLPFN =
        cfa.getAllFunctionNames()
            .stream()
            .map(value -> cfa.getLoopStructure().get().getLoopsForFunction(value))
            .mapToInt(lps -> lps.size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getMax();
    AVLPFN =
        cfa.getAllFunctionNames()
            .stream()
            .map(value -> cfa.getLoopStructure().get().getLoopsForFunction(value))
            .mapToInt(lps -> lps.size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getAverage();
    SDLPFN =
        cfa.getAllFunctionNames()
            .stream()
            .map(value -> cfa.getLoopStructure().get().getLoopsForFunction(value))
            .mapToInt(lps -> lps.size())
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getStandardDeviation();
    FUNCS = cfa.getNumberOfFunctions();
    MXCALLS =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(FunctionEntryNode::getNumEnteringEdges)
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getMax();
    AVCALLS =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(FunctionEntryNode::getNumEnteringEdges)
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getAverage();
    SDCALLS =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(FunctionEntryNode::getNumEnteringEdges)
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getStandardDeviation();
    cfa.getVarClassification().ifPresent(vc -> {
      VARS = vc.getRelevantVariables().size();
    });
    cfa.getVarClassification().ifPresent(vc -> {
      VARSASM = vc.getAssumedVariables().size();
    });
    cfa.getLoopStructure().ifPresent(lps -> {
      VARSLOOP = lps.getLoopExitConditionVariables().size();
    });
    cfa.getLoopStructure().ifPresent(lps -> {
      VARSINC = lps.getLoopIncDecVariables().size();
    });
    cfa.getVarClassification().ifPresent(vc -> {
      FIELDS = vc.getRelevantFields().size();
    });
    MXCC =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(FunctionEntryNode::getCyclomaticComplexity)
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getMax();
    SMCC =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(FunctionEntryNode::getCyclomaticComplexity)
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getSum();
    AVCC =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(FunctionEntryNode::getCyclomaticComplexity)
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getAverage();
    SDCC =
        cfa.getAllFunctionHeads()
            .stream()
            .mapToInt(FunctionEntryNode::getCyclomaticComplexity)
            .collect(IntStatistics::new, IntStatistics::accept, IntStatistics::combine)
            .getStandardDeviation();
  }
}
