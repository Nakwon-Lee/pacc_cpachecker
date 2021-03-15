#!/bin/bash

for i in {70..84}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dlh* resultsbkup/
	sudo rm results/cpa-bam-dlh*
done

for i in {14..15}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dlh* resultsbkup/
	sudo rm results/cpa-bam-dlh*
done

for i in {41..42}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dlh* resultsbkup/
	sudo rm results/cpa-bam-dlh*
done

sudo swapoff -a
sudo benchexec cpa-bam-dlh-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/cpa-bam-dlh* resultsbkup/
sudo rm results/cpa-bam-dlh*

