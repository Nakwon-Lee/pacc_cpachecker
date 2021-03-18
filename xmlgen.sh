#!/bin/bash

for i in {0..39}
do
	python3 xmlforbenchgen.py cpa-bmr-dbb-X.xml morerrlabel- $i cpa-bmr-dbb-$i.xml
done
