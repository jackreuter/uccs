import sys
import sqlite3 as lite

f = open("../hashtags/pos",'r')
con = lite.connect('hashtags.db')

# grams1 = {}

# for line in f:
#     tags = line.strip().split(" ")
#     for tag in tags:
#         if tag in grams1:
#             grams1[tag]+=1
#         else:
#             grams1[tag]=1

# with con:
#     cur = con.cursor()

#     for gram in grams1:
#         #cur.execute("INSERT INTO POSonegrams VALUES('%s',%d)" % (gram))
#         print gram,grams1[gram]
