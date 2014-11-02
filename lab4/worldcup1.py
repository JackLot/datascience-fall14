#!/usr/bin/python
import re

# Function definitions
def ignoreline(line):
	return re.compile("!.*$|^[|][0-9][0-9]*[|][|].*$|^[|]}$|^[|]-$").match(line)

def checkmatch(string, regex):
	return re.compile(regex).match(string)

def getregex(string):
	return re.compile(string)

# Parsing code
f = open('worldcup.txt', 'r')

countryregex = ".*fb[|]([A-Z]{3})"
nomedalregex = "^[|]align=center[|]{{sort dash}}$"
country = ""
countryindex = 0
i = 1

for line in f:
	if not ignoreline(line):
		if checkmatch(line, countryregex):
			country = getregex(countryregex).findall(line)[0]
			countryindex = i
		else:
			if not checkmatch(line, nomedalregex):
				place = i - countryindex
				years = re.compile("([0-9]{4})]]")
				allyears = years.findall(line)
				for y in allyears:
					print country + ", " + y + ", " + str(place)
		i = i + 1