#!/usr/bin/python

import nltk
from os import listdir

for f in listdir('articles'):

    article_path = 'articles/' + f
    print "Processing " + f + "..."
    
    with open((article_path), "r") as myfile:
        data = myfile.read()

    sentences = nltk.sent_tokenize(data)
    sentences = [nltk.word_tokenize(sent) for sent in sentences]
    sentences = [nltk.pos_tag(sent) for sent in sentences]
    
    for sent in sentences:
        sentence = nltk.ne_chunk(sent)
        for chunk in sentence:
            if type(chunk) is nltk.tree.Tree:
                token = ''
                for word in chunk.leaves(): token = token + " " + word[0]
                print " " + token + ", " + chunk.label()