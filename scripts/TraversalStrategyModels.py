import random
from collections import deque

class TraversalStrategy:

	def __init__(self,patos):
		self.atos = patos
		nameset = set()
		for ato in self.atos:
			nameset.add(ato.name)
		self.namesize = len(nameset)
		print('namesize: ' + str(self.namesize))
		self.toroot = None

	def randomOdrGen(self):
		self.toroot = self.randomSelectionLabFuncs()
		currnode = self.toroot
		prob = 0.9
		#TODO add a child to the current node with specific probability repeatedly
		while(random.random() < prob):
			ato = currnode.ato
			if isinstance(ato,FiniteDomainTotalOrder): # can have more than one children
				ret1 = 0
				if len(ato.domain) > len(currnode.children):
					ret1 = self.addValidChild(currnode)
				if ret1 is 0:
					if random.random() < 0.2 and len(currnode.children) is not 0:
						idx = random.randrange(len(currnode.children))
						currnode = currnode.children[idx]
			elif isinstance(ato,AtomicTotalOrder): #can have only one child
				ret2 = 0
				if 1 > len(currnode.children):
					ret2 = self.addValidChild(currnode)
				if ret2 is 0:
					if random.random() < 0.7:
						currnode = currnode.children[0]
			else:
				print('ato should be the TotalOrder')
			if random.random() < 0.3 and currnode.parent is not None:
				currnode = currnode.parent
			prob = prob - 0.01

	def addValidChild(self,currnode):
		ret = 0
		key = True
		nances = 0
		while(key):
			mchild = self.randomSelectionLabFuncs()
			currnode.addChild(mchild)
			key, nances = self.validationParents(mchild)
			if key:
				currnode.popChild()
				if nances >= self.namesize:
					key = False
					ret = -1
			else:
				pass
		return ret

	def randomSelectionLabFuncs(self):
		ato = random.choice(self.atos)
		#print('ran ato: ' + str(ato))
		return TtOdrNode(ato)

	def validation(self): #validating made traversal strategy
		#TODO check whether all items in FiniteDomain are considred or not (child is not mandatory but it is divided for each domain item)
		#Same labfuncs in a path is not allowed
		pass

	def validationParents(self,pcnode):
		ret = False
		nancestors = 0
		par = pcnode.parent
		while(par is not None):
			if pcnode.ato.name is par.ato.name:
				ret = True
			par = par.parent
			nancestors = nancestors + 1

		return ret,nancestors

	def printTS(self):
		self.DFS(self.toroot,0)

	def DFS(self,node,tab):
		sttab = ''
		for i in range(tab):
			sttab = sttab + ' '
		print(sttab + node.ato.name + ' ' + str(node.ato.odr))
		tab = tab + 1
		for child in node.children:
			self.DFS(child,tab)

class AtomicTotalOrder:

	def __init__(self,pname,podr):
		self.name = pname
		self.odr = podr

	def toString(self):
		return ' (' + self.name + ': ' + str(self.odr) + ') '

class FiniteDomainTotalOrder(AtomicTotalOrder):

	def __init__(self,pname,podr,pdomain):
		super().__init__(pname,podr)
		self.domain = pdomain #domain is a tuple

class TtOdrNode:

	def __init__(self,pato):
		self.ato = pato
		self.parent = None
		self.children = []

	def addChild(self,pchild):
		self.children.append(pchild)
		pchild.setParent(self)

	def popChild(self):
		child = self.children.pop()
		child.delParent()

	def setParent(self,pparent):
		self.parent = pparent

	def delParent(self):
		self.parent = None

	def toString(self):
		return self.ato.toString()
