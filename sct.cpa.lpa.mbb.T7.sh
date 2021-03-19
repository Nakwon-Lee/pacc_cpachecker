#!/bin/bash

for i in {43..69}
do
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
done

for i in {38..40}
do
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
done
