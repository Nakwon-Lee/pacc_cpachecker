#!/bin/bash

for i in {70..84}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlh* resultsbkup/
	sudo rm results/cpa-lpa-dlh*
done

for i in {14..15}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlh* resultsbkup/
	sudo rm results/cpa-lpa-dlh*
done

for i in {41..42}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlh* resultsbkup/
	sudo rm results/cpa-lpa-dlh*
done

sudo swapoff -a
sudo benchexec cpa-lpa-dlh-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/cpa-lpa-dlh* resultsbkup/
sudo rm results/cpa-lpa-dlh*

