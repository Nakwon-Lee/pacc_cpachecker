import os
import sys
import csv

def main():
	dirpath = sys.argv[1]
	summaryname = sys.argv[2]
	sumfilepre = 'fitvaluesFull'
	resultname = 'rsummary'
	targetfilelistcsvheaderex = ('No.','file name')
	fitvalsheader = ('NoAffS','VL','VC','Time','Result','AFC','SFC','NoR','NoIter','NoStop','AvgLenTP','DNonTItp','NoAbs','NoCSucc','AvgLenTPblk','DNonTItpblk','ENonTItp','ENonTItpblk','FCov','LCov','CCov','SizRS','TPredAbs','NoPredBAbs','TimPrec','NoBDDN','SizBDDQ','SizBDDQAvg','MaxWait','AvgWait','NoRL','TimPreAdj','TimTran')
	fittotal = ('No.','file name','Time','Result')
	summaryfilename = dirpath + summaryname + '.csv'
	resultdicfilename = dirpath + resultname + '.csv'

	summaryfilerd = open(summaryfilename,'r')
	summaryreader = csv.DictReader(summaryfilerd,fieldnames=targetfilelistcsvheaderex)

	resultdiclist = []
	summaryreader.__next__()
	for tt in summaryreader:
		resultdiclist.append(tt)

	rsummaryfile = open(resultdicfilename,'w')
	rsummarywriter = csv.DictWriter(rsummaryfile,fieldnames=fittotal)
	rsummarywriter.writeheader()

	for tt in resultdiclist:
		sumfilename = dirpath + sumfilepre + str(tt['No.']) + '.csv'
		sumfile = open(sumfilename,'r')
		sumfilereader = csv.DictReader(sumfile,fieldnames=fitvalsheader)

		sumfilereader.__next__()
		for rrow in sumfilereader:
			tt['Time'] = rrow['Time']
			tt['Result'] = rrow['Result']
			break
		rsummarywriter.writerow(tt)

		sumfile.close()

	summaryfilerd.close()
	rsummaryfile.close()
		
if __name__ == '__main__':
	main()
