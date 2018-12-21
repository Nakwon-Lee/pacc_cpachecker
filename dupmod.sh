#!/bin/bash

mv m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$1.results.Test-CPAseqp-CE3.txt m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$2.results.Test-CPAseqp-CE3.txt

mv m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$1.results.Test-CPAseqp-CE3.xml.bz2 m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$2.results.Test-CPAseqp-CE3.xml.bz2

mv m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$1.results.Test-CPAseqp-CE3.MachineFiles-$3.xml.bz2 m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$2.results.Test-CPAseqp-CE3.MachineFiles-$3.xml.bz2

mv m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$1.results.Test-CPAseqp-CE3.MachineFiles64-$3.xml.bz2 m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$2.results.Test-CPAseqp-CE3.MachineFiles64-$3.xml.bz2

unzip m$3/benchmark-TS-CPAseqp-CE3-machine.2018-$1.logfiles.zip

mv benchmark-TS-CPAseqp-CE3-machine.2018-$1.logfiles/ benchmark-TS-CPAseqp-CE3-machine.2018-$2.logfiles/

zip benchmark-TS-CPAseqp-CE3-machine.2018-$2.logfiles.zip benchmark-TS-CPAseqp-CE3-machine.2018-$2.logfiles/*

mv benchmark-TS-CPAseqp-CE3-machine.2018-$2.logfiles.zip m$3/
