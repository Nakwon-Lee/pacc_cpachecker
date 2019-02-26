#!/bin/bash

for i in {6..30}
do
	python3 Makekresultup.py $i $1
	sudo swapoff -a
	python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-Exist1-machine.xml $1
	sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-Exist1-machine.xml
	sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/benchmark* resultsbkup/
	sudo rm results/benchmark*
	sudo swapoff -a
	python3 WhatMachine.py doc/examples/benchmark-TS-ABEl-AvoidChainEffect-machine.xml $1
	sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABEl-AvoidChainEffect-machine.xml
	sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/benchmark* resultsbkup/
	sudo rm results/benchmark*
done
