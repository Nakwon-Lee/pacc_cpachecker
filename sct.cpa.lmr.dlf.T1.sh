#!/bin/bash

for i in {33..39}
do
	python3 xmlforbenchgen.py cpa-lmr-dlf-X.xml morerrlabel- $i cpa-lmr-dlf-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lmr-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lmr-dlf* resultsbkup/
	sudo rm results/cpa-lmr-dlf*
	rm cpa-lmr-dlf-$i.xml
done
