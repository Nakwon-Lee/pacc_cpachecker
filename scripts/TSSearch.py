from __future__ import absolute_import, division, print_function, unicode_literals

import glob
import os
import sys
import xml.etree.ElementTree as ET
import TtOdrXMLToJAVA as XtJ
import copy

sys.dont_write_bytecode = True # prevent creation of .pyc files
for egg in glob.glob(os.path.join(os.path.dirname(__file__), os.pardir, 'lib', 'python-benchmark', '*.egg')):
    sys.path.insert(0, egg)

import benchexec.runexecutor
from TraversalStrategyModels import *

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
		print('Result is ',1)
		if old['Time'] < new['Time']:
			ret = -1
		elif old['Time'] > new['Time']:
			ret = 1
		else:
			pass
	elif old['Result'] == new['Result'] and old['Result'] == 0:
		print('Result is ',0)
		if old['VC'] > new['VC']:
			ret = -1
		elif old['VC'] < new['VC']:
			ret = 1
		else:
			if old['VL'] > new['VL']:
				ret = -1
			elif old['VL'] < new['VL']:
				ret = 1
			else:
				if old['NoAffS'] < new['NoAffS']:
					ret = -1
				elif old['NoAffS'] > new['NoAffS']:
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
	ET.dump(ttodrelem)
	tree = ET.ElementTree(ttodrelem)
	tree.write(pxmlfile)

def xmlDFS(curr,elem):
	ato = curr.ato
	subelem = ET.Element(curr.ato.name)
	subelem.attrib['Odr'] = str(curr.ato.odr)
	elem.append(subelem)
	if isinstance(ato,FiniteDomainTotalOrder):
		domval = ''
		for dom in curr.ato.domain:
			domval = domval + str(dom) + ','
		subelem.attrib['Domain'] = domval[0:len(domval)-1]
		for i in range(len(curr.children)):
			xmlDFS(curr.children[i],subelem)
	elif isinstance(ato,AtomicTotalOrder):
		if len(curr.children) is not 0:
			xmlDFS(curr.children[0],subelem)

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

def main():

	print('Traversal Strategy Search Start')

	currts = None
	bestts = None
	bestvals = None
	outlog = 'output.log'
	fitvars = ('NoAffS','VL','VC','Time','Result')
	labfuncs = (('isAbs',1,(0,1),0),('isAbs',1,(0,1),1),('blkD',0,0),('blkD',0,1),('CS',0,0),('CS',0,1),('tD',0,0),('tD',0,1),('RPO',0,0),('RPO',0,1),('uID',0,0),('uID',0,1))
	atos = None
	valuefile = 'fitvalues.txt'
	bestvalfile = 'bestfitvalues.txt'
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/MySearchStrategyFormula.java'
	currxmlfile = 'currts.xml'
	bestxmlfile = 'bestts.xml'

	atos = makingAtomTotalOrders(labfuncs)
	valout = open(valuefile,'w')
	valout.close()
	bestvalout = open(bestvalfile,'w')
	bestvalout.close()

	#print(atos)

	currvals = None

	for i in range(20):
		
		print('\nTraversal Strategy Search Iter ',i)
		#TODO Make a new solution
		# local search
		# make a new search strategy formula (total order)
		currts = TraversalStrategy(atos)
		currts.randomOdrGen()

		currts.printTS()

		ttOdrToXML(currts,currxmlfile)

		XtJ.xmltoJava(currxmlfile,searchstrategyjavafile)

		#TODO Calculate the fitness of the new solution
		# execution of cpachecker with new total order

		benchexec.runexecutor.main()
		newvals = other_after_run(outlog,fitvars)

		if len(newvals) != len(fitvars):
			print('Iteration Failed!')
			continue

		print('ret: ',newvals)
		newvalsstr = solToString(newvals)
		valout = open(valuefile,'a')
		valout.write(newvalsstr)
		valout.close()

		if bestvals is None:
			fitness = 1
		else:
			fitness = comparefitness(bestvals,newvals)

		#TODO If the new solution is better than the best solution, change the solution
		if fitness is 1: #new one is better than best one
			bestts = currts
			bestvals = copy.deepcopy(newvals)
			bestvalsstr = solToString(bestvals)
			bestvalout = open(bestvalfile,'a')
			bestvalout.write(bestvalsstr)
			bestvalout.close()
			ttOdrToXML(bestts,bestxmlfile)
			print('new best!')
		elif fitness is 0: #TODO update bestts with specific probability
			pass

	return bestts

if __name__ == '__main__':
    main()
