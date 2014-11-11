#!/usr/bin/python
from lxml import html
import requests

# Pull Reddit "front-page" source code
#page = requests.get('http://www.reddit.com?limit=1').text
page = open('reddit_test.html', 'r').read()

# Store the HTML in tree form so we can easily use it
tree = html.fromstring(page)
#tree = html.fromstring('''
	#<div class="content"><div id="siteTable">
#<div class=" thing id-t3_2lvetf odd  link " onclick="click_thing(this)" data-fullname="t3_2lvetf"><p class="parent"></p><span class="rank">1</span><div class="midcol unvoted"><div class="arrow up login-required" onclick="$(this).vote(r.config.vote_hash, null, event)" role="button" aria-label="upvote" tabindex="0"></div><div class="score dislikes">5668</div><div class="score unvoted">5669</div><div class="score likes">5670</div><div class="arrow down login-required" onclick="$(this).vote(r.config.vote_hash, null, event)" role="button" aria-label="downvote" tabindex="0"></div></div><a class="thumbnail may-blank " href="http://i.imgur.com/ESJDl1w.jpg"><img src="//b.thumbs.redditmedia.com/ptHfLLevGrq8NYoLBzGDsrQeaCRgiv6fNG8mp7wO37U.jpg" width="70" height="70" alt=""></a><div class="entry unvoted"><p class="title"><a class="title may-blank " href="http://i.imgur.com/ESJDl1w.jpg" tabindex="1">In high school my friend got two pictures in the yearbook by pretending to be his own twin brother.</a> <span class="domain">(<a href="/domain/i.imgur.com/">i.imgur.com</a>)</span></p><p class="tagline">submitted <time title="Mon Nov 10 16:49:58 2014 UTC" datetime="2014-11-10T08:49:58-08:00" class="live-timestamp">5 hours ago</time> by <a href="http://www.reddit.com/user/Dr_Martin_V_Nostrand" class="author may-blank id-t2_b6714">Dr_Martin_V_Nostrand</a><span class="userattrs"></span> to <a href="http://www.reddit.com/r/funny/" class="subreddit hover may-blank">/r/funny</a></p><ul class="flat-list buttons"><li class="first"><a href="http://www.reddit.com/r/funny/comments/2lvetf/in_high_school_my_friend_got_two_pictures_in_the/" class="comments may-blank">1216 comments</a></li><li class="share"><span class="share-button toggle" style=""><a class="option active login-required" href="#" tabindex="100">share</a><a class="option " href="#">cancel</a></span></li></ul><div class="expando" style="display: none"><span class="error">loading...</span></div></div><div class="child"></div><div class="clearleft"></div></div>
	#</div></div>
	#''')

# Pull out only the posts
#posts = tree.xpath('//*/div[contains(@class, "content")]/div[contains(@class, "sitetable")]/div[contains(@class, "thing")]')
posts = tree.xpath('//*/div[@id="siteTable"]')[0].xpath('//div[contains(concat(" ", normalize-space(@class), " "), " thing ")]')

#[0].xpath('//div[contains(concat(" ", normalize-space(@class), " "), " thing ")]')

#print tree
print posts

# Extract the information we want from the posts
xpaths = {}
xpaths['rank'] = '//span[contains(@class, "rank")]/text()'
xpaths['title'] = '//p[contains(@class, "title")]/a[contains(@class, "title")]/text()'
xpaths['score'] = '//div[contains(@class, "unvoted")]/div[contains(@class, "score") and contains(@class, "unvoted")]/text()'
xpaths['time'] = '//p[contains(@class, "tagline")]/time[contains(@class, "live-timestamp")]/@title'
xpaths['submitter'] = '//p[contains(@class, "tagline")]/a[contains(@class, "author")]/text()'
xpaths['subreddit'] = '//p[contains(@class, "tagline")]/a[contains(@class, "subreddit")]/text()'
xpaths['comments'] = '//a[contains(@class, "comments")]/text()'
xpaths['link'] = '//p[contains(@class, "title")]/a[contains(@class, "title")]/@href'
xpaths['domain'] = '//p[contains(@class, "title")]/span[contains(@class, "domain")]/a/text()'

#for post in posts:
	#for attribute in xpaths:
		#print post.xpath(xpaths[attribute])

#print page

#//*[@id="siteTable"]/div[1]