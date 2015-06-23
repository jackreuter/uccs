#!/usr/bin/python
# -*- coding: utf-8 -*-

import sqlite3 as lite
import sys

f = open ('../raw_data/w2_.txt','r')
con = lite.connect('hashtags.db')

with con and f:

    cur = con.cursor()
    
    for line in f:
        print line
        data = line.split("\t")
        count = int(data[0])
        word1 = data[1]
        word2 = data[2].strip()
        cur.execute("INSERT INTO Twograms VALUES('%s','%s',%d,%d,%d)" % (word1,word2,count,len(word1),len(word2)))

    cur.execute("SELECT * FROM Twograms")
    data = cur.fetchall()

    con.commit()
    con.close()
    print data
