#!/bin/bash
SUM_DIC=summaryPAL/

rm $SUM_DIC*

sudo python3 PostProcessingPre.py $SUM_DIC

#cl0
sudo python3 PostProcessingFitSearch.py cl0PAL0 $SUM_DIC

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl0PALL$i $SUM_DIC
done

#cl1
sudo python3 PostProcessingFitSearch.py cl1PAL0 $SUM_DIC

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl1PALL$i $SUM_DIC
done

#cl2
sudo python3 PostProcessingFitSearch.py cl2PAL0 $SUM_DIC

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl2PALL$i $SUM_DIC
done

#cl3
sudo python3 PostProcessingFitSearch.py cl3PAL0 $SUM_DIC

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl3PALL$i $SUM_DIC
done

#cl4
for i in {0..3}
do
	sudo python3 PostProcessingFitSearch.py cl4PAL$i $SUM_DIC
done

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl4PALL$i $SUM_DIC
done

#cl5
for i in {1..4}
do
	sudo python3 PostProcessingFitSearch.py cl5PAL$i $SUM_DIC
done

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl5PALL$i $SUM_DIC
done

#cl6
for i in {0..1}
do
	sudo python3 PostProcessingFitSearch.py cl6PAL$i $SUM_DIC
done

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl6PALL$i $SUM_DIC
done

#cl7
for i in {1..8}
do
	sudo python3 PostProcessingFitSearch.py cl7PAL$i $SUM_DIC
done

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl7PALL$i $SUM_DIC
done

#cl8
sudo python3 PostProcessingFitSearch.py cl8PAL0 $SUM_DIC

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl8PALL$i $SUM_DIC
done

#cl9
for i in {1..7}
do
	sudo python3 PostProcessingFitSearch.py cl9PAL$i $SUM_DIC
done

for i in {0..26}
do
	sudo python3 PostProcessingFitSearch.py cl9PALL$i $SUM_DIC
done
