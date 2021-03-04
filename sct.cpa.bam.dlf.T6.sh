#!/bin/bash

for i in {70..84}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dlf* resultsbkup/
	sudo rm results/cpa-bam-dlf*
done

for i in {14..15}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dlf* resultsbkup/
	sudo rm results/cpa-bam-dlf*
done

for i in {41..42}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dlf* resultsbkup/
	sudo rm results/cpa-bam-dlf*
done

sudo swapoff -a
sudo benchexec cpa-bam-dlf-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/cpa-bam-dlf* resultsbkup/
sudo rm results/cpa-bam-dlf*

