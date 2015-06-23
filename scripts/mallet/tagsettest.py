import subprocess
import sys

train = sys.argv[1]
test = sys.argv[2]
gold = open(sys.argv[3],'r')

# print "training..."
# subprocess.check_output("java cc.mallet.fst.SimpleTagger --train true --model-file tempcrf %s" % train, shell=True)

print "testing..."
out = subprocess.check_output("java cc.mallet.fst.SimpleTagger --model-file tempcrf %s" % test, shell=True)

outlines = out.split("\n")

total=0.0
correct=0.0
for i,line in enumerate(gold):
    words = line.strip().split(" ")
    tag = ""
    if len(words)>1:
        tag = words[1]
    if tag==outlines[i].strip():
        correct+=1
    total+=1
print "Accuracy:",correct/total

