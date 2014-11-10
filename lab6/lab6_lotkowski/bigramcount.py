from pyspark import SparkContext, SparkConf

conf = SparkConf().setAppName("WordCount").setMaster("local")

sc = SparkContext(conf=conf)

textFile = sc.textFile("bible+shakes.nopunc")


# Iterates over the list of words and creates bigram groupings
def createBigrams(tokens):
    bigrams = []

    for i in range(0, len(tokens)-1):
        bigrams.append(((tokens[i], tokens[i+1]), 1))

    return bigrams

# Returns the sort param used by the sorted() method
def getKeyForSorting(bigramWithCount):
    return bigramWithCount[1]


def sortAndDisplay(matches):
    biAppear = []

    # tuples[1] = (bigram, count) in the form of an iterator
    for match in matches[1]:
        biAppear.append(match)

    # Now that the matching bigrams are in a list, sort them using the occurance 
    # and only return the first 5
    return (matches[0], sorted(biAppear, key=getKeyForSorting, reverse=True)[0:5])


bigramcounts = textFile \
	.map(lambda x: x.split(" ")) \
    .flatMap(createBigrams) \
    .reduceByKey(lambda x, y: x + y) \
    .flatMap(lambda x: [(x[0][0], (x[0],x[1])), (x[0][1], (x[0],x[1]))]) \
    .groupByKey() \
    .map(sortAndDisplay)

bigramcounts.saveAsTextFile("output")