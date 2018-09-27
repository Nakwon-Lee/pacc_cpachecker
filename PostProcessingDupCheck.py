import sys
import csv

def main():

	destivefilelistheader = ['No.','file name']
	fitvalsheaderex = ('NoAffS','VL','VC','Time','Result','AFC','SFC','NoR','NoIter','NoStop','AvgLenTP','DNonTItp','NoAbs','FNoAffS','FVL','FVC','FTime','FResult','FAFC','FSFC','FNoR','FNoIter','FNoStop','FAvgLenTP','FDNonTItp','FNoAbs')
	destivs = ('Nums', 'Sum', 'Avg', 'Min', 'Max', 'Mid', 'Std', 'Unq')

	for afitval in fitvalsheaderex:
		for adestiv in destivs:
			destivefilelistheader.append(afitval + adestiv)

	destivefilelistheader.append('Size')
	destivefilelistheader.append('Lines')
	destivefilelistheader.append('Conds')

	myfile = sys.argv[1]
	filef = open(myfile,'r')

	csvf = csv.DictReader(filef,fieldnames=destivefilelistheader)

	nameset = set()

	next(csvf)
	for arow in  csvf:
		if arow['file name'] not in nameset:
			nameset.add(arow['file name'])
		else:
			print(arow['file name'])

	filef.close()

if __name__ == '__main__':
	main()
