#!/bin/bash

for i in {70..84}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dbb* resultsbkup/
	sudo rm results/cpa-bam-dbb*
done

for i in {14..15}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dbb* resultsbkup/
	sudo rm results/cpa-bam-dbb*
done

for i in {41..42}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dbb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dbb* resultsbkup/
	sudo rm results/cpa-bam-dbb*
done

sudo swapoff -a
sudo benchexec cpa-bam-dbb-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/cpa-bam-dbb* resultsbkup/
sudo rm results/cpa-bam-dbb*

