#!/bin/bash

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-Exist1-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-Exist1-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-BlkBFS-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-BlkBFS-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-BlkDFS-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-BlkDFS-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-AvoidChainEffect-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-AvoidChainEffect-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr

sudo swapoff -a
python3 WhatMachine.py doc/examples/benchmark-TS-ABElbp-AvoidChainEffect-machine.xml $1
sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABElbp-AvoidChainEffect-machine.xml
sftp -b kresultup spiralftp@spiral.kaist.ac.kr
