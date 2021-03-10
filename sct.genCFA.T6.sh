#!/bin/bash

for i in {70..84}
do
	sudo swapoff -a
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done

for i in {14..15}
do
	sudo swapoff -a
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done

for i in {41..42}
do
	sudo swapoff -a
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done

sudo swapoff -a
sudo benchexec generateCFA-94.xml

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/generateCFA* resultsbkup/
sudo rm results/generateCFA*

