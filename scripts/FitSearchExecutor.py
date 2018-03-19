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
from RanTSExecutor import RanTSExecutor
from FileCollector import FileCollector

sys.dont_write_bytecode = True # prevent creation of .pyc files
for egg in glob.glob(os.path.join(os.path.dirname(__file__), os.pardir, 'lib', 'python-benchmark', '*.egg')):
    sys.path.insert(0, egg)

import benchexec.runexecutor
from TraversalStrategyModels import *

def main():
	outlog = 'output.log'
	fitvalspre = 'fitvalues'
	fitvalsprefull = 'fitvaluesFull'
	currxmlfile = 'tsxml'
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/MySearchStrategyFormula.java'
	fitvars = ('NoAffS','VL','VC','Time','Result','AFC','SFC','NoR')
	labfuncs = (('isAbs',1,(0,1),0),('CS',0,1),('RPO',0,1),('CS',0,0),('blkD',0,0),('blkD',0,1),('RPO',0,0),('uID',0,0),('uID',0,1),('LenP',0,1),('LenP',0,0),('loopD',0,1),('loopD',0,0))

	mycore = 0 
	mytime = 30
	mytimefull = 900
	mymem = 7000000000
	dirname = sys.argv[1]
	filename = sys.argv[2]
	csvfile = sys.argv[3]
	mydir = sys.argv[4]
	myalgo = sys.argv[5]
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

		fitvalsfile = mydir + fitvalspre + afile['No.'] + '.csv'
		fitvalsfilefull = mydir + fitvalsprefull + afile['No.'] + '.csv'
		os.mkdir(mydir + currxmlfile + afile['No.'] + '/')

		for i in range(30):

			tsxmlcurr = mydir + currxmlfile + afile['No.'] + '/' + currxmlfile + str(i) + '.xml'

			executor = RanTSExecutor(labfuncs)

			#TODO generate a random TS
			executor.genRanTS(tsxmlcurr, searchstrategyjavafile)

			executor.makeArgv(mycore, mymem, mytime, myalgo, afile['file name'])

			#TODO execute with the generated TS
			newvals = executor.Execute(hdlr)

			os.system('mv output.log ' + mydir + currxmlfile + afile['No.'] + '/' + 'output' + str(i) + '.log')

			#assert len(newvals) == len(hdlr.fitvars), 'length of handled output missmatched'

			#TODO save fitvars of the executed result
			csvfile = open(fitvalsfile, 'a')
			csvwriter = csv.DictWriter(csvfile, fieldnames=hdlr.fitvars)
			print(csvfile.tell())
			if csvfile.tell() == 0:
				csvwriter.writeheader()
			csvwriter.writerow(newvals)
			csvfile.close()

			# for full execution
			executor = RanTSExecutor(labfuncs)
			executor.makeArgv(mycore, mymem, mytimefull, myalgo, afile['file name'])
			newvals = executor.Execute(hdlr)

			os.system('mv output.log ' + mydir + currxmlfile + afile['No.'] + '/' + 'outputF' + str(i) + '.log')

			#TODO save fitvars of the executed result
			csvfile = open(fitvalsfilefull, 'a')
			csvwriter = csv.DictWriter(csvfile, fieldnames=hdlr.fitvars)
			print(csvfile.tell())
			if csvfile.tell() == 0:
				csvwriter.writeheader()
			csvwriter.writerow(newvals)
			csvfile.close()

if __name__ == '__main__':
	main()
