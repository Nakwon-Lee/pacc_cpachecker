import os
import sys
import csv
import numpy as np

def copyValtoDic(fdic, tdic, fkey, tkey):
	ret = 0
	if fdic[fkey] == '':
		tdic[tkey] = 'NaN'
		ret = 0
	else:
		tdic[tkey] = float(fdic[fkey])
		ret = 1

	return ret

def checkUniqueness(alist):
	uset = set()
	for elem in alist:
		uset.add(elem)

	return len(uset)

def destiveCalc(dic, target, headers, fitval):
	for ahead in headers:
		if ahead == 'Nums':
			target[fitval+ahead] = len(dic[fitval])
		elif ahead == 'Avg':
			if len(dic[fitval]) != 0:
				target[fitval+ahead] = np.average(dic[fitval])
			else:
				target[fitval+ahead] = 'NaN'
		elif ahead == 'Sum':
			if len(dic[fitval]) != 0:
				target[fitval+ahead] = np.sum(dic[fitval])
			else:
				target[fitval+ahead] = 0
		elif ahead == 'Std':
			if len(dic[fitval]) != 0:
				target[fitval+ahead] = np.std(dic[fitval])
			else:
				target[fitval+ahead] = 'NaN'
		elif ahead == 'Unq':
			if len(dic[fitval]) != 0:
				target[fitval+ahead] = checkUniqueness(dic[fitval])
			else:
				target[fitval+ahead] = 0
		else:
			assert True, 'head have to be existed!'

def staticCatcher(pfile):
	lines = pfile.readlines()
	nlines = None
	nconds = None
	for aline in lines:
		if aline.find('Total lines:') is not -1:
			tokens = aline.split()
			nlines = int(tokens[len(tokens)-1])
		if aline.find('Total conditions:') is not -1:
			tokens = aline.split()
			nconds = int(tokens[len(tokens)-1])

	return nlines, nconds

def main():
	dirname = sys.argv[1]
	fileprefix = 'fitvalues'
	fullfileprefix = 'fitvaluesFull'
	filesuffix = '.csv'
	targetfilelistcsvheader = ('No.','file name')
	targetfilelistcsvheaderex = ('No.','file name','valid')
	destivefilelistheader = ['No.','file name']
	fitvalsheader = ('NoAffS','VL','VC','Time','Result')
	fitvalsheaderex = ('NoAffS','VL','VC','Time','Result','FNoAffS','FVL','FVC','FTime','FResult')
	destivs = ('Nums', 'Sum', 'Avg', 'Std', 'Unq')

	for afitval in fitvalsheaderex:
		for adestiv in destivs:
			destivefilelistheader.append(afitval + adestiv)

	destivefilelistheader.append('Size')
	destivefilelistheader.append('Lines')
	destivefilelistheader.append('Conds')

	utestlist = ('NoAffS','VL','VC')
	unqthreshold = 5

	summaryfileprefix = 'ssummary'
	descriptiveprefix = 'dessummary'
	sumfileprefix = 'sum'

	dirpath = dirname + '/'
	sumpath = sys.argv[2]
	targetfilelistcsvfilename = dirname + '/' + dirname + '.csv'
	summaryfilename = sumpath + summaryfileprefix + filesuffix
	descriptivefilename = sumpath + descriptiveprefix + filesuffix
	sumfilenamepre = sumpath + sumfileprefix

	targetfilelistcsvfile = open(targetfilelistcsvfilename)
	csvreader = csv.DictReader(targetfilelistcsvfile,fieldnames=targetfilelistcsvheader)

	summaryfilerd = open(summaryfilename,'r')
	summaryreader = csv.DictReader(summaryfilerd,fieldnames=targetfilelistcsvheaderex)
	idx = 0
	for tt in summaryreader:
		idx = idx + 1
	idx = idx - 1
	summaryfilerd.close()

	destivfilerd = open(descriptivefilename,'r')
	destivreader = csv.DictReader(destivfilerd,fieldnames=destivefilelistheader)
	destiveidx = 0
	for tt in destivreader:
		destiveidx = destiveidx + 1
	destiveidx = destiveidx - 1
	destivfilerd.close()

	summaryfile = open(summaryfilename,'a')
	summarywriter = csv.DictWriter(summaryfile,fieldnames=targetfilelistcsvheaderex)
	if summaryfile.tell() == 0:
		summarywriter.writeheader()

	destivfile = open(descriptivefilename,'a')
	destivwriter = csv.DictWriter(destivfile,fieldnames=destivefilelistheader)
	if destivfile.tell() == 0:
		destivwriter.writeheader()

	csvreader.__next__()
	for row in csvreader:

		destivdic = {}
		destivdic['No.'] = destiveidx
		destivdic['file name'] = row['file name']

		number = int(row['No.'])
		fitcsvfilename = dirpath + fileprefix + str(number) + filesuffix
		fullfilename = dirpath + fullfileprefix + str(number) + filesuffix

		nlines = None
		nconds = None
		for i in range(30):
			staticfilename = dirpath + 'tsxml' + str(number) + '/output'+ str(i) + '.log'
			staticf = open(staticfilename,'r')
			nlines, nconds = staticCatcher(staticf)
			staticf.close()
			if nlines != None and nconds != None:
				break

		if nlines == None or nconds == None:
			print(row['file name'])
		destivdic['Lines'] = nlines
		destivdic['Conds'] = nconds

		destivdic['Size'] = os.path.getsize(row['file name'])

		valdiclist = []

		fitcsvfile = open(fitcsvfilename)
		fitcsvreader = csv.DictReader(fitcsvfile,fieldnames=fitvalsheader)
		fullcsvfile = open(fullfilename)
		fullcsvreader = csv.DictReader(fullcsvfile,fieldnames=fitvalsheader)

		resultsum = 0
		fullresultsum = 0
		fulltimesum = []

		destivediclist = {}
		for afitval in fitvalsheaderex:
			destivediclist[afitval] = []
		for i in range(30):
			newdic = {}
			for afitval in fitvalsheaderex:
				newdic[afitval] = 'NaN'
			valdiclist.append(newdic)

		i = 0
		fitcsvreader.__next__()
		for fitlow in fitcsvreader:
			for afitval in fitvalsheader:
				copyValtoDic(fitlow, valdiclist[i], afitval, afitval)
				if valdiclist[i][afitval] != 'NaN':
					destivediclist[afitval].append(valdiclist[i][afitval])
			i = i + 1

		i = 0
		fullcsvreader.__next__()
		for fulllow in fullcsvreader:
			for afitval in fitvalsheader:
				copyValtoDic(fulllow, valdiclist[i], afitval, 'F'+afitval)
				if valdiclist[i]['F'+afitval] != 'NaN':
					destivediclist['F'+afitval].append(valdiclist[i]['F'+afitval])
			i = i + 1

		for afit in fitvalsheaderex:
			destiveCalc(destivediclist, destivdic, destivs, afit)

		destivwriter.writerow(destivdic)

		if destivdic['ResultSum'] == 0 and destivdic['FResultSum'] > 24:
			tempdic = {}
			tempdic['No.'] = idx
			tempdic['file name'] = row['file name']

			uvaild = False
			if destivdic['VLUnq'] > unqthreshold and destivdic['VCUnq'] > unqthreshold and destivdic['NoAffSUnq'] > unqthreshold:
				uvaild = True

			if destivdic['FTimeStd'] > 10 and uvaild:
				tempdic['valid'] = 1
			else:
				tempdic['valid'] = 0
			summarywriter.writerow(tempdic)

			sumfilename = sumfilenamepre + str(idx) + filesuffix
			sumfile = open(sumfilename,'w')
			sumwriter = csv.DictWriter(sumfile,fieldnames=fitvalsheaderex)
			sumwriter.writeheader()

			for elem in valdiclist:
				sumwriter.writerow(elem)

			sumfile.close()

			idx = idx + 1

		fitcsvfile.close()
		fullcsvfile.close()
		destiveidx = destiveidx + 1

	destivfile.close()
	summaryfile.close()
	targetfilelistcsvfile.close()
		
if __name__ == '__main__':
	main()
