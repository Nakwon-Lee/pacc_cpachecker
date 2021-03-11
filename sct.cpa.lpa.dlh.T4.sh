#!/bin/bash

for i in {16..37}
do
	sudo benchexec cpa-lpa-dlh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dlh* resultsbkup/
	sudo rm results/cpa-lpa-dlh*
done
