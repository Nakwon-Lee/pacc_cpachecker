#!/bin/bash

for i in {0..6}
do
	python3 xmlforbenchgen.py cpa-lmr-dst-X.xml morerrlabel- $i cpa-lmr-dst-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lmr-dst-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lmr-dst* resultsbkup/
	sudo rm results/cpa-lmr-dst*
	rm cpa-lmr-dst-$i.xml
done
