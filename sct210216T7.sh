#!/bin/bash

for i in {43..69}
do
	# sudo benchexec cpa-lpa-sbe-$i.xml
	
	# sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	# sudo cp results/cpa-lpa-sbe* resultsbkup/
	# sudo rm results/cpa-lpa-sbe*
	
	sudo benchexec cpa-lpa-dmc-bb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
	
done

for i in {38..40}
do
	# sudo benchexec cpa-lpa-sbe-$i.xml
	
	# sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	# sudo cp results/cpa-lpa-sbe* resultsbkup/
	# sudo rm results/cpa-lpa-sbe*
	
	sudo benchexec cpa-lpa-dmc-bb-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
	
done

for i in {43..69}
do	
	sudo benchexec cpa-lpa-dmc-st-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
	
	sudo benchexec cpa-lpa-dmc-lh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
done

for i in {38..40}
do	
	sudo benchexec cpa-lpa-dmc-st-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
	
	sudo benchexec cpa-lpa-dmc-lh-$i.xml
	
	sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
	sudo cp results/cpa-lpa-dmc* resultsbkup/
	sudo rm results/cpa-lpa-dmc*
done
