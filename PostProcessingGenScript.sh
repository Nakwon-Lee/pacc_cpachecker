#!/bin/bash

for i in {0..49}
do
	sudo python3 PostProcessingGeneralization.py summaryPA $i
done

for i in {0..11}
do
	sudo python3 PostProcessingGeneralization.py summaryPAL $i
done
