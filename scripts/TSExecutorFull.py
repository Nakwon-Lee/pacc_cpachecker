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
from FileCollector import FileCollector

sys.dont_write_bytecode = True # prevent creation of .pyc files
for egg in glob.glob(os.path.join(os.path.dirname(__file__), os.pardir, 'lib', 'python-benchmark', '*.egg')):
    sys.path.insert(0, egg)

import benchexec.runexecutor
from TraversalStrategyModels import *

def main():
	outlog = 'output.log'
	fitvalsprefull = 'fitvaluesFull'
	labfuncs = (('isAbs',1,(0,1),0),('CS',0,1),('RPO',0,1),('CS',0,0),('blkD',0,0),('blkD',0,1),('RPO',0,0),('uID',0,0),('uID',0,1),('LenP',0,1),('LenP',0,0),('loopD',0,1),('loopD',0,0))
	mycore = 0 
	mytime = int(sys.argv[7])
	mymem = 7000000000
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/ABESearchStrategyFormula.java'
	dirname = sys.argv[1]
	filename = sys.argv[2]
	csvfile = sys.argv[3]
	mydir = sys.argv[4]
	myalgo = sys.argv[5]
	ttodrxml = sys.argv[6]
	os.mkdir(mydir)

	hdlr = TSS.MetricsHandler(outlog)

	fc = None
	if dirname == 'GIVEN':
		fc = FileCollector(dirname, filename)
		fc.makeFilelistCsvGiven(csvfile, mydir, filename)
	elif dirname == 'GIVEN2':
		fc = FileCollector(dirname, filename)
		fc.makeFilelistCsvGiven2(csvfile, mydir, filename)
	else:
		fc = FileCollector(dirname, filename)
		fc.makeFilelistCsv(csvfile, mydir)

	for afile in fc.filelist:

		fitvalsfilefull = mydir + fitvalsprefull + afile['No.'] + '.csv'
		os.mkdir(mydir + 'ouputs' + afile['No.'] + '/')

		XtJ.xmltoJava(ttodrxml,searchstrategyjavafile)

		executor = RTS.RanTSExecutor(labfuncs)
		executor.makeArgv(mycore, mymem, mytime, myalgo, afile['file name'])
		newvals = executor.Execute(hdlr)

		os.system('mv output.log ' + mydir + 'ouputs' + afile['No.'] + '/' + 'output.log')

		#TODO save fitvars of the executed result
		csvfile = open(fitvalsfilefull, 'w')
		csvwriter = csv.DictWriter(csvfile, fieldnames=hdlr.fitvars)
		print(csvfile.tell())
		if csvfile.tell() == 0:
			csvwriter.writeheader()
		csvwriter.writerow(newvals)
		csvfile.close()

if __name__ == '__main__':
    main()
