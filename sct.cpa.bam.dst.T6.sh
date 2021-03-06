#!/bin/bash

for i in {70..84}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dst-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dst* resultsbkup/
	sudo rm results/cpa-bam-dst*
done

for i in {14..15}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dst-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dst* resultsbkup/
	sudo rm results/cpa-bam-dst*
done

for i in {41..42}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dst-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dst* resultsbkup/
	sudo rm results/cpa-bam-dst*
done

sudo swapoff -a
sudo benchexec cpa-bam-dst-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/cpa-bam-dst* resultsbkup/
sudo rm results/cpa-bam-dst*

