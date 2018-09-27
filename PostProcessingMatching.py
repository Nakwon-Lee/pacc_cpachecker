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

	nfilePAL = sys.argv[1]
	nfilePALF = sys.argv[2]

	filePAL = open(nfilePAL,'r')
	filePALF = open(nfilePALF,'r')

	csvPAL = csv.DictReader(filePAL,fieldnames=destivefilelistheader)
	csvPALF = csv.DictReader(filePALF,fieldnames=destivefilelistheader)

	listPAL = []
	csvPAL.__next__()
	for arow in  csvPAL:
		listPAL.append(arow)

	listPALF = []
	csvPALF.__next__()
	for arow in  csvPALF:
		listPALF.append(arow)

	filePAL.close()
	filePALF.close()

	resultlistPAL = []
	resultlistPALF = []

	for dicl in listPAL:
		for diclf in listPALF:
			if dicl['file name'] == diclf['file name']:
				resultlistPAL.append(dicl)
				resultlistPALF.append(diclf)

	resultfileL = sys.argv[3]
	resultfileLF = sys.argv[4]

	resfilePAL = open(resultfileL,'w')
	resfilePALF = open(resultfileLF,'w')

	rescsvPAL = csv.DictWriter(resfilePAL,fieldnames=destivefilelistheader)
	rescsvPALF = csv.DictWriter(resfilePALF,fieldnames=destivefilelistheader)

	rescsvPAL.writeheader()
	rescsvPALF.writeheader()

	for dicl in resultlistPAL:
		rescsvPAL.writerow(dicl)

	for diclf in resultlistPALF:
		rescsvPALF.writerow(diclf)

	resfilePAL.close()
	resfilePALF.close()

if __name__ == '__main__':
	main()
