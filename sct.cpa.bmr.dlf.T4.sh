#!/bin/bash

for i in {7..16}
do
	sudo benchexec cpa-bmr-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bmr-dlf* resultsbkup/
	sudo rm results/cpa-bmr-dlf*
done
