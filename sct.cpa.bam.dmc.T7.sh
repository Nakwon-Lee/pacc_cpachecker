#!/bin/bash

for i in {43..69}
do
	sudo benchexec cpa-bam-dmc-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dmc* resultsbkup/
	sudo rm results/cpa-bam-dmc*
done

for i in {38..40}
do
	sudo benchexec cpa-bam-dmc-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dmc* resultsbkup/
	sudo rm results/cpa-bam-dmc*
done
