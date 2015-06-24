#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
from lookupfunctions import *

f = open("../../../hashtags/1000mallet.txt",'r')

# for line in f:
#     words = line.strip().split(" ")
#     for word in words:
#         if isnumber(word):
#             print word,"number"
#         elif isordinal(word):
#             print word,"ordinal"
#         elif word in punctuation:
#             print word,"punctuation"
#         else:
#             cur.execute("SELECT * FROM Dict WHERE Length=%d AND Entry='%s'" % (len(word),word.lower()))
#             data = cur.fetchall()
#             if len(data)==0:
#                 print word,"unknown"
#             else:
#                 print word,
#                 for piece in data:
#                     print piece[2],
#                 print
#     print ". break\n"

print isordinal("27TH")
