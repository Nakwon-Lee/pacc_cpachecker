#!/bin/bash

for i in {17..25}
do
	python3 xmlforbenchgen.py cpa-bmr-dst-X.xml morerrlabel- $i cpa-bmr-dst-$i.xml
	sudo benchexec cpa-bmr-dst-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bmr-dst* resultsbkup/
	sudo rm results/cpa-bmr-dst*
	rm cpa-bmr-dst-$i.xml
done
