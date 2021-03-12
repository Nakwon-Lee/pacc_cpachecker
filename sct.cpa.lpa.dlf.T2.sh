#!/bin/bash

for i in {0..13}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlf* resultsbkup/
	sudo rm results/cpa-lpa-dlf*
done
