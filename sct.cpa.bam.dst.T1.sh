#!/bin/bash

for i in {85..93}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dst-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dst* resultsbkup/
	sudo rm results/cpa-bam-dst*
done

for i in {95..99}
do
	sudo swapoff -a
	sudo benchexec cpa-bam-dst-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-bam-dst* resultsbkup/
	sudo rm results/cpa-bam-dst*
done
