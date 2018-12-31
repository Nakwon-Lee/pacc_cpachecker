#!/bin/bash

for i in {0..9}
do
  sudo swapoff -a
  python3 WhatMachine.py doc/examples/AGR/benchmark-TS-CPAseqp-AGR$i-machine.xml $1
  sudo ./scripts/benchexec --no-container doc/examples/AGR/benchmark-TS-CPAseqp-AGR$i-machine.xml
  sftp -b kresultup spiralftp@spiral.kaist.ac.kr
done
