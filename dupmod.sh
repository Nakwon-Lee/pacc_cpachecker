#!/bin/bash

mv results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.results.TS-PA-ABElbp-RanTS-$3.txt results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.results.TS-PA-ABElbp-RanTS-$3.txt

mv results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.results.TS-PA-ABElbp-RanTS-$3.xml.bz2 results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.results.TS-PA-ABElbp-RanTS-$3.xml.bz2

mv results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.results.TS-PA-ABElbp-RanTS-$3.MachineFiles-$4.xml.bz2 results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.results.TS-PA-ABElbp-RanTS-$3.MachineFiles-$4.xml.bz2

mv results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.results.TS-PA-ABElbp-RanTS-$3.MachineFiles64-$4.xml.bz2 results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.results.TS-PA-ABElbp-RanTS-$3.MachineFiles64-$4.xml.bz2

unzip results/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.logfiles.zip

mv benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.logfiles/ benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.logfiles/

zip benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.logfiles.zip benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.logfiles/*

mv benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.logfiles.zip results/
