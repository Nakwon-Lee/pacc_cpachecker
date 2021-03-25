#!/bin/bash

for i in {70..84}
do
	python3 xmlforbenchgen.py cpa-lpa-mbb-X.xml oneerrlabel- $i cpa-lpa-mbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
	rm cpa-lpa-mbb-$i.xml
done

for i in {14..15}
do
	python3 xmlforbenchgen.py cpa-lpa-mbb-X.xml oneerrlabel- $i cpa-lpa-mbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
	rm cpa-lpa-mbb-$i.xml
done

for i in {41..42}
do
	python3 xmlforbenchgen.py cpa-lpa-mbb-X.xml oneerrlabel- $i cpa-lpa-mbb-$i.xml
	sudo swapoff -a
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
	rm cpa-lpa-mbb-$i.xml
done

python3 xmlforbenchgen.py cpa-lpa-mbb-X.xml oneerrlabel- 94 cpa-lpa-mbb-94.xml
sudo swapoff -a
sudo benchexec cpa-lpa-mbb-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/cpa-lpa-mbb* resultsbkup/
sudo rm results/cpa-lpa-mbb*
rm cpa-lpa-mbb-94.xml

