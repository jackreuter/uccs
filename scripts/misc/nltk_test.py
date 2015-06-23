import sys
import nltk

f = open("../../hashtags/1000seg.txt",'r')

for line in f:
    tokens = nltk.word_tokenize(line.strip().lower())
    tags = nltk.pos_tag(tokens)
    for tag in tags:
        print tag[0],tag[1]
    print ". break"
    print
    


