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

	def neighbourOdrGen(self):
		pass

	def randomOdrGen(self):
		self.toroot = TtOdrNode(None)
		leafs = set()
		k = self.assignValidAto(leafs,self.toroot)
		prob = 0.9
		#TODO add a child to the current node with specific probability repeatedly
		while(random.random() < prob and len(leafs) != 0):
			leaf = leafs.pop()
			r = self.assignValidAto(leafs,leaf)
			# probability decreasing
			prob = prob - 0.01

	def assignValidAto(self,leafs,currnode):
		ret = 0
		key = True
		nances = 0
		while(key):
			ato = self.randomSelectionLabFuncs()
			currnode.ato = ato
			key, nances = self.validationParents(currnode)
			if key:
				currnode.ato = None
				if nances >= self.namesize:
					key = False
					ret = -1
			else:
				if isinstance(ato,FiniteDomainTotalOrder): # can have more than one children
					for dom in ato.domain:
						child = TtOdrNode(None)
						currnode.addChild(child)
				elif isinstance(ato,AtomicTotalOrder): #can have only one child
					child = TtOdrNode(None)
					currnode.addChild(child)
				for child in currnode.children:
					leafs.add(child)
		return ret

	def randomSelectionLabFuncs(self):
		ato = random.choice(self.atos)
		#print('ran ato: ' + str(ato))
		return ato

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
		if node.ato is None:
			print(sttab + 'leaf')
		else:
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
		if self.ato is None:
			return 'None'
		else:
			return self.ato.toString()
