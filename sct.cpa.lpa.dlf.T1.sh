#!/bin/bash

for i in {85..93}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlf* resultsbkup/
	sudo rm results/cpa-lpa-dlf*
done

for i in {95..99}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlf* resultsbkup/
	sudo rm results/cpa-lpa-dlf*
done
