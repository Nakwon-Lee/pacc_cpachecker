#!/bin/bash

for i in {70..84}
do
	python3 xmlforbenchgen.py cpa-lpa-dbb-X.xml oneerrlabel- $i cpa-lpa-dbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dbb* resultsbkup/
	sudo rm results/cpa-lpa-dbb*
	rm cpa-lpa-dbb-$i.xml
done

for i in {14..15}
do
	python3 xmlforbenchgen.py cpa-lpa-dbb-X.xml oneerrlabel- $i cpa-lpa-dbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dbb* resultsbkup/
	sudo rm results/cpa-lpa-dbb*
	rm cpa-lpa-dbb-$i.xml
done

for i in {41..42}
do
	python3 xmlforbenchgen.py cpa-lpa-dbb-X.xml oneerrlabel- $i cpa-lpa-dbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dbb* resultsbkup/
	sudo rm results/cpa-lpa-dbb*
	rm cpa-lpa-dbb-$i.xml
done

python3 xmlforbenchgen.py cpa-lpa-dbb-X.xml oneerrlabel- 94 cpa-lpa-dbb-94.xml
sudo swapoff -a
sudo benchexec cpa-lpa-dbb-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/cpa-lpa-dbb* resultsbkup/
sudo rm results/cpa-lpa-dbb*
rm cpa-lpa-dbb-94.xml

