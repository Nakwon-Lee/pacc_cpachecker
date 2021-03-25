#!/bin/bash

for i in {0..13}
do
	python3 xmlforbenchgen.py cpa-lpa-mbb-X.xml oneerrlabel- $i cpa-lpa-mbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
	rm cpa-lpa-mbb-$i.xml
done
