from __future__ import absolute_import, division, print_function, unicode_literals

import glob
import os
import sys

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
		if line.find("Number of affected states:") >= 0:
			tokens = line.split()
			dic[var[0]] = tokens[4]

		if line.find("Visited lines:") >= 0:
			tokens = line.split()
			dic[var[1]] = tokens[len(tokens)-1]
    
		if line.find("Visited conditions:") >= 0:
			tokens = line.split()
			dic[var[2]] = tokens[len(tokens)-1]

	return dic

def comparefitness(old,new):
	
	ret = 0

	#compare fitness of two vals.
	#if vals1 is better, return -1, if vals2 is better, return 1
	#if vals1 and vals2 have same fitness, return 0

	return ret

def makingAtomTotalOrders(labfuncs):

	atos = []

	for labfunc in labfuncs:
		if labfunc[1] == 1: #TtOdr with finite domain
			ato = FiniteDomainTotalOrder(labfunc[0],labfunc[2])
			atos.append(ato)
		elif labfunc[1] == 0: #TtOdr with infinite domain
			ato = AtomicTotalOrder(labfunc[0])
			atos.append(ato)
		else:
			print('labfunc should be finite or infinite domain')

	return atos

def main():

	print('Traversal Strategy Search Start')

	bestts = None
	currvals = None
	outlog = 'output.log'
	fitvars = ('NoAffS','VL','VC')
	labfuncs = (('IsAbs',1,(0,1)),('blkD',0),('CS',0),('RPO',0),('uID',0))
	atos = None

	atos = makingAtomTotalOrders(labfuncs)

	print(atos)

	while(1):
		#TODO Make a new solution
		# local search
		# make a new search strategy formula (total order)

		#TODO Calculate the fitness of the new solution
		# execution of cpachecker with new total order
		benchexec.runexecutor.main()
		newvals = other_after_run(outlog,fitvars)
		print('ret: ',newvals)
		#fitness = comparefitness(currvals,newvals)

		#TODO If the new solution is better than the best solution, change the solution
		#if fitness is 1: #new one is better than old one
		#	pass
		#else if fitness is 0:
			#TODO update bestts with specific probability
		#	pass

		break

	return bestts

if __name__ == '__main__':
    main()
