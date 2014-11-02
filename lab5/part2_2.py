#!/usr/bin/python

import nltk
import re

IN = re.compile(r'.*\b(president|chairman|chairwoman|manager|co-founder|director|co-director|executive|cheif|economist|partner|analyst)\b')

for doc in nltk.corpus.ieer.parsed_docs('NYT_19980315'):
    for rel in nltk.sem.extract_rels('PER', 'ORG', doc, corpus='ieer', pattern = IN):
        print(nltk.sem.rtuple(rel))