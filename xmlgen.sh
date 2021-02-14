#!/bin/bash

for i in {0..99}
do
	python3 WhatMachine.py cpa-bam-bnb-X.xml $i cpa-bam-bnb-$i.xml
done
