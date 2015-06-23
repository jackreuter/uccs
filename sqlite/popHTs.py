#!/usr/bin/python
# -*- coding: utf-8 -*-

import sqlite3 as lite
import sys

f = open ('../hashtags/1000seg.txt','r')
con = lite.connect('hashtags.db')

with con and f:

    cur = con.cursor()

    count = 0
    for line in f:
        print line
        text = line.strip()
        cur.execute("INSERT INTO Answers VALUES(%d,'%s')" % (count,text))
        count += 1

    cur.execute("SELECT * FROM Answers")
    data = cur.fetchall()

    con.commit()
    con.close()
    print data
