#!/bin/bash

for i in {26..32}
do
	python3 xmlforbenchgen.py cpa-lmr-mbb-X.xml morerrlabel- $i cpa-lmr-mbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lmr-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lmr-mbb* resultsbkup/
	sudo rm results/cpa-lmr-mbb*
	rm cpa-lmr-mbb-$i.xml
done
