#!/bin/bash

sudo python3 ./scripts/benchmark.py 'test/test-sets/dy-ts-orig.xml'

sudo python3 ./scripts/benchmark.py 'test/test-sets/dy-ts-blkbfs.xml'

sudo python3 ./scripts/benchmark.py 'test/test-sets/dy-ts-csrpo.xml'

sudo python3 ./scripts/benchmark.py 'test/test-sets/dy-ts-blkdfs.xml'
