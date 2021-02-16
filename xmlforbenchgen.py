import sys
import re
from xml.etree.ElementTree import ElementTree, Element, parse, dump

def main():
	args = sys.argv
	xmltree = parse(args[1])
	machinenum = args[2]
	root = xmltree.getroot()
	indent(root)
	#dump(root)

	runs = root.findall('rundefinition')

	for arun in runs:
		tasks = arun.findall('tasks')
		for task in tasks:
			task.set('name','oneerrlabel-' + args[2])
			includes = task.findall('includesfile')
			for include in includes:
				include.text = '../sv-benchmarks/c/oneerrlabel-' + args[2] + '.set'

	# if len(args) == 4:
	# 	rundefs = root.findall('rundefinition')

	# 	for rundef in rundefs:
	# 		print(rundef.get('name'))
	# 		rundefnamestr = rundef.get('name')
	# 		namertokens = rundefnamestr.split('-')
	# 		newrnamestr = ''
	# 		for i in range(len(namertokens)-1):
	# 			newrnamestr = newrnamestr + namertokens[i] + '-'
	# 		newrnamestr = newrnamestr + args[3]
	# 		rundef.attrib['name'] = newrnamestr	

	#dump(root)

	ElementTree(root).write(args[3])

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
