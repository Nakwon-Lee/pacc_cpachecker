from __future__ import absolute_import, division, print_function, unicode_literals

import glob
import os
import sys
import xml.etree.ElementTree as ET
import TtOdrXMLToJAVA as XtJ
import copy
import GeneticAlgo as GA

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

def other_after_run(outlog,var):

	print('filename: ',outlog)

	f = open(outlog,"r")
	lines = f.readlines()
	f.close()

	dic = {}

	# loop for extracting other metrics

	for line in lines:
		if line.find("Number of affected states:") is not -1:
			tokens = line.split()
			dic[var[0]] = int(tokens[4])

		if line.find("Visited lines:") is not -1:
			tokens = line.split()
			dic[var[1]] = int(tokens[len(tokens)-1])
    
		if line.find("Visited conditions:") is not -1:
			tokens = line.split()
			dic[var[2]] = int(tokens[len(tokens)-1])

		if line.find("Total CPU time for CPAchecker:") is not -1:
			tokens = line.split()
			token = tokens[len(tokens)-1]
			dic[var[3]] = float(token[0:len(token)-1])

		if line.find("Verification result:") is not -1:
			tokens = line.split()
			token = tokens[2]
			result = token[0:len(token)-1]
			print(result)
			if result == 'TRUE' or result == 'FALSE':
				dic[var[4]] = 1
			else:
				dic[var[4]] = 0

	return dic

def comparefitness(old,new):
	
	ret = 0

	if old['Result'] > new['Result']:
		ret = -1
	elif old['Result'] < new['Result']:
		ret = 1
	elif old['Result'] == new['Result'] and old['Result'] == 1:
		#print('Result is ',1)
		if old['Time'] < new['Time']:
			ret = -1
		elif old['Time'] > new['Time']:
			ret = 1
		else:
			pass
	elif old['Result'] == new['Result'] and old['Result'] == 0:
		#print('Result is ',0)
		oldfit = (0.62807*old['NoAffS'])+(-1.27101*old['VL'])+(1.18150*old['VC'])-0.02434
		newfit = (0.62807*new['NoAffS'])+(-1.27101*new['VL'])+(1.18150*new['VC'])-0.02434
		if oldfit < newfit:
			ret = -1
		elif oldfit > newfit:
			ret = 1
		else:
			pass
	return ret

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

def binarySearchIdx(sortedlist,tup):
	listlen = len(sortedlist)
	jumplen = listlen//2
	retidx = jumplen
	left = 0
	right = listlen
	while(jumplen>0):
		retidx = left + jumplen
		key = comparefitness(sortedlist[retidx],tup)
		if key == -1: # tup is worse
			left = retidx + 1
		elif key == 1 or key == 0: # tup is better or equal
			right = retidx
		listlen = right - left
		jumplen = listlen//2

	if (right - left) == 0:
		pass
	elif (right - left) == 1:
		key2 = comparefitness(sortedlist[retidx],tup)
		if key2 == -1:
			retidx = retidx + 1
		elif key2 == 1 or key2 == 0:
			pass
	return retidx

def generalizedFit(pop,fitvars):
	genFit = []
	maxs = {}
	mins = {}
	for fitvar in fitvars:
		tlist = []
		for j in range(len(pop)):
			tlist.append(pop[j][1][fitvar])
		maxs[fitvar] = max(tlist)
		mins[fitvar] = min(tlist)

	for i in range(len(pop)):
		genval = {}
		for fitvar in fitvars:
			diff = maxs[fitvar]-mins[fitvar]
			if diff == 0:
				genval[fitvar] = 0
			else:
				genval[fitvar] = (pop[i][1][fitvar]-mins[fitvar])/(maxs[fitvar]-mins[fitvar])
		genFit.append(genval)
	
	return genFit

def main():

	print('Traversal Strategy Search Start')

	currts = None
	bestts = None
	tempts = None
	bestvals = None
	outlog = 'output.log'
	fitvars = ('NoAffS','VL','VC','Time','Result')
	labfuncs = (('isAbs',1,(0,1),0),('CS',0,1),('RPO',0,1),('CS',0,0),('blkD',0,0),('blkD',0,1),('RPO',0,0),('uID',0,0),('uID',0,1),('LenP',0,1),('LenP',0,0),('loopD',0,1),('loopD',0,0))
	atos = None
	valuefile = 'fitvalues.txt'
	bestvalfile = 'bestfitvalues.txt'
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/MySearchStrategyFormula.java'
	currxmlfile = 'currts.xml'
	bestxmlfile = 'bestts.xml'

	population = []
	popsize = 20
	elitismsize = 2
	evallimit = 10

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

				# Calculate the fitness of the new solution
				# execution of cpachecker with new total order
				buildExecutable()
				benchexec.runexecutor.main()
				newvals = other_after_run(outlog,fitvars)

				population[i] = (population[i][0],newvals)

				assert len(newvals) == len(fitvars)

		#TODO generalize fitness values
		genfit = generalizedFit(population,fitvars)
		sortedgenfitlist = []
		sortedpop = []

		for i in range(len(population)):
			lensortedpop = len(sortedpop)
			positioninlist = binarySearchIdx(sortedgenfitlist,genfit[i])
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
