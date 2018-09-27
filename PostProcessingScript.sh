#!/bin/bash
SUM_DIC=summaryPAF/

rm $SUM_DIC*

sudo python3 PostProcessingPre.py $SUM_DIC

for i in {0..1}
do
	sudo python3 PostProcessingFitSearch.py cl0PA$i $SUM_DIC
done

for i in {0..4}
do
	sudo python3 PostProcessingFitSearch.py cl1PA$i $SUM_DIC
done

sudo python3 PostProcessingFitSearch.py cl2PA0 $SUM_DIC

for i in {2..6}
do
	sudo python3 PostProcessingFitSearch.py cl2PA$i $SUM_DIC
done

for i in {0..4}
do
	sudo python3 PostProcessingFitSearch.py cl3PA$i $SUM_DIC
done

for i in {1..8}
do
	sudo python3 PostProcessingFitSearch.py cl4PA$i $SUM_DIC
done

for i in {0..1}
do
	sudo python3 PostProcessingFitSearch.py cl5PA$i $SUM_DIC
done

for i in {2..5}
do
	sudo python3 PostProcessingFitSearch.py cl6PA$i $SUM_DIC
done

for i in {0..2}
do
	sudo python3 PostProcessingFitSearch.py cl7PA$i $SUM_DIC
done

for i in {0..1}
do
	sudo python3 PostProcessingFitSearch.py cl8PA$i $SUM_DIC
done

for i in {0..10}
do
	sudo python3 PostProcessingFitSearch.py cl9PA$i $SUM_DIC
done
