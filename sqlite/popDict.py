#!/usr/bin/python
# -*- coding: utf-8 -*-

import sqlite3 as lite
import sys

f = open ('../dicts/groomed/words','r')
con = lite.connect('hashtags.db')

with con and f:

    cur = con.cursor()
    
    for line in f:
        print line
        entry = line.strip()
        length = len(entry)
        source = "words"
        cur.execute("INSERT INTO Dict VALUES('%s',%d,'%s')" % (entry,length,source))

    cur.execute("SELECT * FROM Dict")
    data = cur.fetchall()

    con.commit()
    con.close()
    print data
