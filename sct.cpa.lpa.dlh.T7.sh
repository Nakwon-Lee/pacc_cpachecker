#!/bin/bash

for i in {43..69}
do
	sudo benchexec cpa-lpa-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlh* resultsbkup/
	sudo rm results/cpa-lpa-dlh*
done

for i in {38..40}
do
	sudo benchexec cpa-lpa-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlh* resultsbkup/
	sudo rm results/cpa-lpa-dlh*
done
