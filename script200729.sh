#!/bin/bash

python3 Makekresultup.py 0 $1

#main analysis

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-BATS.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-BATS.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-Ex.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-Ex.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

#witness analysis
