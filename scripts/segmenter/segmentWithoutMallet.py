from lookupfunctions import *
import sys

f = open ('../../hashtags/1129sup.txt','r')

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

segmentall(f)
