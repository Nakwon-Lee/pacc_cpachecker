#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-lpa-mbb-X.xml oneerrlabel- $i cpa-lpa-mbb-$i.xml
done
