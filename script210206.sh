#!/bin/bash

sudo benchexec $1

sudo sftp -b kresultup spiralftp@spiral.kaist.ac.kr
sudo cp results/generate* resultsbkup/
sudo rm results/generate*
