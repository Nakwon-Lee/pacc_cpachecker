#!/bin/bash

sudo python3 ./scripts/TSSearch.py --cores 0 --memlimit 8000000000 --no-container -- scripts/cpa.sh -heap 7000M -timelimit 30s -noout -Dy-MySearchStrategy-PredAbs-ABElf -preprocess -stats -setprop cpa.predicate.memoryAllocationsAlwaysSucceed=true -spec ../sv-benchmarks/c/ReachSafety.prp ../sv-benchmarks/c/ntdrivers/floppy2_true-unreach-call.i.cil.c
