#!/bin/bash

for i in {16..37}
do
	sudo benchexec cpa-bam-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dbb* resultsbkup/
	sudo rm results/cpa-bam-dbb*
done
