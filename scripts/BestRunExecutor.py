from __future__ import absolute_import, division, print_function, unicode_literals

import sys
import csv
import glob
import os

import TtOdrXMLToJAVA as XtJ
import TSSearch as TSS

sys.dont_write_bytecode = True # prevent creation of .pyc files
for egg in glob.glob(os.path.join(os.path.dirname(__file__), os.pardir, 'lib', 'python-benchmark', '*.egg')):
    sys.path.insert(0, egg)

class BestRunExecutor:
	def __init__(self, pLabfuncs, pnloops, poutlog, pfitvars, poutfile, pttodrxml, psearchstrategyjavafile):
		self.executor = TSS.TSSearch(pLabfuncs)
		self.nloops = pnloops
		self.outlog = poutlog
		self.fitvars = pfitvars
		self.outfile = poutfile
		self.ttodrxml = pttodrxml
		self.searchstrategyjavafile = psearchstrategyjavafile

	def repeatedExecution(self, pcores, pmemlimit, ptimelimit, pfilen):

		XtJ.xmltoJava(self.ttodrxml,self.searchstrategyjavafile)

		self.executor.makeArgv(pcores, pmemlimit, ptimelimit, pfilen)

		for i in range(self.nloops):
			newvals = self.executor.Execute(self.outlog, self.fitvars)

			csvfile = open(self.outfile, 'a')
			csvwriter = csv.DictWriter(csvfile, fieldnames=self.fitvars)
			print(csvfile.tell())
			if csvfile.tell() == 0:
				csvwriter.writeheader()
			csvwriter.writerow(newvals)
			csvfile.close()

# ttodr xml file, number of repeatation
def main():

	labfuncs = (('isAbs',1,(0,1),0),('CS',0,1),('RPO',0,1),('CS',0,0),('blkD',0,0),('blkD',0,1),('RPO',0,0),('uID',0,0),('uID',0,1),('LenP',0,1),('LenP',0,0),('loopD',0,1),('loopD',0,0))

	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/MySearchStrategyFormula.java'

	outlog = 'output.log'
	fitvars = ('NoAffS','VL','VC','Time','Result','AFC','SFC','NoR')

	nloop = int(sys.argv[3])
	ttodrxml = sys.argv[1]
	outfile = sys.argv[2]

	mycore = 0
	mytime = 900
	mymem = 7000000000
	myfile = sys.argv[4]

	print('Traversal Strategy Search Start')

	brexecutor = BestRunExecutor(labfuncs, nloop, outlog, fitvars, outfile, ttodrxml, searchstrategyjavafile)

	print('Traversal Strategy Search Start')

	brexecutor.repeatedExecution(mycore, mymem, mytime, myfile)

if __name__ == '__main__':
    main()
