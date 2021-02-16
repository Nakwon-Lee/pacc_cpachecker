#!/bin/bash

for i in {0..99}
do
	python3 xmlforbenchgen.py cpa-lpa-sbe-X.xml $i cpa-lpa-sbe-$i.xml
	python3 xmlforbenchgen.py cpa-lpa-dmc-bb-X.xml $i cpa-lpa-dmc-bb-$i.xml
	python3 xmlforbenchgen.py cpa-lpa-dmc-lh-X.xml $i cpa-lpa-dmc-lh-$i.xml
	python3 xmlforbenchgen.py cpa-lpa-dmc-st-X.xml $i cpa-lpa-dmc-st-$i.xml
done
