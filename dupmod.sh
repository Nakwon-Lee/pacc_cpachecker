#!/bin/bash

mv m$3/benchmark-TS-CPAseqp-AGR9-machine.$1.results.Test-CPAseqp-AGR9.txt m$3/benchmark-TS-CPAseqp-AGR9-machine.$2.results.Test-CPAseqp-AGR9.txt

mv m$3/benchmark-TS-CPAseqp-AGR9-machine.$1.results.Test-CPAseqp-AGR9.xml.bz2 m$3/benchmark-TS-CPAseqp-AGR9-machine.$2.results.Test-CPAseqp-AGR9.xml.bz2

mv m$3/benchmark-TS-CPAseqp-AGR9-machine.$1.results.Test-CPAseqp-AGR9.MachineFiles-$3.xml.bz2 m$3/benchmark-TS-CPAseqp-AGR9-machine.$2.results.Test-CPAseqp-AGR9.MachineFiles-$3.xml.bz2

mv m$3/benchmark-TS-CPAseqp-AGR9-machine.$1.results.Test-CPAseqp-AGR9.MachineFiles64-$3.xml.bz2 m$3/benchmark-TS-CPAseqp-AGR9-machine.$2.results.Test-CPAseqp-AGR9.MachineFiles64-$3.xml.bz2

unzip m$3/benchmark-TS-CPAseqp-AGR9-machine.$1.logfiles.zip

mv benchmark-TS-CPAseqp-AGR9-machine.$1.logfiles/ benchmark-TS-CPAseqp-AGR9-machine.$2.logfiles/

zip benchmark-TS-CPAseqp-AGR9-machine.$2.logfiles.zip benchmark-TS-CPAseqp-AGR9-machine.$2.logfiles/*

mv benchmark-TS-CPAseqp-AGR9-machine.$2.logfiles.zip m$3/
