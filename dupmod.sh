#!/bin/bash

mv m$3/benchmark-TS-ABEl-Exist1-machine.$1.results.Test-PA-ABEl-Exist1.txt m$3/benchmark-TS-ABEl-Exist1-machine.$2.results.Test-PA-ABEl-Exist1.txt

mv m$3/benchmark-TS-ABEl-Exist1-machine.$1.results.Test-PA-ABEl-Exist1.xml.bz2 m$3/benchmark-TS-ABEl-Exist1-machine.$2.results.Test-PA-ABEl-Exist1.xml.bz2

mv m$3/benchmark-TS-ABEl-Exist1-machine.$1.results.Test-PA-ABEl-Exist1.MachineFiles-$3.xml.bz2 m$3/benchmark-TS-ABEl-Exist1-machine.$2.results.Test-PA-ABEl-Exist1.MachineFiles-$3.xml.bz2

mv m$3/benchmark-TS-ABEl-Exist1-machine.$1.results.Test-PA-ABEl-Exist1.MachineFiles64-$3.xml.bz2 m$3/benchmark-TS-ABEl-Exist1-machine.$2.results.Test-PA-ABEl-Exist1.MachineFiles64-$3.xml.bz2

unzip m$3/benchmark-TS-ABEl-Exist1-machine.$1.logfiles.zip

mv benchmark-TS-ABEl-Exist1-machine.$1.logfiles/ benchmark-TS-ABEl-Exist1-machine.$2.logfiles/

zip benchmark-TS-ABEl-Exist1-machine.$2.logfiles.zip benchmark-TS-ABEl-Exist1-machine.$2.logfiles/*

mv benchmark-TS-ABEl-Exist1-machine.$2.logfiles.zip m$3/
