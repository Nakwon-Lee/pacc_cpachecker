import random
from collections import deque

class TraversalStrategy:

	def __init__(self,patos):
		self.origatos = patos
		self.atos = patos[1:len(patos)-1]
		self.atox = patos[0:3]
		nameset = set()
		for ato in self.atos:
			nameset.add(ato.name)
		self.namesize = len(nameset)
		#print('namesize: ' + str(self.namesize))
		self.toroot = None

	def neighbourOdrGen(self):
		ret = -1
		#key = random.randrange(2)
		key = 1
		retts = None
		if key == 0:
			#Change order
			retts, ret = self.genChangeOrder()
		elif key == 1:
			#Swap nodes (no modification of tree structure)
			retts, ret = self.genSwapNodes()
		elif key == 2:
			pass
			#Add a subtree to a leaf
		#Swap subtrees
		if ret < 0:
			print("neighbour gen fail")

		return retts

	def getParIdx(self, node):
		idx = -1
		if node.parent != None:
			idx = node.parent.children.index(node)

		return idx

	def genSwapNodes(self): #swapped nodes should be the AtomicTotalOrder
		key = True
		ret = -1
		tempts = None
		tick = 0

		while(key and tick < 5):
			tempts = self.deepcopyTS()
			tempts.printTS()
			nodes = tempts.getNodesAto()
			if len(nodes) < 2:
				break
			n1 = nodes.pop()
			n2 = nodes.pop()
			print(n1.ato.name,' ',n2.ato.name)
			n1par = n1.parent
			n1paridx = self.getParIdx(n1)
			n1chi = n1.children[0]
			n2par = n2.parent
			n2paridx = self.getParIdx(n2)
			n2chi = n2.children[0]

			if n1 is not n2par:
				if n2par != None:
					n1.parent = n2par
					n2par.children[n2paridx] = n1
				else:
					n1.parent = None
					tempts.toroot = n1
			else:
				n1.parent = n2
				n2.children[0] = n1

			if n1 is not n2chi:
				n1.children[0] = n2chi
				n2chi.parent = n1
			else:
				n1.children[0] = n2
				n2.parent = n1

			if n2 is not n1par:
				if n1par != None:
					n2.parent = n1par
					n1par.children[n1paridx] = n2
				else:
					n2.parent = None
					tempts.toroot = n2
			else:
				n2.parent = n1
				n1.children[0] = n2

			if n2 is not n1chi:
				n2.children[0] = n1chi
				n1chi.parent = n2
			else:
				n2.children[0] = n1
				n1.parent = n2

			tempts.printTS()

			isvalid = tempts.validation()

			print(isvalid)

			if isvalid:
				key = False
				ret = 0

			tick = tick + 1

		if ret == 0:
			return tempts, ret
		else:
			return self, ret

	def genChangeOrder(self):
		ret = -1
		nodes = self.getNodes()
		node = nodes.pop()
		oppato = self.getOppositeOrder(node.ato)
		if oppato != None:
			node.ato = oppato
			ret = 0

		return self, ret

	def getOppositeOrder(self,pato):
		retato = None
		for ato in self.atos:
			if ato.name == pato.name and ato.odr != pato.odr:
				retato = ato

		return retato

	def getNodes(self):
		nodes = []
		stack = []
		stack.append(self.toroot)
		while len(stack) > 0:
			node = stack.pop()
			if node.ato != None:
				nodes.append(node)
			for child in node.children:
				stack.append(child)

		random.shuffle(nodes)

		return nodes

	def getSubNodes(self, root):
		nodes = []
		stack = []
		stack.append(root)
		while len(stack) > 0:
			node = stack.pop()
			if node.ato != None:
				nodes.append(node)
			for child in node.children:
				stack.append(child)

		return nodes

	def getLeafs(self):
		leafs = []
		stack = []
		stack.append(self.toroot)
		while len(stack) > 0:
			node = stack.pop()
			if node.ato == None:
				leafs.append(node)
			for child in node.children:
				stack.append(child)

		random.shuffle(leafs)

		return leafs

	def getNodesAto(self):
		nodes = []
		stack = []
		stack.append(self.toroot)
		while len(stack) > 0:
			node = stack.pop()
			if node.ato != None and type(node.ato) is not FiniteDomainTotalOrder:
				nodes.append(node)
			for child in node.children:
				stack.append(child)

		random.shuffle(nodes)

		return nodes

	def randomOdrGen(self):
		self.toroot = TtOdrNode(None)
		leafs = []
		#k = self.assignValidAto(leafs,self.toroot)
		self.assignRanAto(leafs,self.toroot)
		prob = 0.9
		#TODO add a child to the current node with specific probability repeatedly
		while(random.random() < prob and len(leafs) != 0):
			random.shuffle(leafs)
			leaf = leafs.pop()
			#r = self.assignValidAto(leafs,leaf)
			self.assignRanAto(leafs,leaf)
			# probability decreasing
			prob = prob - 0.01
		self.finishingTS()

	def assignRanAto(self,leafs,currnode):
		ret = 0
		key = True
		nances = 0
		ato = self.randomSelectionLabFuncs()
		currnode.ato = ato
		
		if isinstance(ato,FiniteDomainTotalOrder): # can have more than one children
			for dom in ato.domain:
				child = TtOdrNode(None)
				currnode.addChild(child)
		elif isinstance(ato,AtomicTotalOrder): #can have only one child
			child = TtOdrNode(None)
			currnode.addChild(child)
		for child in currnode.children:
			leafs.append(child)

	def assignValidAto(self,leafs,currnode):
		ret = 0
		key = True
		nances = 0
		while(key):
			ato = self.randomSelectionLabFuncs()
			currnode.ato = ato
			key, nances = self.validationParents2(currnode)
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
					leafs.append(child)
		return ret

	def randomSelectionLabFuncs(self):
		ato = random.choice(self.atos)
		#print('ran ato: ' + str(ato))
		return ato

	def validation(self): #validating made traversal strategy
		#TODO check whether all items in FiniteDomain are considred or not (child is not mandatory but it is divided for each domain item)
		#Same labfuncs in a path is not allowed
		leafs = self.getLeafs()
		isvalid = False
		for leaf in leafs:
			print('leaf!')
			isvalid,l = self.validationParents(leaf)
			if isvalid:
				break

		return not isvalid

	def validationParents2(self,pcnode): #pcnode should be a non dummy node
		ret = False
		nancestors = 0
		par = pcnode.parent
		while(par is not None):
			if type(pcnode.ato) is AtomicTotalOrder and type(par.ato) is AtomicTotalOrder:
				if pcnode.ato.name == par.ato.name:
					ret = True
					break
			par = par.parent
			nancestors = nancestors + 1

		return ret,nancestors

	def validationParents(self,pcnode): #pcnode should be a dummy (leaf) node
		ret = False
		nancestors = 0
		node = pcnode.parent
		nameset = set()
		while(node is not None):
			if node.ato.name not in nameset:	
				nameset.add(node.ato.name)
			else:
				ret = True
			node = node.parent
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

	def deepcopyTS(self):
		clonets = TraversalStrategy(self.origatos)
		clonets.toroot = self.deepcopyTSDFS(self.toroot)

		return clonets

	def deepcopyTSDFS(self,node):
		clonenode = TtOdrNode(node.ato)

		for child in node.children:
			clonechild = self.deepcopyTSDFS(child)
			clonenode.addChild(clonechild)
			clonechild.setParent(clonenode)

		return clonenode

	def finishingTS(self):
		newroot = TtOdrNode(self.atox[0])
		child1 = TtOdrNode(self.atox[1])
		newroot.addChild(child1)
		child11 = TtOdrNode(self.atox[2])
		child1.addChild(child11)
		child111 = TtOdrNode(None)
		child11.addChild(child111)
		newroot.addChild(self.toroot)
		self.toroot = newroot

	def compressingTS(self): #the same function of ART nodes is not allowed in an order tree path
		nodestack = []
		nodestack.append(self.toroot)
		while(len(nodestack)!=0):
			cnode = nodestack.pop()
			if cnode.ato == None:
				pass
			else:
				if type(cnode.ato) is AtomicTotalOrder:
					isdupl,nancestos = self.validationParents2(cnode)
					mchild = cnode.children[0]
					if isdupl: #the node is duplicated ato
						par = cnode.parent
						idx = self.getParIdx(cnode)
						par.setChild(mchild,idx)
						cnode.delParent()
						cnode.delChild(0)
					nodestack.append(mchild)
				elif type(cnode.ato) is FiniteDomainTotalOrder:
					for i in range(len(cnode.children)):
						nodestack.append(cnode.children[i])


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

	def setChild(self,pchild,idx):
		self.children[idx] = pchild
		pchild.setParent(self)

	def popChild(self):
		child = self.children.pop()
		child.delParent()

	def delChild(self,idx):
		child = self.children[idx] = None

	def setParent(self,pparent):
		self.parent = pparent

	def delParent(self):
		self.parent = None

	def toString(self):
		if self.ato is None:
			return 'None'
		else:
			return self.ato.toString()
