#!/bin/bash

for i in {1..10}
do
	python3 Makekresultup.py $i $1
	sudo swapoff -a
	python3 WhatMachine.py doc/examples/benchmark-TS-ABElf-BAM-Exist1.xml $1
	sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABElf-BAM-Exist1.xml
	sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/benchmark* resultsbkup/
	sudo rm results/benchmark*
	sudo swapoff -a
	python3 WhatMachine.py doc/examples/benchmark-TS-ABElf-BAM-CE.xml $1
	sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABElf-BAM-CE.xml
	sftp -b sftpbatches/kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/benchmark* resultsbkup/
	sudo rm results/benchmark*
done
