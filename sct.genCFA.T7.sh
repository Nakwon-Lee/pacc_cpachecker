#!/bin/bash

for i in {43..69}
do
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done

for i in {38..40}
do
	sudo benchexec generateCFA-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/generateCFA* resultsbkup/
	sudo rm results/generateCFA*
done
