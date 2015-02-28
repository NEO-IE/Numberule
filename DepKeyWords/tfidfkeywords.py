'''
Attempts to find the most important words in a corpus using tf-idf scores
'''
from sklearn.feature_extraction.text import TfidfVectorizer
import numpy as np
from collections import defaultdict
import operator

def getSentsAsDocs(filePath):
    return [x.strip() for x in open(filePath, 'r')]

'''
Returns top-k tuples of (word, count); the 'word' has the tf-idf score in the top 3 in 'count' sentences
The filepath can be made a directory consisting of files by changing arguments to TfidfVectorizer
'''
def analyze(filePath, k):
    docs = getSentsAsDocs(filePath)
    cvec = TfidfVectorizer(stop_words = 'english')
    cvec.fit_transform(docs) #this creates the vocabulary
    revocab = cvec.vocabulary_
    vocab = defaultdict(str)
    countmap = defaultdict(int)
    for word in revocab.keys():
        vocab[int(revocab[word])] = word

    tfidfmat = cvec.transform(docs).todense()
    #print cvec.transform(docs).todense()
    #print vocab
    numrow = tfidfmat.shape[0]
    for i in range(0, numrow):
        currRow = tfidfmat[i].A
        ind = currRow.argsort()[0][::-1][0:3]
        for indd in np.nditer(ind):
            countmap[vocab[int(indd)]] = countmap[vocab[int(indd)]] + 1

    return sorted(countmap.iteritems(), key = operator.itemgetter(1), reverse = True)[0:k]

import sys
if __name__ == '__main__':
    if(len(sys.argv) != 3):
        print 'Usage : python tfidfkeywords.py filename k (the number of keywords needed)'
        sys.exit(0)
    topk = analyze(sys.argv[1], int(sys.argv[2]))
    for (imp_word,count) in topk:
        print imp_word, count
