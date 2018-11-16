import sys
import re
from xml.etree.ElementTree import ElementTree, Element, parse, dump

def main():
	args = sys.argv
	xmltree = parse(args[1])
	machinenum = args[2]
	root = xmltree.getroot()
	indent(root)
	dump(root)

	tasks = root.findall('tasks')

	for task in tasks:
		print(task.get('name'))
		tasknamestr = task.get('name')
		nametokens = tasknamestr.split('-')
		newnamestr = nametokens[0] + '-' + machinenum
		task.attrib['name'] = newnamestr
		includes = task.findall('includesfile')
		for include in includes:
			filestr = include.text
			tokens = filestr.split('-')
			newstr = tokens[0] + '-' + machinenum + '.set'
			include.text = newstr

	if len(args) == 4:
		rundefs = root.findall('rundefinition')

		for rundef in rundefs:
			print(rundef.get('name'))
			rundefnamestr = rundef.get('name')
			namertokens = rundefnamestr.split('-')
			newrnamestr = ''
			for i in range(len(namertokens)-1):
				newrnamestr = newrnamestr + namertokens[i] + '-'
			newrnamestr = newrnamestr + args[3]
			rundef.attrib['name'] = newrnamestr	
			options = rundef.findall('option')
			for option in options:
				print(option.get('name'))
				optstr = option.get('name')
				if optstr != '-noout':
					opttokens = optstr.split('-')
					newoptstr = ''
					for k in range(len(opttokens)-1):
						newoptstr = newoptstr + opttokens[k] + '-'
					newoptstr = newoptstr + args[3]
					option.attrib['name'] = newoptstr

	dump(root)

	ElementTree(root).write(args[1])

def indent(elem, level=0):
    i = '\n' + level*'  '
    if len(elem):
        if not elem.text or not elem.text.strip():
            elem.text = i + "  "
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
        for elem in elem:
            indent(elem, level+1)
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            elem.tail = i

if __name__ == '__main__':
	main()
