#!/usr/bin/python
from lxml import html
import urllib2

# Pull Reddit "front-page" source code
page = urllib2.urlopen("http://www.reddit.com?limit=25").read()

print page