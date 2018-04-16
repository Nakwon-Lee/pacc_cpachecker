import os
import sys
import csv

def main():
	dirname = sys.argv[1]
	dirpath = dirname + '/'
	summaryname = sys.argv[2]
	sumfilepre = 'sum'
	resultname = 'rsummary'
	targetfilelistcsvheaderex = ('No.','file name','valid')
	fitvalsheaderex = ('NoAffS','VL','VC','Time','Result','FTime','FResult')
	fitvalsheadergen = ('GNoAffS','GVL','GVC','GTime','GResult','GFTime','GFResult')
	fittotal = fitvalsheaderex + fitvalsheadergen
	summaryfilename = dirpath + summaryname + '.csv'
	resultdicfilename = dirpath + resultname + '.csv'

	summaryfilerd = open(summaryfilename,'r')
	summaryreader = csv.DictReader(summaryfilerd,fieldnames=targetfilelistcsvheaderex)

	resultdiclist = []
	summaryreader.__next__()
	for tt in summaryreader:
		key = tt['valid']
		if int(key) == 1:
			resultdiclist.append(tt)

	rsummaryfile = open(resultdicfilename,'w')
	rsummarywriter = csv.DictWriter(rsummaryfile,fieldnames=fittotal)
	rsummarywriter.writeheader()

	for tt in resultdiclist:
		sumfilename = dirpath + sumfilepre + str(tt['No.']) + '.csv'
		sumfile = open(sumfilename,'r')
		sumfilereader = csv.DictReader(sumfile,fieldnames=fitvalsheaderex)

		gsumfilename = dirpath + 'g' + sumfilepre + str(tt['No.']) + '.csv'
		gsumfile = open(gsumfilename,'r')
		gsumfilereader = csv.DictReader(gsumfile,fieldnames=fitvalsheadergen)

		sumfilereader.__next__()
		gsumfilereader.__next__()
		for rrow in sumfilereader:
			tempdic = rrow
			gendic = gsumfilereader.__next__()
			for key in fitvalsheadergen:
				tempdic[key] = gendic[key]
			rsummarywriter.writerow(tempdic)

		sumfile.close()
		gsumfile.close()

	summaryfilerd.close()
	rsummaryfile.close()
		
if __name__ == '__main__':
	main()
