#!/bin/bash

for i in {16..37}
do
	sudo benchexec cpa-bam-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dlh* resultsbkup/
	sudo rm results/cpa-bam-dlh*
done
