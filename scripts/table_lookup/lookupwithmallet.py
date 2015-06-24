from lookupfunctions import *
import sys

#f = open ('../../hashtags/1129sup.txt','r')
#segmentall(f)

cmd = """java -cp "/Users/jreuter/Documents/uccs/scripts/mallet" TrainHMM %s %s """ % (sys.argv[1], sys.argv[2])
print subprocess.check_output(cmd,shell=True)
