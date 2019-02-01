#!/bin/bash

git checkout -- doc/ kresultup
sudo rm results/benchmark*
git pull
sudo swapoff -a
sudo nohup ./scriptTSRQ1.sh $1 &
