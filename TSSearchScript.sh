#!/bin/bash

TARGET_FILE="../sv-benchmarks/c/ssh/s3_srvr.blast.01_true-unreach-call.i.cil.c"

sudo python3 ./scripts/TSSearch.py --cores 0 --memlimit 16000000000 --no-container -- scripts/cpa.sh -heap 14000M -timelimit 30s -noout -Dy-MySearchStrategy-PredAbs-ABElf -preprocess -stats -setprop cpa.predicate.memoryAllocationsAlwaysSucceed=true -spec ../sv-benchmarks/c/ReachSafety.prp $TARGET_FILE
