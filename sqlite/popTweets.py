#!/usr/bin/python
# -*- coding: utf-8 -*-

import sqlite3 as lite
import sys

f = open ('../raw_data/sentiment140-data/tweets','r')
con = lite.connect('hashtags.db')

with con and f:
    cur = con.cursor()
    count = 0
    for line in f:
        print count/1600000.0*100, "\033[F"
        tweet = line.strip()
        words = tweet.split(" ")
        for word in words:
            #print word
            word=word.decode('utf-8','ignore').encode("utf-8").lower()
            if len(word)>0:
                cur.execute("INSERT INTO Tweetwords VALUES('%s',%d)" % (word, len(word)))
        count += 1
            
    cur.execute("SELECT * FROM Tweetwords")
    data = cur.fetchall()
    con.commit()

con.close()
print data
