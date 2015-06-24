from lookupfunctions import *
import sys

f = open ('../../hashtags/1129sup.txt','r')

cmd = """java TrainHMM %s %s """ % (sys.argv[1], sys.argv[2])
print subprocess.check_output(cmd,shell=True)

#segment string s
def segment(s):
    print "segmenting..."
    if hasmatch(s):
        return s
    else:
        segs = getallsegs(s,True)
        topscore = 0.0
        topseg = ""
        for seg in segs:
            malletscore = getmalletscore(seg)
            if malletscore > topscore:
                topseg = seg
                topscore = score
        return topseg

#gets best label sequence based on trained HMM or CRF
#then gets prob of that label sequence based on the same model
def getmalletscore(s):
    return 1.0
