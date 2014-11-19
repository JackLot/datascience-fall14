#!/usr/bin/python
from lxml import html
import urllib2
import time
import datetime

# Get the time and date of request (TOR) and date of request (DOR)
ts = time.time()
DOR = datetime.datetime.fromtimestamp(ts).strftime('%m_%d_%Y')
timestamp = datetime.datetime.fromtimestamp(ts).strftime('%m_%d_%Y %H:%M:%S')

# Pull Reddit "front-page" source code
page = open('redditsource.html', 'r').read()
#page = urllib2.urlopen("http://www.reddit.com?limit=25").read()

# Store the HTML in lxml tree form so we can easily use it
tree = html.fromstring(page)

# Get a list of HtmlElements where each elements is the post DIV
posts = tree.xpath('//*/div[@id="siteTable"]/div[contains(@class, "thing")]')

# Create XPATH strings for extracting information we want from the post DIVs
xpaths = {}
xpaths['rank'] = './/span[contains(@class, "rank")]/text()'
#xpaths['title'] = './/p[contains(@class, "title")]/a[contains(@class, "title")]/text()'
xpaths['score'] = './/div[contains(@class, "unvoted")]/div[contains(@class, "score") and contains(@class, "unvoted")]/text()'
xpaths['posttime'] = './/p[contains(@class, "tagline")]/time[contains(@class, "live-timestamp")]/@title'
xpaths['submitter'] = './/p[contains(@class, "tagline")]/a[contains(@class, "author")]/text()'
xpaths['subreddit'] = './/p[contains(@class, "tagline")]/a[contains(@class, "subreddit")]/text()'
xpaths['comments'] = './/a[contains(@class, "comments")]/text()'
xpaths['link'] = './/p[contains(@class, "title")]/a[contains(@class, "title")]/@href'
xpaths['domain'] = './/p[contains(@class, "title")]/span[contains(@class, "domain")]/a/text()'

# Open the CSV file to be written
fo = open("redditFP-" + DOR + ".csv", "w+")

# Write the CSV column headers
fo.write(", ".join(xpaths) + ", timestamp" + "\n")

# Print out comma-separated post information
for post in posts:

	attrlist = []

	for attribute in xpaths:
		attrlist.append(post.xpath(xpaths[attribute])[0])

	fo.write(", ".join(attrlist) + ", " + timestamp + "\n")

# Close the file
fo.close()