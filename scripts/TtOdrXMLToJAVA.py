import xml.etree.ElementTree as ET

def xmltoJava(pXmlfile,pJavafile):
	elemtree = ET.parse(pXmlfile)
	elemroot = elemtree.getroot()
	#print('root: '+elemroot.tag)
	code = 'int ret = 0;\n'
	children = list(elemroot)
	for child in children:
		code = code + xmltoJavaDFS(child, 0)

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
	outfile.close()

def xmltoJavaDFS(elem, key):
	code = ''
	code = code + makeIfElse(elem.tag,elem.get('Odr'),key)

	domstr = elem.get('Domain')
	domlist = None
	if domstr is not None:
		domlist = domstr.split(',')	

	children = list(elem)

	if domlist is not None: # finite domain
		code = code + makeSwitchHeader(elem.tag,key)
		for i in range(len(children)):
			code = code + makeSwitchCase(domlist[i],key)
			code = code + xmltoJavaDFS(children[i], key+1)
			code = code + makeBreak(key)
		code = code + makeDefaultCase(key)
		code = code + makeBreak(key)
		code = code + (key*' ') + '}\n'
	else: # infinite domain
		for i in range(len(children)):
			code = code + xmltoJavaDFS(children[i], key+1)

	code = code + (key*' ') + '}\n'

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

