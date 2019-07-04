#!/bin/bash

python3 Makekresultup.py 0 $1

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-Ex.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-Ex.xml
sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BATS.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BATS.xml
sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-Ex.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-Ex.xml
sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-BATS.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-BATS.xml
sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*
