
class TraversalStrategy:

	def __init__(self,patos):
		self.atos = patos
		self.toroot = None

	def validation(self): #validating made traversal strategy
		#TODO check whether all items in FiniteDomain are considred or not
		pass

class AtomicTotalOrder:

	def __init__(self,pname):
		self.name = pname

class FiniteDomainTotalOrder(AtomicTotalOrder):

	def __init__(self,pname,pdomain):
		super().__init__(pname)
		self.domain = pdomain #domain is a tuple

class TONode:

	def __init__(self,pato):
		self.ato = pato
		self.children = []
