import os
import sys
import csv
import copy
import random

def main():
	dirpath = sys.argv[1]
	summaryname = sys.argv[2]
	attempts = int(sys.argv[3])
	trainingpre = 'training'
	testingpre = 'testing'
	fitvalsheaderex = ('NoAffS','VL','VC','Time','Result','FTime','FResult')
	fitvalsheadergen = ('GNoAffS','GVL','GVC','GTime','GResult','GFTime','GFResult')
	fittotal = fitvalsheaderex + fitvalsheadergen
	targetvals = ('GNoAffS','GVL','GFTime')
	suffix = '.csv'
	numoffold = 10

	summaryfilerd = open(summaryname,'r')
	summaryreader = csv.DictReader(summaryfilerd,fieldnames=fittotal)

	diclist = []
	summaryreader.__next__()
	for tt in summaryreader:
		diclist.append(copy.deepcopy(tt))

	summaryfilerd.close()

	length = len(diclist)
	assert (length % attempts) == 0, 'attempts should divide length with out remainder'
	filenums = length // attempts

	foldlist = []
	for i in range(numoffold):
		templist = []
		foldlist.append(templist)

	filenumset = []
	for i in range(filenums):
		filenumset.append(i+1)

	random.shuffle(filenumset)
	i = 0
	for afilenum in filenumset:
		afold = foldlist[i%numoffold]
		afold.append(afilenum)
		i = i + 1

	i = 0
	for afold in foldlist:
		trainingfile = dirpath + trainingpre + str(i) + suffix
		testingfile = dirpath + testingpre + str(i) + suffix

		traininglist = []
		testinglist = []

		stendlist = [] # tuple (st,end]

		for anum in afold:
			st = (anum-1) * attempts
			end = anum * attempts
			temptup = (st,end)
			stendlist.append(temptup)

		j = 0
		for adict in diclist:
			keyforrange = False
			for atup in stendlist:
				if j >= atup[0] and j < atup[1]:
					keyforrange = True
					break
			if keyforrange:
				testinglist.append(adict)
			else:
				traininglist.append(adict)
			j = j + 1

		trainingf = open(trainingfile,'w')
		trainingwriter = csv.DictWriter(trainingf,fieldnames=targetvals)
		trainingwriter.writeheader()

		for atrain in traininglist:
			tempdict = {}
			for atarg in targetvals:
				tempdict[atarg] = atrain[atarg]
			trainingwriter.writerow(tempdict)

		trainingf.close()

		testingf = open(testingfile,'w')
		testingwriter = csv.DictWriter(testingf,fieldnames=targetvals)
		testingwriter.writeheader()

		for atest in testinglist:
			tempdict = {}
			for atarg in targetvals:
				tempdict[atarg] = atest[atarg]
			testingwriter.writerow(tempdict)

		testingf.close()

		i = i + 1
		
if __name__ == '__main__':
	main()
