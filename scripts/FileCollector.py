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

def main():
	args = sys.argv
	dirname = args[1]
	filename = args[2]
	csvfile = args[3]
	fc = FileCollector(dirname, filename)
	fc.makeFilelistCsv(csvfile, './')

if __name__ == '__main__':
	main()


