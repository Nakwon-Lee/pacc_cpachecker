#!/bin/bash

for i in {1..5}
do
	sudo swapoff -a
	python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-Exist1-machine.xml $1
	sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-Exist1-machine.xml
	sftp -b kresultup spiralftp@spiral.kaist.ac.kr
        sudo swapoff -a
	python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-AvoidChainEffect-machine.xml $1
	sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-AvoidChainEffect-machine.xml
	sftp -b kresultup spiralftp@spiral.kaist.ac.kr
done
