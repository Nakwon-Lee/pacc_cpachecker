#!/bin/bash

sudo python3 ./scripts/TSSearch.py --timelimit 200s --walltimelimit 200s --cores 0 --memlimit 8000000000 --no-container -- scripts/cpa.sh -heap 7000M -timelimit 100s -Dy-MySearchStrategy-PredAbs-ABElf -preprocess -noout -stats -spec sv-comp/PropertyERROR.prp sv-comp/ntdrivers/floppy_true-unreach-call.i.cil.c
