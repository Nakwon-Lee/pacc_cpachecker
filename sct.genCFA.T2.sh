#!/bin/bash

for i in {0..13}
do
	sudo swapoff -a
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done
