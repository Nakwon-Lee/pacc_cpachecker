#!/bin/bash

for i in {7..16}
do
	python3 xmlforbenchgen.py cpa-lmr-sbe-X.xml morerrlabel- $i cpa-lmr-sbe-$i.xml
	sudo benchexec cpa-lmr-sbe-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lmr-sbe* resultsbkup/
	sudo rm results/cpa-lmr-sbe*
	rm cpa-lmr-sbe-$i.xml
done
