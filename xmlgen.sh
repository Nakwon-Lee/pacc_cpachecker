#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-lpa-dlh-X.xml $i cpa-lpa-dlh-$i.xml
done
