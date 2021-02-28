#!/bin/bash

for i in {0..13}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dmc-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dmc* resultsbkup/
	sudo rm results/cpa-bam-dmc*
	
	sudo swapoff -a
	sudo benchexec cpa-bam-bnb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-bnb* resultsbkup/
	sudo rm results/cpa-bam-bnb*
	
done
