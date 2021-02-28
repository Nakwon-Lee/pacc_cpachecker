#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-bam-bmc-X.xml $i cpa-bam-bmc-$i.xml
	python3 xmlforbenchgen.py cpa-bam-bnb-X.xml $i cpa-bam-bnb-$i.xml
done
