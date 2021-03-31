#!/bin/bash

for i in {33..39}
do
	python3 xmlforbenchgen.py cpa-lmr-dbb-X.xml morerrlabel- $i cpa-lmr-dbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lmr-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lmr-dbb* resultsbkup/
	sudo rm results/cpa-lmr-dbb*
	rm cpa-lmr-dbb-$i.xml
done
