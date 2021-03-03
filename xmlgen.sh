#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-bam-dlf-X.xml $i cpa-bam-dlf-$i.xml
done
