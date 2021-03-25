#!/bin/bash

for i in {85..93}
do
	python3 xmlforbenchgen.py cpa-lpa-dbb-X.xml oneerrlabel- $i cpa-lpa-dbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dbb* resultsbkup/
	sudo rm results/cpa-lpa-dbb*
	rm cpa-lpa-dbb-$i.xml
done

for i in {95..99}
do
	python3 xmlforbenchgen.py cpa-lpa-dbb-X.xml oneerrlabel- $i cpa-lpa-dbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dbb* resultsbkup/
	sudo rm results/cpa-lpa-dbb*
	rm cpa-lpa-dbb-$i.xml
done
