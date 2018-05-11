#!/bin/bash
SUM_DIC=summaryPALC/

rm $SUM_DIC*

sudo python3 PostProcessingPre.py $SUM_DIC

#cl0
for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl0PALLCont$i $SUM_DIC
done

#cl1
for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl1PALLCont$i $SUM_DIC
done

#cl2
sudo python3 PostProcessingFitSearch.py cl2PALCont0 $SUM_DIC

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl2PALLCont$i $SUM_DIC
done

#cl3
for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl3PALLCont$i $SUM_DIC
done

#cl4
for i in {0..2}
do
	sudo python3 PostProcessingFitSearch.py cl4PALCont$i $SUM_DIC
done

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl4PALLCont$i $SUM_DIC
done

#cl5
sudo python3 PostProcessingFitSearch.py cl5PALCont0 $SUM_DIC

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl5PALLCont$i $SUM_DIC
done

#cl6
for i in {0..1}
do
	sudo python3 PostProcessingFitSearch.py cl6PALCont$i $SUM_DIC
done

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl6PALLCont$i $SUM_DIC
done

#cl7
for i in {0..3}
do
	sudo python3 PostProcessingFitSearch.py cl7PALCont$i $SUM_DIC
done

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl7PALLCont$i $SUM_DIC
done

#cl8
for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl8PALLCont$i $SUM_DIC
done

#cl9
sudo python3 PostProcessingFitSearch.py cl9PALCont0 $SUM_DIC

for i in {0..28}
do
	sudo python3 PostProcessingFitSearch.py cl9PALLCont$i $SUM_DIC
done


