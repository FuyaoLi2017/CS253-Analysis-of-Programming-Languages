#!/usr/bin/env python
import re, sys, operator

# Mileage may vary. If this crashes, make it lower
RECURSION_LIMIT = 9500
# We add a few more, because, contrary to the name,
# this doesn't just rule recursion: it rules the 
# depth of the call stack
sys.setrecursionlimit(RECURSION_LIMIT+10)


stop_words = set(open('../stop_words.txt').read().split(','))
words = re.findall('[a-z]{2,}', open(sys.argv[1]).read().lower())
word_freqs = {}

# Define the Y-combinator
Y = lambda F: F(lambda x: Y(F)(x))
def aa(wordfreq):
    print wordfreq[0][0],'-', wordfreq[0][1]
    return True
    
wf_print = Y(lambda f: lambda wordfreq: None if wordfreq == [] else aa(wordfreq) and f(wordfreq[1:])) 

for i in range(0, len(words), RECURSION_LIMIT):
    Y(lambda f: lambda wordlist: lambda stopwords: lambda wordfreqs: None if wordlist == []
      else f(wordlist[1:])(stopwords)(wordfreqs) if wordfreqs.update({wordlist[0]: (wordfreqs.get(wordlist[0], 0) + (1 if wordlist[0] not in stopwords else 0))}) is None
      else True)(words[i:i+RECURSION_LIMIT])(stop_words)(word_freqs)

wf_print(sorted(word_freqs.items(), key=operator.itemgetter(1), reverse=True)[:25])

