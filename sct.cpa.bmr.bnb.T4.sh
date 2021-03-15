#!/bin/bash

for i in {7..16}
do
	sudo benchexec cpa-bmr-bnb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bmr-bnb* resultsbkup/
	sudo rm results/cpa-bmr-bnb*
done
