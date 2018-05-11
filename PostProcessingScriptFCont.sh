#!/bin/bash
SUM_DIC=summaryPAFC/

rm $SUM_DIC*

sudo python3 PostProcessingPre.py $SUM_DIC

#cl0
for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl0PALFCont$i $SUM_DIC
done

#cl1
for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl1PALFCont$i $SUM_DIC
done

#cl2
sudo python3 PostProcessingFitSearch.py cl2PAFCont0 $SUM_DIC

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl2PALFCont$i $SUM_DIC
done

#cl3
for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl3PALFCont$i $SUM_DIC
done

#cl4
for i in {0..2}
do
	sudo python3 PostProcessingFitSearch.py cl4PAFCont$i $SUM_DIC
done

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl4PALFCont$i $SUM_DIC
done

#cl5
sudo python3 PostProcessingFitSearch.py cl5PAFCont0 $SUM_DIC

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl5PALFCont$i $SUM_DIC
done

#cl6
for i in {0..1}
do
	sudo python3 PostProcessingFitSearch.py cl6PAFCont$i $SUM_DIC
done

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl6PALFCont$i $SUM_DIC
done

#cl7
for i in {0..3}
do
	sudo python3 PostProcessingFitSearch.py cl7PAFCont$i $SUM_DIC
done

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl7PALFCont$i $SUM_DIC
done

#cl8
for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl8PALFCont$i $SUM_DIC
done

#cl9
sudo python3 PostProcessingFitSearch.py cl9PAFCont0 $SUM_DIC

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl9PALFCont$i $SUM_DIC
done


