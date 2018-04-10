import os
import sys
import csv

def main():
	dirname = sys.argv[1]
	dirpath = dirname + '/'
	num = sys.argv[2]
	sumfilepre = 'sum'
	fitvalsheaderex = ('NoAffS','VL','VC','Time','Result','FTime','FResult')
	fitvalsheadergen = ('GNoAffS','GVL','GVC','GTime','GResult','GFTime','GFResult')
	sumfilename = dirpath + sumfilepre + str(num) + '.csv'
	genfilename = dirpath + 'g' + sumfilepre + str(num) + '.csv'

	sumfilerd = open(sumfilename,'r')
	sumreader = csv.DictReader(sumfilerd,fitvalsheaderex)

	setofvals = {}

	resultdiclist = []
	sumreader.__next__()
	for rrow in sumreader:
		resultdiclist.append(rrow)

	for key in fitvalsheaderex:
		templist = []
		for tt in resultdiclist:
			if tt[key] == '':
				templist.append(0)
			else:	
				templist.append(float(tt[key]))
		setofvals[key] = templist

	gendiclist = []
	for tt in resultdiclist:
		tempdic = {}
		for key in fitvalsheaderex:
			if key == 'Result' or key == 'FResult':
				tempdic['G' + key] = float(tt[key])
			else:
				keymax = max(setofvals[key])
				keymin = min(setofvals[key])
				if tt[key] == '':
					keyval = 0
				else:	
					keyval = float(tt[key])
				divbot = keymax-keymin
				if divbot == 0:
					genval = 0
				else:
					genval = (keyval - keymin)/(keymax-keymin)
				tempdic['G' + key] = genval
		gendiclist.append(tempdic)

	genfile = open(genfilename,'w')
	genwriter = csv.DictWriter(genfile,fieldnames=fitvalsheadergen)
	genwriter.writeheader()

	for tt in gendiclist:
		genwriter.writerow(tt)

	genfile.close()
	sumfilerd.close()
		
if __name__ == '__main__':
	main()
