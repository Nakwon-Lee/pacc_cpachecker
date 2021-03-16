#!/bin/bash

for i in {0..39}
do
	python3 xmlforbenchgen.py cpa-bmr-dlf-X.xml morerrlabel- $i cpa-bmr-dlf-$i.xml
done
