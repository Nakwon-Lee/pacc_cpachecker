import abc
import math

class Comparator(metaclass = abc.ABCMeta):

	@abc.abstractmethod
	def compare(self, old, new):
		pass

	@abc.abstractmethod
	def preprocessing(self, pop,fitvars):
		pass

class GeneralTSComparator(Comparator):

	def __init__(self):
		pass

	def preprocessing(self, pop,fitvars):
		genFit = []
		maxs = {}
		mins = {}
		for fitvar in fitvars:
			tlist = []
			for j in range(len(pop)):
				tlist.append(pop[j][1][fitvar])
			maxs[fitvar] = max(tlist)
			mins[fitvar] = min(tlist)

		for i in range(len(pop)):
			genval = {}
			for fitvar in fitvars:
				if fitvar != 'Time' and fitvar != 'Result':
					diff = maxs[fitvar]-mins[fitvar]
					if diff == 0:
						genval[fitvar] = 0
					else:
						genval[fitvar] = (pop[i][1][fitvar]-mins[fitvar])/(maxs[fitvar]-mins[fitvar])
				else:
					genval[fitvar] = pop[i][1][fitvar]
			genFit.append(genval)
	
		return genFit

	def compare(self, old, new):

		ret = 0

		if old['Result'] > new['Result']:
			ret = -1
		elif old['Result'] < new['Result']:
			ret = 1
		elif old['Result'] == new['Result'] and old['Result'] == 1:
			#print('Result is ',1)
			if old['Time'] < new['Time']:
				ret = -1
			elif old['Time'] > new['Time']:
				ret = 1
			else:
				pass
		#elif old['Result'] == new['Result'] and old['Result'] == 0:
			#print('Result is ',0)
			#oldfit = (0.62807*old['NoAffS'])+(-1.27101*old['VL'])+(1.18150*old['VC'])-0.02434
			#newfit = (0.62807*new['NoAffS'])+(-1.27101*new['VL'])+(1.18150*new['VC'])-0.02434
			#if oldfit < newfit:
			#	ret = -1
			#elif oldfit > newfit:
			#	ret = 1
			#else:
			#	pass
		return ret

class FCTSComparator(Comparator):

	def __init__(self):
		pass

	def preprocessing(self, pop,fitvars):
		genFit = []

		successratelist = [] 

		maxs = {}
		mins = {}
		for fitvar in fitvars:
			tlist = []
			for j in range(len(pop)):
				tlist.append(pop[j][1][fitvar])
			maxs[fitvar] = max(tlist)
			mins[fitvar] = min(tlist)

		assert 'AFC' in fitvars and 'SFC' in fitvars, 'FC related features must be needed.'

		for i in range(len(pop)):
			assrate = 0
			if pop[i][1]['AFC'] != 0:
				assrate = pop[i][1]['SFC'] / pop[i][1]['AFC']
			successratelist.append(assrate)

		for i in range(len(pop)):
			genval = {}
			for fitvar in fitvars:
				if fitvar != 'Time' and fitvar != 'Result':
					diff = maxs[fitvar]-mins[fitvar]
					if diff == 0:
						genval[fitvar] = 0
					else:
						genval[fitvar] = (pop[i][1][fitvar]-mins[fitvar])/(maxs[fitvar]-mins[fitvar])
				else:
					genval[fitvar] = pop[i][1][fitvar]

			genval['FCSR'] = successratelist[i]

			genFit.append(genval)
	
		return genFit

	def compare(self, old, new):

		#('NoAffS','VL','VC','Time','Result','AFC','SFC','NoR')

		ret = 0

		olddist = math.sqrt(math.pow((0-old['NoR']),2) + math.pow((0-old['AFC']),2))
		newdist = math.sqrt(math.pow((0-new['NoR']),2) + math.pow((0-new['AFC']),2))

		if old['Result'] > new['Result']:
			ret = -1
		elif old['Result'] < new['Result']:
			ret = 1
		else:
			if olddist > newdist:
				ret = 1
			elif olddist < newdist:
				ret = -1
			else:
				if old['FCSR'] > new['FCSR']:
					ret = -1
				elif old['FCSR'] < new['FCSR']:
					ret = 1
				else:
					pass

		return ret
