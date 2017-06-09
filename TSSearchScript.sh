#!/bin/bash

sudo python3 ./scripts/TSSearch.py --timelimit 900s --walltimelimit 990s --cores 0 --memlimit 8000000000 --no-container -- scripts/cpa.sh -heap 7000M -timelimit 900s -predicateAnalysis-PredAbsRefiner-ABElf -preprocess -noout -stats -spec sv-comp/PropertyERROR.prp sv-comp/ntdrivers/kbfiltr_false-unreach-call.i.cil.c
