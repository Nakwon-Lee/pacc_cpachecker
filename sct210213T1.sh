#!/bin/bash

for i in {95..99}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-bnb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-bnb* resultsbkup/
	sudo rm results/cpa-bam-bnb*
done
