#!/bin/bash

TARGET_FILE="../sv-benchmarks/c/ntdrivers-simplified/cdaudio_simpl1_true-unreach-call_true-valid-memsafety_true-termination.cil.c"
BEST_DIR="bests_FCtime_cdaudio"

for i in {1..10}
do
	python3 scripts/BestRunExecutor.py $BEST_DIR/bestts$i.xml $BEST_DIR/bestrun$i.csv 10 $TARGET_FILE
done
