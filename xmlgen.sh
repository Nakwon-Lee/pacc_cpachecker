#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-bam-dst-X.xml $i cpa-bam-dst-$i.xml
done
