#!/bin/bash

for i in {17..25}
do
	python3 xmlforbenchgen.py gmrerateCFA-X.xml morerrlabel- $i gmrerateCFA-$i.xml
	sudo benchexec gmrerateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/gmrerateCFA* resultsbkup/
	sudo rm results/gmrerateCFA*
	rm gmrerateCFA-$i.xml
done
