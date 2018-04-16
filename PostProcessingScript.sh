#!/bin/bash
SUM_DIC=summaryPA/

rm $SUM_DIC*

sudo python3 PostProcessingPre.py $SUM_DIC

for i in {0..20}
do
	sudo python3 PostProcessingFitSearch.py result$i $SUM_DIC
done

for i in {0..17}
do
	sudo python3 PostProcessingFitSearch.py attempts$i $SUM_DIC
done

sudo python3 PostProcessingFitSearch.py remoT2PA0 $SUM_DIC
