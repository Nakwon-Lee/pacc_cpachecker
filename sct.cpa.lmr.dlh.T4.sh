#!/bin/bash

for i in {7..16}
do
	python3 xmlforbenchgen.py cpa-lmr-dlh-X.xml morerrlabel- $i cpa-lmr-dlh-$i.xml
	sudo benchexec cpa-lmr-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lmr-dlh* resultsbkup/
	sudo rm results/cpa-lmr-dlh*
	rm cpa-lmr-dlh-$i.xml
done
