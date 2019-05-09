import sys

DIRECTORY_PATH = 'sftpbatches/'
BATCH_STR_PRE = 'put results/benchmark* nwlee/RQ1-6/T'
BATCH_STR_MID = '/m'
BATCH_STR_SUF = '/\nquit\n'
FILE_NAME_PRE = 'kresultup'

'''
usage: python Makekresultup.py <trial num> <machine num>
'''

def main():
    args = sys.argv
    batchname = DIRECTORY_PATH + FILE_NAME_PRE
    batchfile = open(batchname,'w')

    inst = BATCH_STR_PRE + args[1] + BATCH_STR_MID + args[2] + BATCH_STR_SUF

    batchfile.write(inst)

    batchfile.close()

if __name__ == "__main__":
    main()