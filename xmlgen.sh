#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-bam-dbb-X.xml $i cpa-bam-dbb-$i.xml
done
