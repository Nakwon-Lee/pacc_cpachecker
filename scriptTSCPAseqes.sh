#!/bin/bash

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-CPAseqp-LoopStackSorted-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-CPAseqp-LoopStackSorted-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr

for i in {0..9}
do
  sudo swapoff -a
  python3 WhatMachine.py doc/examples/benchmark-TS-CPAseqp-Rand$i-machine.xml $1
  sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-CPAseqp-Rand$i-machine.xml
  sftp -b kresultup spiralftp@spiral.kaist.ac.kr
done
