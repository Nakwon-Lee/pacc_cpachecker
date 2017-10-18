import abc
import random
from TraversalStrategyModels import *

class GeneticAlgo(metaclass = abc.ABCMeta):

	@abc.abstractmethod
	def selection(self, population):
		pass

	@abc.abstractmethod
	def crossover(self, sol1, sol2):
		pass

	@abc.abstractmethod
	def mutation(self, sol):
		pass

class GARouletteWheelTree(GeneticAlgo):

	def __init__(self,poplen):
		self.mutprob = 0.2
		self.poplen = poplen
		self.selprob = 0.5
		# calculate the probability
		ktick = 20/poplen
		klist = []
		for i in range(poplen):
			klist.append(ktick*i)
		self.rangelist = []
		for i in range(poplen):
			value = 1-((1-self.selprob)**(klist[i]+1))
			self.rangelist.append(value)

	def selection(self, population):
		sol1 = -1
		sol2 = -1
		while(sol2 < 0):
			sprob = random.random()
			for i in range(self.poplen):
				if sprob < self.rangelist[i]:
					if sol1 < 0:
						sol1 = i
					else:
						if sol1 != i:
							sol2 = i
					break

		print(sol1, ' ', sol2)

		return population[sol1][0], population[sol2][0]

	def crossover(self, sol1, sol2):
		# parameters should be deepcopied
		# .toroot.children[1] is the target
		sol1nodes = sol1.getSubNodes(sol1.toroot.children[1])
		sol2nodes = sol2.getSubNodes(sol2.toroot.children[1])
		random.shuffle(sol1nodes)
		random.shuffle(sol2nodes)
		sol1point = sol1nodes[0]
		sol2point = sol2nodes[0]
		sol1idx = random.randrange(len(sol1point.children))
		sol1point.setChild(sol2point, sol1idx)
		newsol = sol1

		return newsol

	def mutation(self, sol):
		newsol = sol
		# node level mutation
		if random.random() < self.mutprob:
			solnodes = newsol.getSubNodes(newsol.toroot.children[1])
			random.shuffle(solnodes)
			solpoint = solnodes[0]
			solato = newsol.randomSelectionLabFuncs()
			solpoint.ato = solato
		# solution level mutation
		if random.random() < self.mutprob:
			newsol.randomOdrGen()
		return newsol
