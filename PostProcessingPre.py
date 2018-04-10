import os
import sys
import csv

def main():
	filesuffix = '.csv'
	targetfilelistcsvheaderex = ('No.','file name','valid')
	destivefilelistheader = ['No.','file name']
	fitvalsheaderex = ('NoAffS','VL','VC','Time','Result','FNoAffS','FVL','FVC','FTime','FResult')
	destivs = ('Nums', 'Sum', 'Avg', 'Std', 'Unq')

	for afitval in fitvalsheaderex:
		for adestiv in destivs:
			destivefilelistheader.append(afitval + adestiv)

	summaryfileprefix = 'ssummary'
	descriptiveprefix = 'dessummary'

	sumpath = sys.argv[1]
	summaryfilename = sumpath + summaryfileprefix + filesuffix
	descriptivefilename = sumpath + descriptiveprefix + filesuffix

	summaryfile = open(summaryfilename,'w')
	summarywriter = csv.DictWriter(summaryfile,fieldnames=targetfilelistcsvheaderex)
	summarywriter.writeheader()
	summaryfile.close()

	destivfile = open(descriptivefilename,'w')
	destivwriter = csv.DictWriter(destivfile,fieldnames=destivefilelistheader)
	destivwriter.writeheader()
	destivfile.close()

if __name__ == '__main__':
	main()
