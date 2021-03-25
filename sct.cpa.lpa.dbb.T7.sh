#!/bin/bash

for i in {43..69}
do
	python3 xmlforbenchgen.py cpa-lpa-dbb-X.xml oneerrlabel- $i cpa-lpa-dbb-$i.xml
	sudo benchexec cpa-lpa-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dbb* resultsbkup/
	sudo rm results/cpa-lpa-dbb*
	rm cpa-lpa-dbb-$i.xml
done

for i in {38..40}
do
	python3 xmlforbenchgen.py cpa-lpa-dbb-X.xml oneerrlabel- $i cpa-lpa-dbb-$i.xml
	sudo benchexec cpa-lpa-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dbb* resultsbkup/
	sudo rm results/cpa-lpa-dbb*
	rm cpa-lpa-dbb-$i.xml
done
