#!/usr/bin/python
# -*- coding: utf-8 -*-

import sqlite3 as lite
import sys
import math
import subprocess
import num2words

con = lite.connect('../../sqlite/hashtags.db')
cur = con.cursor()
punctuation = "_-.,!?`~*^+=:;'/"

#check if string is number
def isnumber(s):
    try:
        int(s)
        return True
    except ValueError:
        return False

#return start and end index of last number in string 0,0 if none
def findnumber(s):
    i=0
    j=0
    found=False
    for index,char in enumerate(s):
        if char.isdigit():
            if not found:
                found=True
                i=index
        else:
            if found:
                j=index
                found=False
    if found:
        j=len(s)
    return (i,j)
            
#check if string is ordinal
def isordinal(s):
    l=len(s)
    if l<3:
        return False
    else:
        if not isnumber(s[0:l-2]):
            return False
        else:
            n=int(s[0:l-2])
            e=s[l-2:].lower()
            if not(e=='th' or e=='st' or e=='nd' or e=='rd'):
                return False
            else:
                if (n%10==0 or n%10>3):
                    if e=='th':
                        return True
                    else:
                        return False
                elif (n%10==1):
                    if n%100==11:
                        if e=='th':
                            return True
                        else:
                            return False
                    else:
                        if e=='st':
                            return True
                        else:
                            return False
                elif (n%10==2):
                    if n%100==12:
                        if e=='th':
                            return True
                        else:
                            return False
                    else:
                        if e=='nd':
                            return True
                        else:
                            return False
                else:
                    if n%100==13:
                        if e=='th':
                            return True
                        else:
                            return False
                    else:
                        if e=='rd':
                            return True
                        else:
                            return False

#looks up string in dict database
def hasmatch(s):
    s = s.lower()
    if isnumber(s):
        return True
    if isordinal(s):
        return True
    with con:
        cur.execute("SELECT * FROM Dict WHERE Length=%d AND Entry='%s'" % (len(s),s))
        data = cur.fetchall()
        if len(data)>0:
            return True
        else:
            return False
    return False

#concats all of a with all of b, m in the middle, returns new set c
def concat(setA, m, setB, alen, blen):
    c = set()
    aSize = len(setA)
    bSize = len(setB)
    if aSize==0 and bSize==0:
        if alen==0 and blen==0:
            c.add(m)
    elif aSize==0:
        if alen==0:
            for b in setB:
                c.add(m+" "+b)
    elif bSize==0:
        if blen==0:
            for a in setA:
                c.add(a+" "+m)
    else:
        for b in setB:
            for a in setA:
                c.add(a+" "+m+" "+b)
    return c

#gets all possible segmentations of a string s
#if allowUnknowns then it will accept segments containing unknowns such that
#any uknown has at least one known word neighbor
#otherwise it requires that all strings are known

#matches dynamically updates with which substrings have matches
#partials dynamically updates with already seg substrings
def getallsegs(s, allowUnknowns):
    for i,char in enumerate(s):
        if char in punctuation:
            return concat(getallsegs(s[0:i],allowUnknowns),char,getallsegs(s[i+1:len(s)],allowUnknowns),i,len(s)-(i+1))
    l = len(s)

    #first segment out number/ordinal
    (i,j) = findnumber(s)
    if not (i,j)==(0,0):
        nsegs = concat(getallsegs(s[0:i],allowUnknowns),s[i:j],getallsegs(s[j:len(s)],allowUnknowns),i,len(s)-j)
        osegs = set()
        if isordinal(s[i:j+2]):
            j += 2
            osegs = concat(getallsegs(s[0:i],allowUnknowns),s[i:j],getallsegs(s[j:len(s)],allowUnknowns),i,len(s)-j)
        return nsegs | osegs

    #too slow if >17 characters
    if len(s)>17:
        matchedMids = []
        #i is length of middle segment
        #j is index of middle segment
        i=len(s)
        while i>0 and len(matchedMids)<3:
            j=0
            while j<len(s)-i+1 and len(matchedMids)<3:
                mid = s[j:j+i].lower()
                if hasmatch(mid):
                    matchedMids.append((i,j))
                j+=1
            i-=1
        segs = set()
        for mid in matchedMids:
            i = mid[0]
            j = mid[1]
            #print s[j:j+i]
            segs = segs | concat(getallsegs(s[0:j],allowUnknowns),s[j:j+i].lower(),getallsegs(s[j+i:len(s)],allowUnknowns),j,len(s)-(j+i))
        return segs
        
        # front = s[0:len(s)/2]
        # back = s[len(s)/2:len(s)]
        # frontSegs = gettop(getallsegs(front,allowUnknowns),lengthscore,2)
        # backSegs = gettop(getallsegs(back,allowUnknowns),lengthscore,2)
        # segs = set()
        # for f in frontSegs:
        #     fwords = f.split(" ")
        #     for b in backSegs:
        #         bwords = b.split(" ")
        #         middle = fwords[len(fwords)-1]+bwords[0]
        #         print middle
        #         mids = getallsegs(middle,allowUnknowns)
        #         for mid in mids:
        #             segs.add(" ".join(fwords[0:len(fwords)-1])+" "+mid+" "+" ".join(bwords[1:len(bwords)]))
        # return segs
                
    matches = [[0]*(l+1) for i in range(l+1)]
    partials = [[set("0")]*(l+1) for i in range(l+1)]    
    return getallsegsdyn(s,matches,partials,0,l,allowUnknowns)

def getallsegsdyn(s, matches, partials, start, l, au):
    segs = set()
    if l==0:
        return segs
    else:
        #i is length of middle segment
        #j is index of middle segment in reference to the whole string
        for i in range(l,0,-1):
            for j in range(start,start+l-i+1):
                mid = s[j:j+i]
                if matches[i][j]==1:
                    begSegs = partials[start][j-start]
                    endSegs = partials[i+j][l-(j-start+i)]
                    #print begSegs, endSegs
                    if "0" in begSegs:
                        begSegs = getallsegsdyn(s,matches, partials, start,j-start,au)
                        partials[start][j-start]=begSegs
                    if "0" in endSegs:
                        endSegs = getallsegsdyn(s,matches, partials, j+i,l-(j-start+i),au)
                        partials[i+j][l-(j-start+i)]=endSegs
                    segs = segs.union(concat(begSegs,mid.lower(),endSegs,j-start,l-(j-start+i)))
                elif matches[i][j]==0:
                    if hasmatch(mid):
                        begSegs = partials[start][j-start]
                        endSegs = partials[i+j][l-(j-start+i)]
                        if "0" in begSegs:
                            begSegs = getallsegsdyn(s,matches, partials, start,j-start,au)
                            partials[start][j-start]=begSegs
                        if "0" in endSegs:
                            endSegs = getallsegsdyn(s,matches, partials, j+i,l-(j-start+i),au)
                            partials[i+j][l-(j-start+i)]=endSegs
                        segs = segs.union(concat(begSegs,mid.lower(),endSegs,j-start,l-(j-start+i)))
                        matches[i][j]=1
                    else:
                        if (au and l==i):
                            segs.add(mid.upper())
                        matches[i][j]=-1
                else:
                     if (au and l==i):
                         segs.add(mid.upper())
    return segs


###--------------------------------------###
###-----------AMBIGUITY HANDLING---------###

#score a specific segmentation
#these are the functions that matter!!!!
#resolve ambiguities

#favors maximum matching and known words
def lengthscore(seg):
    score = 0
    words = seg.split(" ")
    for word in words:
        if word.islower() or isnumber(word) or isordinal(word) or word in punctuation:
            score += len(word)*len(word)
        else:
            score += len(word)
    # knownfactor = (known+.5)/(len(words)+.5)
    # return score*(knownfactor*knownfactor)
    score = score/len(words)
    #print score,":",seg
    return score

#converts number to possible string representations
def getnumberwords(s):
    full = num2words.num2words(int(s))
    split = full.split(" ")
    words = [[word.replace("-","") for word in split]]
    if s=="2":
        words.append(["to"])
        words.append(["too"])
    if s=="4":
        words.append(["for"])
    return words

#################unwritten
#gets possible translations of abbreviation a
def getabbrwords(a):
    return [[a]]

#check whether word in seg is abbrev
def isabbr(a):
    return True
#################nettirwnu

#favors common 2-grams and maximum matching
def twogramscore(seg):
    score = 1.0
    words = seg.split(" ")
    i = 0
    while i<len(words)-1:
        best = 0
        allPossibleWords1 = [[words[i]]]
        allPossibleWords2 = [[words[i+1]]]
        if isnumber(words[i]):
            allPossibleWords1 += getnumberwords(words[i])
        if isnumber(words[i+1]):
            allPossibleWords2 += getnumberwords(words[i+1])
        if isabbr(words[i]):
            allPossibleWords1 += getabbrwords(words[i])
        if isabbr(words[i+1]):
            allPossibleWords1 += getabbrwords(words[i+1])
        with con:
            for p1 in allPossibleWords1:
                for p2 in allPossibleWords2:
                    w1 = p1[len(p1)-1]
                    w2 = p2[0]
                    cur.execute("SELECT * FROM Twograms WHERE LOne=%d AND LTwo=%d AND WOne='%s' AND WTwo='%s'" % (len(w1),len(w2),w1,w2))
                    data = cur.fetchone()
                    if data:
                        wordScore = int(data[2])
                        if wordScore>best:
                            best = wordScore
        score += best
        i += 1
    if len(words)>1:
        score = math.pow(score, 1.0/(len(words)-1))
    if score<5:
        score = 1.0
    score = score*lengthscore(seg)
    #print score,":",seg
    return score
        
#get top l scoring segs
def gettop(segs, score, l):
    if len(segs)==0:
        return []
    else:
        segs = sorted(list(segs), key=lambda seg:score(seg), reverse=True)
        best = score(segs[0])
        i = 1
        while i<len(segs) and math.sqrt(best)<math.sqrt(score(segs[i]))+1:
            i += 1
        # while i<len(segs) and i<l:
        #     i += 1
            # for seg in segs:
            #     print score(seg),seg
        return segs[0:i]

    
###-------------------------------###
###---------UKNOWN HANDLING-------###

#assumes two or more single characters in a row have been missegmented
def concatsingletons(s):
    words = s.split(" ")
    out = ""
    singletons = ""
    for word in words:
        if len(word)>1 or isnumber(word) or word in punctuation:
            if len(singletons)==0:
                out += word+" "
            else:
                out += singletons+" "+word+" "
            singletons = ""
        else:
            singletons += word
    return (out+singletons).strip()

#counts # of occurences of string in twitter corpus
def tweetfreq(s):
    with con:
        cur.execute("SELECT * FROM Tweetwords WHERE Length=%d AND Text='%s'" % (len(s),s.lower()))
        data = cur.fetchall()
        return len(data)
    # try:
    #     cmd = """grep -c -i " %s " ../../raw_data/sentiment140-data/tweets""" % s
    #     return subprocess.check_output(cmd,shell=True)
    # except subprocess.CalledProcessError:
    #     return 0

#add new word to dictionary
def addtodict(s):
    with con:
        cur.execute("INSERT INTO Dict VALUES('%s',%d,'%s')" % (s.lower(),len(s),"twitter"))
        con.commit()

#checks whether all words in seg are legit
def checksout(seg):
    checksout = True
    for word in seg:
        if not hasmatch(word):
            if tweetfreq(word)>1:
                addtodict(word)
            else:
                checksout = False
    return checksout


##----------------------------------------------------------##        
##-------------------------CamelCase------------------------##

#checks if strings looks like its in camelcase
def camelcapsornone(s):
    upCount = 0
    loCount = 0
    currentcapseq = 0
    maxcapseq = 0
    for char in s:
        if char.isupper() and not isnumber(char):
            upCount += 1
            currentcapseq += 1
        if char.islower():
            loCount += 1
            if currentcapseq > maxcapseq:
                maxcapseq = currentcapseq
            currentcapseq = 0
    if upCount>0 and loCount>0:
        if maxcapseq > 2:
            return "caps"
        elif loCount>=upCount:
            return "camel"
    else:
        return "none"
            
#segments string based on camelcase
def camelseg(s):
    seg = ""
    word = ""
    for char in s:
        if char.isupper():
            seg += word+" "
            word = char
        else:
            word += char
    return concatsingletons((seg+word).strip())

#segments string based on chunks of caps
def capseg(s):
    seg = ""
    word = ""
    up = False
    if s[0].isupper():
        up = True
    for char in s:
        if char.isupper() and up or char.islower() and not up:
            word += char
        else:
            seg += word+" "
            word = char
            up = not up
    return (seg+word).strip()

            
##############################
#######FATTY FUNCTIONS########

#segment string s
def segment(s, camelCheck=True):
    print "segmenting..."
    #handle camelcase
    lookslike = "none"
    if camelCheck:
        lookslike = camelcapsornone(s)
    if lookslike=="camel":
        seg = camelseg(s)
        if checksout(seg):
            return seg
        else:
            return segment(s,False)
    elif lookslike=="caps":
        seg = capseg(s)
        if checksout(seg):
            return seg
        else:
            return segment(s,False)
    else:
        seg1 = segmentwithAU(s,True)
        seg2 = segmentwithAU(s,False)
        if seg1==seg2:
            return concatsingletons(seg1)
        else:
            words = seg1.split(" ")
            newseg = ""
            for word in words:
                if word.isupper():
                    if tweetfreq(word)>1:
                        newseg+=word+" "
                        addtodict(word)
                    else:
                        if len(words)>1:
                            newseg+=segment(word,False)+" "
                        else:
                            newseg+=word+" "
                else:
                    newseg+=word+" "
            return concatsingletons(newseg.strip())

#segment string s
def segmentwithAU(s,au):
    if hasmatch(s):
        return s
    else:
        # print s
        # print "segmenting..."
        segs = getallsegs(s,au)
        # print "pruning..."
        segs = gettop(segs,lengthscore,10)
        # print segs
        # print "choosing best..."
        segs = gettop(segs,twogramscore,2)
        # print segs
        if len(segs)==0:
            return s
        else:
            return concatsingletons(segs[0])

#segment all strings
def segmentall(f):
    with con:
        # for i in range(0,1000):
        #     cur.execute("SELECT * FROM Hashtags WHERE Id=%d" % i)
        #     data = cur.fetchone()
        #     hashtag = data[1]
        for line in f:
            hashtag = line.strip()
            print hashtag
            print segment(hashtag)+"\n"
            # cur.execute("SELECT * FROM Answers WHERE Id=%d" % i)
            # data = cur.fetchone()
            # answer = data[1]
    con.close()
