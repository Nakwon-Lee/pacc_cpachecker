#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py generateCFA-X.xml $i generateCFA-$i.xml
done
