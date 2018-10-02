from TraversalStrategyModels import *
import TSSearch as TSS
import xml.etree.ElementTree as ET
import sys

def XMLTottOdr(pxmlfile):
	labfuncs = (('isAbs',1,(0,1),0),('CS',0,1),('RPO',0,1),('CS',0,0),('blkD',0,0),('blkD',0,1),('RPO',0,0),('uID',0,0),('uID',0,1),('LenP',0,1),('LenP',0,0),('loopD',0,1),('loopD',0,0))
	atos = None

	atos = TSS.makingAtomTotalOrders(labfuncs)

	ttodr = TraversalStrategy(atos)

	xmltree = ET.parse(pxmlfile)
	xmlroot = xmltree.getroot()
	children = list(xmlroot)

	ttodr.toroot = xmlttodrDFS(children[0],atos)

	return ttodr

def xmlttodrDFS(xmlnode,patos):
	returnnode = None
	children = list(xmlnode)
	if xmlnode.tag == 'dumTtOdr':
		returnnode = TtOdrNode(None)
	elif xmlnode.tag == 'aTtOdr':
		mato = findAto(xmlnode,patos)
		returnnode = TtOdrNode(mato)
		returnnode.addChild(xmlttodrDFS(children[0],patos))
	elif xmlnode.tag == 'FaTtOdr':
		mfato = findFAto(xmlnode,patos)
		mdom = xmlnode.get('Domain')
		domlist = None
		if mdom is not None:
			domlist = mdom.split(',')
		assert domlist != None, 'domain must be exist'
		returnnode = TtOdrNode(mfato)
		for i in range(len(domlist)):
			returnnode.addChild(xmlttodrDFS(children[i],patos))

	return returnnode

def findAto(pxmlnode,patos):
	rato = None
	
	mname = pxmlnode.get('Name')
	modr = pxmlnode.get('Odr')

	for ato in patos:
		if isinstance(ato,AtomicTotalOrder):
			if ato.name == mname and ato.odr == int(modr):
				rato = ato
				break

	assert rato != None, 'ato must be found'

	return rato

def findFAto(pxmlnode,patos):
	rato = None
	
	mname = pxmlnode.get('Name')
	modr = pxmlnode.get('Odr')
	mdom = pxmlnode.get('Domain')
	domlist = None
	if mdom is not None:
		domlist = mdom.split(',')
	assert domlist != None, 'domain must be exist'

	for ato in patos:
		if isinstance(ato,FiniteDomainTotalOrder):
			if ato.name == mname and ato.odr == int(modr):
				valid = True
				if len(domlist) == len(ato.domain):
					for i in range(len(domlist)):
						if int(domlist[i]) != ato.domain[i]:
							valid = False
				else:
					valid = False
				if valid:
					rato = ato
					break

	assert rato != None, 'ato must be found'

	return rato

def main():
	xmlfile = sys.argv[1]
	ttodr = XMLTottOdr(xmlfile)
	ttodr.printTS()
	print(' ')
	ttodr.compressingTS()
	ttodr.printTS()
	print(' ')

if __name__ == '__main__':
    main()
