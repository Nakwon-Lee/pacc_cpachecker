import os
import sys
import csv
import numpy as np

def copyValtoDic(fdic, tdic, fkey, tkey, vrest, vtime):
	ret = 0
	if fkey == 'Time':
		if fdic['Result'] == '1':
			if vrest == 1:
				tdic[tkey] = float(fdic[fkey])
			elif vrest == 2:
				tdic[tkey] = 'NaN'
			else:
				assert False, 'vrest should be 1 or 2'
		elif fdic['Result'] == '0':
			if float(vtime) > 900:
				tdic[tkey] = 900
			else:
				tdic[tkey] = 'NaN'
		elif fdic['Result'] == '':
			tdic[tkey] = 900
		else:
			assert False, 'Result should be 1, 0, or None'
	elif fkey == 'Result':
		if vrest == None:
			tdic[tkey] = 0
		elif vrest == 1:
			tdic[tkey] = float(fdic[fkey])
		elif vrest == 2:
			tdic[tkey] = 'NaN'
		else:
			tdic[tkey] = float(fdic[fkey]) 
	else:
		if fdic[fkey] == '':
			tdic[tkey] = 'NaN'
		else:
			tdic[tkey] = float(fdic[fkey])

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
		elif ahead == 'Min':
			if len(dic[fitval]) != 0:
				#target[fitval+ahead] = min(dic[fitval])
				target[fitval+ahead] = np.percentile(dic[fitval],25)
			else:
				target[fitval+ahead] = 'NaN'
		elif ahead == 'Max':
			if len(dic[fitval]) != 0:
				#target[fitval+ahead] = max(dic[fitval])
				target[fitval+ahead] = np.percentile(dic[fitval],75)
			else:
				target[fitval+ahead] = 'NaN'
		elif ahead == 'Mid':
			if len(dic[fitval]) != 0:
				target[fitval+ahead] = np.median(dic[fitval])
			else:
				target[fitval+ahead] = 'NaN'
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

def resultCatcher(pfile):
	lines = pfile.readlines()
	result = None
	fulltime = None
	for aline in lines:
		if aline.find("Verification result:") is not -1:
			tokens = aline.split()
			token = tokens[2]
			result = token[0:len(token)-1]
			if result == 'TRUE':
				result = 1
			elif result == 'FALSE':
				result = 2
			else:
				result = 0
		if aline.find("Total CPU time for CPAchecker:") is not -1:
			tokens = aline.split()
			token = tokens[5]
			fulltime = token[0:len(token)-1]

	return result, fulltime

def main():
	dirname = sys.argv[1]
	fileprefix = 'fitvalues'
	fullfileprefix = 'fitvaluesFull'
	filesuffix = '.csv'
	targetfilelistcsvheader = ('No.','file name')
	targetfilelistcsvheaderex = ('No.','file name')
	destivefilelistheader = ['No.','file name']
	fitvalsheader = ('NoAffS','VL','VC','Time','Result','AFC','SFC','NoR','NoIter','NoStop','AvgLenTP','DNonTItp','NoAbs')
	fitvalsheaderex = ('NoAffS','VL','VC','Time','Result','AFC','SFC','NoR','NoIter','NoStop','AvgLenTP','DNonTItp','NoAbs','FNoAffS','FVL','FVC','FTime','FResult','FAFC','FSFC','FNoR','FNoIter','FNoStop','FAvgLenTP','FDNonTItp','FNoAbs')
	destivs = ('Nums', 'Sum', 'Avg', 'Min', 'Max', 'Mid', 'Std', 'Unq')

	count = 0

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
			isexist = os.path.exists(staticfilename)
			if isexist:
				staticf = open(staticfilename,'r')
				nlines, nconds = staticCatcher(staticf)
				staticf.close()
			if nlines != None and nconds != None:
				break

		if nlines == None or nconds == None:
			pass
			#print(row['file name'])
		destivdic['Lines'] = nlines
		destivdic['Conds'] = nconds

		destivdic['Size'] = os.path.getsize(row['file name'])

		valdiclist = []

		fitcsvfile = None
		fitcsvreader = None
		fcsvexi = os.path.exists(fitcsvfilename)
		if fcsvexi:
			fitcsvfile = open(fitcsvfilename)
			fitcsvreader = csv.DictReader(fitcsvfile,fieldnames=fitvalsheader)
		fullcsvfile = None
		fullcsvreader = None
		fulcsvexi = os.path.exists(fullfilename)
		if fulcsvexi:
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
		if fitcsvreader != None: 
			fitcsvreader.__next__()
			for fitlow in fitcsvreader:
				vrest = None
				vtime = None
				outffilename = dirpath + 'tsxml' + str(number) + '/output'+ str(i) + '.log'
				#outffilename = dirpath + 'ouputs' + str(number) + '/output.log'
				isoutfexist = os.path.exists(outffilename)
				if isoutfexist:
					outff = open(outffilename,'r')
					vrest, vtime = resultCatcher(outff)
					outff.close()
				for afitval in fitvalsheader:
					copyValtoDic(fitlow, valdiclist[i], afitval, afitval, vrest, vtime)
					if valdiclist[i][afitval] != 'NaN':
						destivediclist[afitval].append(valdiclist[i][afitval])
				i = i + 1

		i = 0
		if fullcsvreader != None:
			fullcsvreader.__next__()
			for fulllow in fullcsvreader:
				vrest = None
				vtime = None
				outffilename = dirpath + 'tsxml' + str(number) + '/outputF'+ str(i) + '.log'
				#outffilename = dirpath + 'ouputs' + str(number) + '/output.log'
				isoutfexist = os.path.exists(outffilename)
				if isoutfexist:
					outff = open(outffilename,'r')
					vrest, vtime = resultCatcher(outff)
					outff.close()
				for afitval in fitvalsheader:
					copyValtoDic(fulllow, valdiclist[i], afitval, 'F'+afitval, vrest, vtime)
					if valdiclist[i]['F'+afitval] != 'NaN':
						destivediclist['F'+afitval].append(valdiclist[i]['F'+afitval])
				i = i + 1

		for afit in fitvalsheaderex:
			destiveCalc(destivediclist, destivdic, destivs, afit)

		destivwriter.writerow(destivdic)

		if destivdic['FResultSum'] > 0 and destivdic['FTimeAvg'] > 10:
			quat1 = destivdic['FTimeMin']
			quat3 = destivdic['FTimeMax']
			qcd = (quat3-quat1)/(quat3+quat1)
			if qcd > 0.1:
				tempdic = {}
				tempdic['No.'] = idx
				tempdic['file name'] = row['file name']
				summarywriter.writerow(tempdic)

				sumfilename = sumfilenamepre + str(idx) + filesuffix
				sumfile = open(sumfilename,'w')
				sumwriter = csv.DictWriter(sumfile,fieldnames=fitvalsheaderex)
				sumwriter.writeheader()

				for elem in valdiclist:
					sumwriter.writerow(elem)

				sumfile.close()

				idx = idx + 1

		if fitcsvreader != None:
			fitcsvfile.close()
		if fullcsvfile != None:
			fullcsvfile.close()
		destiveidx = destiveidx + 1

	destivfile.close()
	summaryfile.close()
	targetfilelistcsvfile.close()
		
if __name__ == '__main__':
	main()
