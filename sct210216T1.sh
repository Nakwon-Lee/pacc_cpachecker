#!/bin/bash

for i in {85..93}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-sbe-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-sbe* resultsbkup/
	sudo rm results/cpa-lpa-sbe*
	
	sudo swapoff -a
	sudo benchexec cpa-lpa-dmc-bb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
	
done

for i in {95..99}
do
	sudo swapoff -a
	sudo benchexec cpa-lpa-sbe-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-sbe* resultsbkup/
	sudo rm results/cpa-lpa-sbe*
	
	sudo swapoff -a
	sudo benchexec cpa-lpa-dmc-bb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
	
done

for i in {85..93}
do	
	sudo swapoff -a
	sudo benchexec cpa-lpa-dmc-st-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
	
	sudo swapoff -a
	sudo benchexec cpa-lpa-dmc-lh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
done

for i in {95..99}
do	
	sudo swapoff -a
	sudo benchexec cpa-lpa-dmc-st-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
	
	sudo swapoff -a
	sudo benchexec cpa-lpa-dmc-lh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
done
