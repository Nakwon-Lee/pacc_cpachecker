#!/bin/bash

for i in {16..37}
do
	sudo benchexec cpa-lpa-dlf-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlf* resultsbkup/
	sudo rm results/cpa-lpa-dlf*
done
