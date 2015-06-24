import sys
import nltk

f = open("../../hashtags/mallet/test/all",'r')

for line in f:
    words = line.strip().split(" ")
    if len(line.strip())>1:
        if words[1]=="noun" or words[1]=="num":
            print line.strip()
        else:
            print words[0],"non-noun"
    else:
        print line.strip()
    


