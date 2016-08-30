#!/bin/bash

sudo mount -t cgroup none /sys/fs/cgroup
sudo chmod o+wt '/sys/fs/cgroup/'
sudo swapoff -a

