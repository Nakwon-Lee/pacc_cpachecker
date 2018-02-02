#!/bin/bash

TARGET_FILE="../sv-benchmarks/c/ntdrivers-simplified/cdaudio_simpl1_true-unreach-call_true-valid-memsafety_true-termination.cil.c"
BEST_DIR="bests_FCtime"

mkdir $BEST_DIR
for i in {1..10}
do
	python3 scripts/TSSearch.py $TARGET_FILE
	mv bestfitvalues.txt $BEST_DIR/bestfitvalues$i.txt
	mv bestts.xml $BEST_DIR/bestts$i.xml
done
