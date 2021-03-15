#!/bin/bash

for i in {0..39}
do
	python3 xmlforbenchgen.py cpa-bmr-bnb-X.xml morerrlabel- $i cpa-bmr-bnb-$i.xml
done
