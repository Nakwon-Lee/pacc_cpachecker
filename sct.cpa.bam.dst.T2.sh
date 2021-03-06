#!/bin/bash

for i in {0..13}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dst-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dst* resultsbkup/
	sudo rm results/cpa-bam-dst*
done
