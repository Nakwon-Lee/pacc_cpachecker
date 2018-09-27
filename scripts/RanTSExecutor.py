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

sys.dont_write_bytecode = True # prevent creation of .pyc files
for egg in glob.glob(os.path.join(os.path.dirname(__file__), os.pardir, 'lib', 'python-benchmark', '*.egg')):
    sys.path.insert(0, egg)

import benchexec.runexecutor
from TraversalStrategyModels import *

class RanTSExecutor:
	def __init__(self, labfuncs):
		self.atos = TSS.makingAtomTotalOrders(labfuncs)
		self.defaultargv = ['./scripts/RanTSExecutor.py', '--no-container', '--', 'scripts/cpa.sh', '-', '-heap', '-timelimit', '-preprocess', '-stats', '-noout', '-setprop', 'cpa.predicate.memoryAllocationsAlwaysSucceed=true', '-spec', '../sv-benchmarks/c/ReachSafety.prp']
		self.myargv = None

	def makeArgv(self, cores, memlimit, timelimit, algo, filen):
		self.myargv = copy.deepcopy(self.defaultargv)
		self.myargv[4] = self.myargv[4] + algo
		self.myargv.insert(6,str(int(memlimit*0.8)))
		self.myargv.insert(8,str(int(timelimit)))
		self.myargv.insert(1,str(cores))
		self.myargv.insert(1,"--cores")
		self.myargv.insert(1,str(timelimit*2))
		self.myargv.insert(1,"--walltimelimit")
		self.myargv.insert(1,str(memlimit))
		self.myargv.insert(1,"--memlimit")
		self.myargv.append(filen)

	def genRanTS(self, currxmlfile, searchstrategyjavafile):
		indiv = TraversalStrategy(self.atos)
		indiv.randomOdrGen()
		TSS.ttOdrToXML(indiv,currxmlfile)
		XtJ.xmltoJava(currxmlfile,searchstrategyjavafile)

	def Execute(self, handler):
		print(self.myargv)
		TSS.buildExecutable()
		benchexec.runexecutor.main(self.myargv)
		newvals = handler.other_after_run()
		return newvals

def main():
	outlog = 'output.log'
	fitvalsfile = 'fitvalues.csv'
	currxmlfile = 'currts.xml'
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/ABESearchStrategyFormula.java.java'
	labfuncs = (('isAbs',1,(0,1),0),('CS',0,1),('RPO',0,1),('CS',0,0),('blkD',0,0),('blkD',0,1),('RPO',0,0),('uID',0,0),('uID',0,1),('LenP',0,1),('LenP',0,0),('loopD',0,1),('loopD',0,0))

	mycore = 0 
	mytime = 900
	mymem = 12000000000
	myfile = sys.argv[1]
	myalgo = sys.argv[2]

	hdlr = TSS.MetricsHandler(outlog)

	executor = RanTSExecutor(labfuncs)

	#TODO generate a random TS
	executor.genRanTS(currxmlfile, searchstrategyjavafile)

	executor.makeArgv(mycore, mymem, mytime, myalgo, myfile)

	#TODO execute with the generated TS
	newvals = executor.Execute(hdlr)

	#assert len(newvals) == len(hdlr.fitvars), 'length of handled output missmatched'

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
