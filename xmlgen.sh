#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-bmr-bnb-X.xml $i cpa-bmr-bnb-$i.xml
done
