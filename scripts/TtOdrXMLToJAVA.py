import sys
import xml.etree.ElementTree as ET

def xmltoJava(pXmlfile,pJavafile):
	elemtree = ET.parse(pXmlfile)
	elemroot = elemtree.getroot()
	#print('root: '+elemroot.tag)
	code = 'int ret = 0;\n'
	children = list(elemroot)
	for child in children:
		code = code + xmltoJavaDFS(child, 0)
	ET.dump(elemtree)
	writingToJava(code,pJavafile)

def writingToJava(code,pjfile):
	frontlines = []
	backlines = []
	javafile = open(pjfile,'r')
	inlines = javafile.readlines()
	key = 0

	for inline in inlines:
		if inline.find('return') is not -1:
			key = 2
		elif inline.find('int ret = 0;') is not -1:
			key = 1

		if key is 0: # front lines
			frontlines.append(inline)
		elif key is 2:
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

def xmltoJavaDFS(elem, key):
	code = ''
	children = list(elem)
	if elem.tag == 'dumTtOdr':
		pass
	elif elem.tag == 'aTtOdr':
		code = code + makeIfElse(elem.get('Name'),elem.get('Odr'),key)
		code = code + xmltoJavaDFS(children[0], key+1)
		code = code + (key*' ') + '}\n'
	elif elem.tag == 'FaTtOdr':
		code = code + makeIfElse(elem.get('Name'),elem.get('Odr'),key)
		domstr = elem.get('Domain')
		domlist = None
		if domstr is not None:
			domlist = domstr.split(',')
		if domlist is not None: # finite domain
			code = code + makeSwitchHeader(elem.get('Name'),key)
			for i in range(len(children)):
				code = code + makeSwitchCase(domlist[i],key)
				code = code + xmltoJavaDFS(children[i], key+1)
				code = code + makeBreak(key)
			code = code + makeDefaultCase(key)
			code = code + makeBreak(key)
			code = code + (key*' ') + '}\n'
		code = code + (key*' ') + '}\n'
	else:	
		pass

	return code

def makeIfElse(plabelname,podr,key):
	odr = int(podr)
	space = key * ' '
	space1 = space + ' '
	code = space +'if(e1.'+plabelname+'()'
	if odr is 0:
		code = code + '<'
	elif odr is 1:
		code = code + '>'
	code = code + 'e2.' + plabelname + '()){\n'
	code = code + space1
	code = code + 'ret = -1;\n'
	code = code + space +'}\n'
	code = code + space + 'else if(e1.'+plabelname+'()'
	if odr is 0:
		code = code + '>'
	elif odr is 1:
		code = code + '<'
	code = code + 'e2.' + plabelname + '()){\n'
	code = code + space1
	code = code + 'ret = 1;\n'
	code = code + space +'}\n'
	code = code + space + 'else{\n'
	return code

def makeSwitchHeader(plabelname,key):
	return (key*' ') + 'switch(e1.'+plabelname+'()){\n'

def makeSwitchCase(dom,key):
	return (key*' ') + 'case ' + dom + ':\n'

def makeDefaultCase(key):
	return (key*' ') + 'default:\n'

def makeBreak(key):
	return ((key+1)*' ') + 'break;\n'

def main(args):
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/MySearchStrategyFormula.java'
	xmltoJava(args[1],searchstrategyjavafile)

if __name__ == '__main__':
    main(sys.argv)
