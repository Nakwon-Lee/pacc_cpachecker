#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-lpa-dlf-X.xml $i cpa-lpa-dlf-$i.xml
done
