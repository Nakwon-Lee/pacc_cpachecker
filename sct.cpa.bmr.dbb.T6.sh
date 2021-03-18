#!/bin/bash

for i in {26..32}
do
	sudo swapoff -a
	sudo benchexec cpa-bmr-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bmr-dbb* resultsbkup/
	sudo rm results/cpa-bmr-dbb*
done
