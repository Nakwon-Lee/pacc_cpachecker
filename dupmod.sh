#!/bin/bash

mv m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$1.results.Test-PA-ABEl-CE.txt m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$2.results.Test-PA-ABEl-CE.txt

mv m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$1.results.Test-PA-ABEl-CE.xml.bz2 m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$2.results.Test-PA-ABEl-CE.xml.bz2

mv m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$1.results.Test-PA-ABEl-CE.MachineFiles-$3.xml.bz2 m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$2.results.Test-PA-ABEl-CE.MachineFiles-$3.xml.bz2

mv m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$1.results.Test-PA-ABEl-CE.MachineFiles64-$3.xml.bz2 m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$2.results.Test-PA-ABEl-CE.MachineFiles64-$3.xml.bz2

unzip m$3/benchmark-TS-ABEl-AvoidChainEffect-machine.$1.logfiles.zip

mv benchmark-TS-ABEl-AvoidChainEffect-machine.$1.logfiles/ benchmark-TS-ABEl-AvoidChainEffect-machine.$2.logfiles/

zip benchmark-TS-ABEl-AvoidChainEffect-machine.$2.logfiles.zip benchmark-TS-ABEl-AvoidChainEffect-machine.$2.logfiles/*

mv benchmark-TS-ABEl-AvoidChainEffect-machine.$2.logfiles.zip m$3/
