#!/bin/bash

for i in {0..9}
do
  sudo swapoff -a
  python3 WhatMachine.py doc/examples/valuerands/benchmark-TS-CPAseqp-Rand$i-machine.xml $1
  sudo ./scripts/benchexec --no-container doc/examples/valuerands/benchmark-TS-CPAseqp-Rand$i-machine.xml
  sftp -b kresultup spiralftp@spiral.kaist.ac.kr
done
