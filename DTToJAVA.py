import sys
import xml.etree.ElementTree as ET
import csv

def rparttoJava(pCsvf,pJavafile):
	csvf = open(pCsvf,'r')
	rows = csv.DictReader(csvf)
	code = '// rule start \n'
	code = code + rparttoJavaDFS(rows,0)
	writingToJava(code,pJavafile)

def rparttoJavaDFS(pRows,key):
	code = ''
	arow = pRows.__next__()
	if arow['var'] == '<leaf>':
		code = code + (key*' ')
		code = code + 'ret = ' + selectDistanceMetric(int(arow['yval'])) + ';\n'
	else:
		code = code + makeIf(arow,key)
		code = code + rparttoJavaDFS(pRows, key+1)
		code = code + (key*' ') + '}\n'
		code = code + makeElse(key)
		code = code + rparttoJavaDFS(pRows, key+1)
		code = code + (key*' ') + '}\n'
	return code
	
def makeIf(pRow,key):
	op = '<' if int(pRow['ncat']) == -1 else '>='
	space = key * ' '
	code = space +'if(pfts.' + pRow['var'] + op + pRow['index'] + '){\n'
	return code

def makeElse(key):
	space = key * ' '
	code = space + 'else{\n'
	return code

def selectDistanceMetric(pVal):
	if pVal == 1:
		return 'DistanceScheme.STATEMENTS'
	elif pVal == 2:
		return 'DistanceScheme.BASICBLOCKS'
	elif pVal == 3:
		return 'DistanceScheme.LOOPHEADS'
	elif pVal == 4:
		return 'DistanceScheme.LOOPSANDFUNCS'
	else:
		assert False, 'No appropriate distance metric'
		return 'Fail'

def writingToJava(code,pjfile):
	frontlines = []
	backlines = []
	javafile = open(pjfile,'r')
	inlines = javafile.readlines()
	key = 0

	for inline in inlines:
		if inline.find('rule end') != -1:
			key = 2
		elif inline.find('rule start') != -1:
			key = 1

		if key == 0: # front lines
			frontlines.append(inline)
		elif key == 2:
			backlines.append(inline)

	javafile.close()
	outfile = open(pjfile,'w')
	for line in frontlines:
		outfile.write(line)
	outfile.write(code)
	for line in backlines:
		outfile.write(line)
	outfile.flush()
	outfile.close()

def main(args):
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/cfa/EDSselection.java'
	rparttoJava(args[1],searchstrategyjavafile)

if __name__ == '__main__':
    main(sys.argv)
