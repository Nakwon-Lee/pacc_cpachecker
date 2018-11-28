#!/bin/bash

mv nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.results.TS-PA-ABElbp-RanTS-$3.txt nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.results.TS-PA-ABElbp-RanTS-$3.txt

mv nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.results.TS-PA-ABElbp-RanTS-$3.xml.bz2 nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.results.TS-PA-ABElbp-RanTS-$3.xml.bz2

mv nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.results.TS-PA-ABElbp-RanTS-$3.MachineFiles-$4.xml.bz2 nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.results.TS-PA-ABElbp-RanTS-$3.MachineFiles-$4.xml.bz2

mv nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.results.TS-PA-ABElbp-RanTS-$3.MachineFiles64-$4.xml.bz2 nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.results.TS-PA-ABElbp-RanTS-$3.MachineFiles64-$4.xml.bz2

unzip nwlee/m$4/benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.logfiles.zip

mv benchmark-TS-ABElbp-RanTS-machine.2018-11-$1.logfiles/ benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.logfiles/

zip benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.logfiles.zip benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.logfiles/*

mv benchmark-TS-ABElbp-RanTS-machine.2018-11-$2.logfiles.zip nwlee/m$4/
