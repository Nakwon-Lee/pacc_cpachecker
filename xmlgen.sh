#!/bin/bash

for i in {0..39}
do
	python3 xmlforbenchgen.py cpa-bmr-dlh-X.xml morerrlabel- $i cpa-bmr-dlh-$i.xml
done
