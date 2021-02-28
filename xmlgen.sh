#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-bam-dmc-X.xml $i cpa-bam-dmc-$i.xml
	python3 xmlforbenchgen.py cpa-bam-bnb-X.xml $i cpa-bam-bnb-$i.xml
done
