#!/bin/bash

python3 Makekresultup.py 0 $1

#main analysis
sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-Ex.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-Ex.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BATS.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BATS.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-Ex.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-Ex.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-BATS.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-BATS.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

#witness analysis
#LAEx
sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-Ex-witness.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-Ex-witness.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-Ex-violation.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-Ex-violation.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*
#LABATS
sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BATS-witness.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BATS-witness.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BATS-violation.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BATS-violation.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*
#BAMEx
sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-Ex-witness.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-Ex-witness.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-Ex-violation.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-Ex-violation.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*
#BAMBATS
sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-BATS-witness.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-BATS-witness.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-LPAABE-BAM-BATS-violation.xml $1
sudo benchexec --no-container doc/examples/benchmark-LPAABE-BAM-BATS-violation.xml
sudo sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/benchmark* resultsbkup/
sudo rm results/benchmark*
