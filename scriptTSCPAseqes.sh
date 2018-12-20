#!/bin/bash

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-CPAseqp-CE3-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-CPAseqp-CE3-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr
