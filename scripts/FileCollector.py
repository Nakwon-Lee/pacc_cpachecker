import sys
import csv
from pathlib import Path

class FileCollector:
	def __init__(self, dirname, filename):
		self.p = Path(dirname)
		self.files = filename
		self.filelist = None
	def makeFilelistCsv(self, csvfile, mydir):
		fileset = set(self.p.glob(self.files))
		fieldnames = ['No.','file name']
		filediclist = []
		i = 0
		for afile in fileset:
			filediclist.append({fieldnames[0]:str(i),fieldnames[1]:str(afile)})
			i = i + 1
		self.filelist = filediclist
		csvf = open(mydir+csvfile, 'w')
		csvwriter = csv.DictWriter(csvf, fieldnames=fieldnames)
		csvwriter.writeheader()
		for elem in filediclist:
			csvwriter.writerow(elem)
		csvf.close()
	def makeFilelistCsvGiven(self, csvfile, mydir, givencsv):
		fieldnameex = ['No.','file name','valid']
		fieldname = ['No.','file name']
		givenf = open(givencsv)
		givenreader = csv.DictReader(givenf, fieldnames=fieldnameex)
		diclist = []
		i = 0
		givenreader.__next__()
		for rrow in givenreader:
			if rrow['valid'] == '1':
				tempdic = {}
				tempdic[fieldnameex[0]] = str(i)
				tempdic[fieldnameex[1]] = rrow['file name']
				diclist.append(tempdic)
				i = i + 1
		self.filelist = diclist
		csvf = open(mydir+csvfile, 'w')
		csvwriter = csv.DictWriter(csvf, fieldnames=fieldname)
		csvwriter.writeheader()
		for elem in diclist:
			csvwriter.writerow(elem)
		csvf.close()
		givenf.close()

	def makeFilelistCsvGiven2(self, csvfile, mydir, givencsv):
		fieldnameex = ['No.','file name']
		fieldname = ['No.','file name']
		givenf = open(givencsv)
		givenreader = csv.DictReader(givenf, fieldnames=fieldnameex)
		diclist = []
		i = 0
		givenreader.__next__()
		for rrow in givenreader:
			tempdic = {}
			tempdic[fieldnameex[0]] = str(i)
			tempdic[fieldnameex[1]] = rrow['file name']
			diclist.append(tempdic)
			i = i + 1
		self.filelist = diclist
		csvf = open(mydir+csvfile, 'w')
		csvwriter = csv.DictWriter(csvf, fieldnames=fieldname)
		csvwriter.writeheader()
		for elem in diclist:
			csvwriter.writerow(elem)
		csvf.close()
		givenf.close()

	def makeFileListGivenSets(self, csvfile, givensets):
		filenamelist = []

		for aset in givensets:
			filec = open(aset)
			lines = filec.readlines()
			for line in lines:
				spline = line.splitlines()
				if spline[0] == '':
					pass
				else:
					fileset = set(self.p.glob(spline[0]))
					for afile in fileset:
						filenamelist.append('../' + str(afile))
			filec.close()

		print('No. of files: ',len(filenamelist))
		csvfilec = open(csvfile,'w')
		for astr in filenamelist:
			csvfilec.write(astr + '\n')
		csvfilec.close()

	def divideFilesToSets(self, files, nameprefix, divideno):
		dividefiles = []

		for i in range(divideno):
			filename = nameprefix + str(i) + '.set'
			dividefiles.append(filename)

		setfilef = open(files,'r')
		lines = setfilef.readlines()
		setfilef.close()

		fullfiles = []
		for aline in lines:
			spline = aline.splitlines()

			if spline[0] == '':
				pass
			else:
				fullfiles.append(spline[0])
		
		divfilelist = []
		for k in range(divideno):
			initlist = []
			divfilelist.append(initlist)

		idx = 0			
		for afilestr in fullfiles:
			idx = idx % divideno
			divfilelist[idx].append(afilestr)
			idx = idx + 1

		for m in range(divideno):
			divafilef = open(dividefiles[m],'w')
			for afilen in divfilelist[m]:
				divafilef.write(afilen + '\n')
			divafilef.close()

def main():
	args = sys.argv
	dirname = args[1]
	prefix = None
	filename = None
	csvfile = None

	if dirname == 'SETS':
		prefix = args[2]
		csvfile = args[3]
		filename = []
		for i in range(4,len(args)):
			filename.append(args[i])
		fc = FileCollector(prefix, None)
		fc.makeFileListGivenSets(csvfile, filename)
	elif dirname == 'DIV':
		prefix = args[2]
		filename = args[3]
		divno = int(args[4])
		fc = FileCollector(prefix, None)
		fc.divideFilesToSets(filename, prefix, divno)
	else:
		filename = args[2]
		csvfile = args[3]
		fc = FileCollector(dirname, filename)
		fc.makeFilelistCsv(csvfile, './')

if __name__ == '__main__':
	main()


