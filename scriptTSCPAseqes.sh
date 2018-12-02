#!/bin/bash

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-CPAseq-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-CPAseq-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-CPAseqp-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-CPAseqp-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-ABElbp-CPAseqp-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABElbp-CPAseqp-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr
