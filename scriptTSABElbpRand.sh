#!/bin/bash

for i in {0..9}
do
  sudo swapoff -a
  python3 WhatMachineR.py doc/examples/benchmark-TS-ABElbp-RanTS-machine.xml $1 $i
  sudo ./scripts/benchexec --no-container doc/examples/benchmark-TS-ABElbp-RanTS-machine.xml
done
