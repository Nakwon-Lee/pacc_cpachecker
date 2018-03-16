from __future__ import absolute_import, division, print_function, unicode_literals

import glob
import os
import sys
import csv
import shutil
import xml.etree.ElementTree as ET
import TtOdrXMLToJAVA as XtJ
import copy
import GeneticAlgo as GA
import TSSearch as TSS
import RanTSExecutor as RTS

sys.dont_write_bytecode = True # prevent creation of .pyc files
for egg in glob.glob(os.path.join(os.path.dirname(__file__), os.pardir, 'lib', 'python-benchmark', '*.egg')):
    sys.path.insert(0, egg)

import benchexec.runexecutor
from TraversalStrategyModels import *

def main():
	outlog = 'output.log'
	fitvalsfile = 'fitvaluesFull.csv'
	labfuncs = (('isAbs',1,(0,1),0),('CS',0,1),('RPO',0,1),('CS',0,0),('blkD',0,0),('blkD',0,1),('RPO',0,0),('uID',0,0),('uID',0,1),('LenP',0,1),('LenP',0,0),('loopD',0,1),('loopD',0,0))
	mycore = 0 
	mytime = 900
	mymem = 7000000000
	myfile = sys.argv[1]
	myalgo = sys.argv[2]

	print(sys.argv)

	hdlr = TSS.MetricsHandler(outlog)

	executor = RTS.RanTSExecutor(labfuncs)
	executor.makeArgv(mycore, mymem, mytime, myalgo, myfile)
	newvals = executor.Execute(hdlr)

	#TODO save fitvars of the executed result
	csvfile = open(fitvalsfile, 'a')
	csvwriter = csv.DictWriter(csvfile, fieldnames=hdlr.fitvars)
	print(csvfile.tell())
	if csvfile.tell() == 0:
		csvwriter.writeheader()
	csvwriter.writerow(newvals)
	csvfile.close()

if __name__ == '__main__':
    main()
