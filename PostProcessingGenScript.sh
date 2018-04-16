#!/bin/bash

for i in {0..63}
do
	sudo python3 PostProcessingGeneralization.py summary $i
done
