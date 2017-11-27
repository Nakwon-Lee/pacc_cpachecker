class Comparator(metaclass = abc.ABCMeta):

	@abc.abstractmethod
	def compare(old, new):
		pass

class GeneralTSComparator(Comparator):

	def __init__(self):
		pass

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
		elif old['Result'] == new['Result'] and old['Result'] == 0:
			#print('Result is ',0)
			oldfit = (0.62807*old['NoAffS'])+(-1.27101*old['VL'])+(1.18150*old['VC'])-0.02434
			newfit = (0.62807*new['NoAffS'])+(-1.27101*new['VL'])+(1.18150*new['VC'])-0.02434
			if oldfit < newfit:
				ret = -1
			elif oldfit > newfit:
				ret = 1
			else:
				pass
		return ret
