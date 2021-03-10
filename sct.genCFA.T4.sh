#!/bin/bash

for i in {16..37}
do
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done
