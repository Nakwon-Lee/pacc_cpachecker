#!/bin/bash

for i in {85..93}
do
	sudo swapoff -a
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done

for i in {95..99}
do
	sudo swapoff -a
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done
