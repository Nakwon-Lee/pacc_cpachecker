#!/bin/bash

for i in {70..84}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
done

for i in {14..15}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
done

for i in {41..42}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-mbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-mbb* resultsbkup/
	sudo rm results/cpa-lpa-mbb*
done

sudo swapoff -a
sudo benchexec cpa-lpa-mbb-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/cpa-lpa-mbb* resultsbkup/
sudo rm results/cpa-lpa-mbb*

