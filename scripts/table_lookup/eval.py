#!/usr/bin/python

import sys
from lookupfunctions import *

gold = open(sys.argv[1],'r')
test = open(sys.argv[2],'r')

#get binary representation of seg
def toBin(s):
    bin="";
    i=0;
    while i<len(s)-1:
        if s[i+1:i+2]==" ":
            bin+="1"
            i+=2
        else:
            bin+="0"
            i+=1
    return bin

#counts how many strings correctly segmented
def countMatches(g, t):
    matches=0
    if len(g)!=len(t):
        print "error: bins are not same length"
        return matches
    else:
        i=0
        synced=True
        while i<len(g):
            if g[i]==t[i]:
                if g[i]=="0":
                    i+=1
                else:
                    if synced:
                        matches+=1
                        i+=1
                    else:
                        synced=True
                        i+=1
            else:
                synced=False
                i+=1
        if synced:
            matches+=1
    return matches

#gets precision of t compared to g
def getPrecision(g, t):
    return countMatches(t,g)/float(t.count('1')+1)

#gets recall of t compared to g
def getRecall(g, t):
    return countMatches(t,g)/float(g.count('1')+1)


##################
#compare the files

total = 0.0
prec = 0.0
rec = 0.0
for g,t in zip(gold,test):
    total += 1
    #print g,t,
    g=g.strip()
    t=t.strip()
    g=concatsingletons(g.strip())
    t=concatsingletons(t.strip())
    # print toBin(g),g,
    # print toBin(t),t
    # print getPrecision(toBin(g),toBin(t)),
    # print getRecall(toBin(g),toBin(t))
    lineprec = getPrecision(toBin(g),toBin(t))
    linerec = getRecall(toBin(g),toBin(t))
    # if lineprec<1.0:
    #     print twogramscore(t),"\t",t
    prec += lineprec
    rec += linerec
    #print prec/total,rec/total,"\n"

prec = prec/total
rec = rec/total
print "Precision:",prec
print "Recall:",rec

