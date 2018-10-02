import sys
import xml.etree.ElementTree as ET

def xmltoJava(pXmlfile,pJavafile):
	elemtree = ET.parse(pXmlfile)
	elemroot = elemtree.getroot()
	#print('root: '+elemroot.tag)
	code = '// compare start \n'
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
		if inline.find('compare end') is not -1:
			key = 2
		elif inline.find('compare start') is not -1:
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
		code = code + (key*' ')
		code = code + 'return 0;\n'
	elif elem.tag == 'aTtOdr':
		code = code + makeIfElse(elem.get('Name'),elem.get('Odr'),key)
		code = code + xmltoJavaDFS(children[0], key+1)
		code = code + (key*' ') + '}\n'
	elif elem.tag == 'FaTtOdr':
		domstr = elem.get('Domain')
		domlist = None
		if domstr is not None:
			domlist = domstr.split(',')
		if domlist is not None:
			for i in range(len(domlist)):
				code = code + makeIfElseFato(elem.get('Name'),elem.get('Odr'),domlist[i],key)
			code = code + (key*' ') + '{\n'

			code = code + makeSwitchHeader(elem.get('Name'),key)
			# child 0
			# code = code + makeSwitchCaseIf(domlist[0],key)
			# code = code + xmltoJavaDFS(children[0], key+1)
			# code = code + makeBreak(key)
			# child 1~k
			for i in range(len(domlist)):
				code = code + makeSwitchCase(domlist[i],key)
				code = code + xmltoJavaDFS(children[i], key+1)
				code = code + makeBreak(key)
			# child k+1
			code = code + makeDefaultCase(key)
			code = code + xmltoJavaDFS(children[len(domlist)], key+1)
			code = code + (key*' ') + '}\n'
		code = code + (key*' ') + '}\n'
	else:	
		pass

	return code

def makeIfElse(plabelname,podr,key):
	odr = int(podr)
	space = key * ' '
	space1 = space + ' '
	code = space +'if(e1.'+plabelname+'() < e2.' + plabelname + '()){\n'
	code = code + space1
	if odr is 0:
		code = code + 'return 1;\n'
	elif odr is 1:
		code = code + 'return -1;\n'
	code = code + space +'}\n'
	code = code + space + 'else if(e1.'+plabelname+'() > e2.' + plabelname + '()){\n'
	code = code + space1
	if odr is 0:
		code = code + 'return -1;\n'
	elif odr is 1:
		code = code + 'return 1;\n'
	code = code + space +'}\n'
	code = code + space + 'else{\n'
	return code

def makeIfElseFato(plabelname,podr,pdom,key):
	odr = int(podr)
	space = key * ' '
	space1 = space + ' '
	code = space +'if(e1.'+plabelname+'() < ' + pdom + ' && e2.' + plabelname + '() >= ' + pdom + ' ){\n'
	code = code + space1
	if odr is 0:
		code = code + 'return 1;\n'
	elif odr is 1:
		code = code + 'return -1;\n'
	code = code + space +'}\n'
	code = code + space + 'else if(e1.'+plabelname+'() >= ' + pdom + ' && e2.' + plabelname + '() < ' + pdom + ' ){\n'
	code = code + space1
	if odr is 0:
		code = code + 'return -1;\n'
	elif odr is 1:
		code = code + 'return 1;\n'
	code = code + space +'}\n'
	code = code + space + 'else'
	return code

def makeSwitchHeader(plabelname,key):
	return (key*' ') + 'int thePhi = e1.'+plabelname+'();\n'

def makeSwitchCaseIf(dom,key):
	return (key*' ') + 'if(thePhi<' + dom + '){\n'

def makeSwitchCase(dom,key):
	return (key*' ') + 'if(thePhi<' + dom + '){\n'

def makeDefaultCase(key):
	return (key*' ') + '{\n'

def makeBreak(key):
	return ((key+1)*' ') + '}\n' + ((key+1)*' ') + 'else '

def main(args):
	searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/ABESearchStrategyFormula.java'
	if len(args) == 3:
		searchstrategyjavafile = 'src/org/sosy_lab/cpachecker/core/searchstrategy/' + args[2] + '.java'
	xmltoJava(args[1],searchstrategyjavafile)

if __name__ == '__main__':
    main(sys.argv)
