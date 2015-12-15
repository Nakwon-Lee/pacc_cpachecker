#!/bin/bash

for ((i=0;i<10;i++)); do
    ./scripts/benchmark.py ./doc/examples/benchmark_My.xml
done
