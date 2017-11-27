import sys
import csv
from pathlib import Path

class FileCollector:
	def __init__(self, dirname, filename):
		self.p = Path(dirname)
		self.files = filename
	def makeFilelistCsv(self, csvfile, mydir):
		self.filelist = set(self.p.glob(self.files))
		fieldnames = ['No.','file name']
		filediclist = []
		i = 0
		for afile in self.filelist:
			filediclist.append({fieldnames[0]:str(i),fieldnames[1]:afile})
			i = i + 1
		csvf = open(mydir+csvfile, 'w')
		csvwriter = csv.DictWriter(csvf, fieldnames=fieldnames)
		csvwriter.writeheader()
		for elem in filediclist:
			csvwriter.writerow(elem)
		csvf.close()

def main():
	args = sys.argv
	dirname = args[1]
	filename = args[2]
	csvfile = args[3]
	fc = FileCollector(dirname, filename)
	fc.makeFilelistCsv(csvfile, './')

if __name__ == '__main__':
	main()


