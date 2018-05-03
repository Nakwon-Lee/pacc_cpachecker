from __future__ import absolute_import, division, print_function, unicode_literals

import glob
import os
import sys
import shutil
import xml.etree.ElementTree as ET
import TtOdrXMLToJAVA as XtJ
import copy
import GeneticAlgo as GA
import Comparator as Comp

sys.dont_write_bytecode = True # prevent creation of .pyc files
for egg in glob.glob(os.path.join(os.path.dirname(__file__), os.pardir, 'lib', 'python-benchmark', '*.egg')):
    sys.path.insert(0, egg)

import benchexec.runexecutor
import benchexec.tools.cpachecker as cpaTool
import benchexec.util as util
from TraversalStrategyModels import *

def buildExecutable():
	tool = cpaTool.Tool()
	tool.executable()

def makingAtomTotalOrders(labfuncs):

	atos = []

	for labfunc in labfuncs:
		if labfunc[1] == 1: #TtOdr with finite domain
			ato = FiniteDomainTotalOrder(labfunc[0],labfunc[3],labfunc[2])
			atos.append(ato)
		elif labfunc[1] == 0: #TtOdr with infinite domain
			ato = AtomicTotalOrder(labfunc[0],labfunc[2])
			atos.append(ato)
		else:
			print('labfunc should be finite or infinite domain')

	return atos

def ttOdrToXML(pttOdr,pxmlfile):
	ttodrelem = ET.Element('ttOdr')
	xmlDFS(pttOdr.toroot,ttodrelem)
	indent(ttodrelem)
	# ET.dump(ttodrelem)
	tree = ET.ElementTree(ttodrelem)
	tree.write(pxmlfile)
	temp = open(pxmlfile,'r')
	temp.close()

def xmlDFS(curr,elem):
	ato = curr.ato
	subelem = None
	if ato is None:
		subelem = ET.Element('dumTtOdr')
		elem.append(subelem)
	else:
		if isinstance(ato,FiniteDomainTotalOrder):
			subelem = ET.Element('FaTtOdr')
			subelem.attrib['Name'] = curr.ato.name
			subelem.attrib['Odr'] = str(curr.ato.odr)
			elem.append(subelem)
			domval = ''
			for dom in curr.ato.domain:
				domval = domval + str(dom) + ','
			subelem.attrib['Domain'] = domval[0:len(domval)-1]
		elif isinstance(ato,AtomicTotalOrder):
			subelem = ET.Element('aTtOdr')
			subelem.attrib['Name'] = curr.ato.name
			subelem.attrib['Odr'] = str(curr.ato.odr)
			elem.append(subelem)
		for i in range(len(curr.children)):
			xmlDFS(curr.children[i],subelem)

def indent(elem, level=0):
    i = '\n' + level*'  '
    if len(elem):
        if not elem.text or not elem.text.strip():
            elem.text = i + "  "
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
        for elem in elem:
            indent(elem, level+1)
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            elem.tail = i

def solToString(sols):
	retstr = '['
	for key in sols:
		retstr = retstr + key + ':' + str(sols[key]) + ', '
	retstr = retstr[0:len(retstr)-2]
	retstr = retstr + ']\n'
	return retstr

def binarySearchIdx(sortedlist,tup,pcomp):
	listlen = len(sortedlist)
	jumplen = listlen//2
	retidx = jumplen
	left = 0
	right = listlen
	while(jumplen>0):
		retidx = left + jumplen
		key = pcomp.compare(sortedlist[retidx],tup)
		if key == -1: # tup is worse
			left = retidx + 1
		elif key == 1 or key == 0: # tup is better or equal
			right = retidx
		listlen = right - left
		jumplen = listlen//2

	if (right - left) == 0:
		pass
	elif (right - left) == 1:
		key2 = pcomp.compare(sortedlist[retidx],tup)
		if key2 == -1:
			retidx = retidx + 1
		elif key2 == 1 or key2 == 0:
			pass
	return retidx

class MetricsHandler:
	def __init__(self, outlog):
		self.fitvars = ('NoAffS','VL','VC','Time','Result','AFC','SFC','NoR','NoIter','NoStop','AvgLenTP','DNonTItp','NoAbs','NoCSucc','AvgLenTPblk','DNonTItpblk','ENonTItp','ENonTItpblk','FCov','LCov','CCov','SizRS','TPredAbs','NoPredBAbs','TimPrec','NoBDDN','SizBDDQ','SizBDDQAvg','MaxWait','AvgWait','NoRL','TimPreAdj','TimTran')
		self.out = outlog

	def other_after_run(self):

		print('filename: ', self.out)

		f = open(self.out,"r")
		lines = f.readlines()
		f.close()

		dic = {}

		# loop for extracting other metrics

		for line in lines:
			if line.find("Number of affected states:") is not -1:
				tokens = line.split()
				dic[self.fitvars[0]] = int(tokens[4])

			if line.find("Visited lines:") is not -1:
				tokens = line.split()
				dic[self.fitvars[1]] = int(tokens[len(tokens)-1])
	    
			if line.find("Visited conditions:") is not -1:
				tokens = line.split()
				dic[self.fitvars[2]] = int(tokens[len(tokens)-1])

			if line.find("CPU time for analysis:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[3]] = float(token[0:len(token)-1])

			if line.find("Verification result:") is not -1:
				tokens = line.split()
				token = tokens[2]
				result = token[0:len(token)-1]
				print(result)
				if result == 'TRUE' or result == 'FALSE':
					dic[self.fitvars[4]] = 1
				else:
					dic[self.fitvars[4]] = 0

			if line.find("Attempted forced coverings:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[5]] = int(token)

			if line.find("Successful forced coverings:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-2]
				dic[self.fitvars[6]] = int(token)

			if line.find("Number of refinements:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[7]] = int(token)

			if line.find("Number of iterations:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[8]] = int(token)

			if line.find("Number of times stopped:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[9]] = int(token)

			if line.find("Avg. length of target path (in blocks):") is not -1:
				tokens = line.split()
				token = tokens[7]
				token2 = tokens[15]
				token2 = token2[0:len(token2)-1]
				dic[self.fitvars[10]] = float(token)
				dic[self.fitvars[14]] = float(token2)

			if line.find("Different non-trivial interpolants along paths:") is not -1:
				tokens = line.split()
				token = tokens[5]
				token2 = tokens[len(tokens)-1]
				token2 = token2[0:len(token2)-1]
				dic[self.fitvars[11]] = int(token)
				dic[self.fitvars[15]] = float(token2)

			if line.find("Number of abstractions:") is not -1:
				tokens = line.split()
				token = tokens[3]
				dic[self.fitvars[12]] = int(token)

			if line.find("Number of computed successors:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[13]] = int(token)

			if line.find("Equal non-trivial interpolants along paths:") is not -1:
				tokens = line.split()
				token = tokens[5]
				token2 = tokens[len(tokens)-1]
				token2 = token2[0:len(token2)-1]
				dic[self.fitvars[16]] = int(token)
				dic[self.fitvars[17]] = float(token2)

			if line.find("Function coverage:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[18]] = float(token)

			if line.find("Line coverage:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[19]] = float(token)

			if line.find("Condition coverage:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[20]] = float(token)

			if line.find("Size of reached set:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[21]] = int(token)

			if line.find("Total predicates per abstraction:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[22]] = int(token)

			if line.find("Number of preds handled by boolean abs:") is not -1:
				tokens = line.split()
				token = tokens[7]
				dic[self.fitvars[23]] = int(token)

			if line.find("Time for prec operator:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				token = token[0:len(token)-1]
				dic[self.fitvars[24]] = float(token)

			if line.find("Number of BDD nodes:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[25]] = int(token)

			if line.find("Size of BDD node cleanup queue:") is not -1:
				tokens = line.split()
				token = tokens[6]
				dic[self.fitvars[26]] = int(token)
				token2 = tokens[len(tokens)-1]
				token2 = token2[0:len(token2)-1]
				dic[self.fitvars[27]] = float(token2)

			if line.find("Max size of waitlist:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[28]] = int(token)

			if line.find("Average size of waitlist:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				dic[self.fitvars[29]] = int(token)

			if line.find("Number of reached locations:") is not -1:
				tokens = line.split()
				token = tokens[4]
				dic[self.fitvars[30]] = int(token)

			if line.find("Time for precision adjustment:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				token = token[0:len(token)-1]
				dic[self.fitvars[31]] = float(token)

			if line.find("Time for transfer relation:") is not -1:
				tokens = line.split()
				token = tokens[len(tokens)-1]
				token = token[0:len(token)-1]
				dic[self.fitvars[32]] = float(token)

		return dic

class TSSearch:
	def __init__(self, labfuncs):
		self.atos = makingAtomTotalOrders(labfuncs)
		self.defaultargv = ['./scripts/RanTSExecutor.py', '--no-container', '--', 'scripts/cpa.sh', '-Dy-MySearchStrategy', '-heap', '-timelimit', '-preprocess', '-stats', '-noout', '-setprop', 'cpa.predicate.memoryAllocationsAlwaysSucceed=true', '-spec', '../sv-benchmarks/c/ReachSafety.prp']
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

	def Execute(self, handler):
		print(self.myargv)
		buildExecutable()
		benchexec.runexecutor.main(self.myargv)
		newvals = handler.other_after_run()
		return newvals

def main():

	print('Traversal Strategy Search Start')

	currts = None
	bestts = None
	tempts = None
	bestvals = None
	outlog = 'output.log'
	labfuncs = (('isAbs',1,(0,1),0),('CS',0,1),('RPO',0,1),('CS',0,0),('blkD',0,0),('blkD',0,1),('RPO',0,0),('uID',0,0),('uID',0,1),('LenP',0,1),('LenP',0,0),('loopD',0,1),('loopD',0,0))
	atos = None
	valuefile = 'fitvalues.txt'
	bestvalfile = 'bestfitvalues.txt'
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/MySearchStrategyFormula.java'
	currxmlfile = 'currts.xml'
	bestxmlfile = 'bestts.xml'

	mhdlr = MetricsHandler(outlog)

	population = []
	popsize = 20
	elitismsize = 2
	evallimit = 10

	mycore = 0
	mytime = 900
	mymem = 7000000000
	myfile = sys.argv[1]
	myalgo = sys.argv[2]

	assert popsize > elitismsize

	gaClass = GA.GARouletteWheelTree(popsize)

	atos = makingAtomTotalOrders(labfuncs)
	valout = open(valuefile,'w')
	valout.close()
	bestvalout = open(bestvalfile,'w')
	bestvalout.close()

	#print(atos)

	nofgenerations = 0
	currvals = None
	initpops = True

	while(nofgenerations < evallimit):
		
		print('\nTraversal Strategy Search Iter ')
		# Make a new solution
		# population-based search
		# make a new search strategy formula (total order)

		#selection, crossover, and mutation are needed
		poptemp = []

		#elitism
		if initpops:
			pass
		else:
			for i in range(elitismsize):
				poptemp.append(population[i])
		
		while(len(poptemp) < popsize):
			if initpops:
				indiv = TraversalStrategy(atos)
				indiv.randomOdrGen()
				indivvals = None
				poptemp.append((indiv, indivvals))
			else:
				#selection
				sol1, sol2 = gaClass.selection(population)
				#crossover
				newsol = gaClass.crossover(sol1.deepcopyTS(), sol2.deepcopyTS())
				#mutation
				newsol = gaClass.mutation(newsol)
				solvals = None
				poptemp.append((newsol, solvals))

		initpops = False

		population = poptemp

		for i in range(len(population)):
			if population[i][1] == None:
				ttOdrToXML(population[i][0],currxmlfile)
				XtJ.xmltoJava(currxmlfile,searchstrategyjavafile)

				executor = TSSearch(labfuncs)

				executor.makeArgv(mycore, mymem, mytime, myalgo, myfile)

				# Calculate the fitness of the new solution
				# execution of cpachecker with new total order
				newvals = executor.Execute(mhdlr)

				population[i] = (population[i][0],newvals)

				#assert len(newvals) == len(hdlr.fitvars), 'length of handled output missmatched'

		#TODO if preprocessing or generalization is needed, do it in this section
		comparator = Comp.GeneralTSComparator()
		genfit = comparator.preprocessing(population,hdlr.fitvars)
		#preprocessing or generalization end

		sortedgenfitlist = []
		sortedpop = []

		for i in range(len(population)):
			lensortedpop = len(sortedpop)
			positioninlist = binarySearchIdx(sortedgenfitlist,genfit[i],comparator)
			temptuplist = []
			temptuplist.append(genfit[i])
			sortedgenfitlist = sortedgenfitlist[0:positioninlist] + temptuplist + sortedgenfitlist[positioninlist:lensortedpop]
			temptuplist2 = []
			temptuplist2.append(population[i])
			sortedpop = sortedpop[0:positioninlist] + temptuplist2 + sortedpop[positioninlist:lensortedpop]

		assert len(population) == len(sortedpop), 'population and sortedpop are missmatched!'

		population = sortedpop

		# print('ret: ',newvals)
		# newvalsstr = solToString(newvals)
		# valout = open(valuefile,'a')
		# valout.write(newvalsstr)
		# valout.close()

		#save the best solution
		bestts = population[0][0]
		bestvalsstr = solToString(population[0][1])
		bestvalout = open(bestvalfile,'a')
		bestvalout.write(bestvalsstr)
		bestvalout.close()
		ttOdrToXML(bestts,bestxmlfile)

		nofgenerations = nofgenerations + 1

	XtJ.xmltoJava(bestxmlfile,searchstrategyjavafile)
	print('Search complete! best execution is needed!')

	return bestts

if __name__ == '__main__':
	main()
