#!/usr/bin/python
import re

# Function definitions
def checkmatch(string, regex):
	return re.compile(regex).match(string)

def parseseats(string):
	matches = re.compile("\d+").findall(string)
	return matches[0] + ", " + matches[1] + ", " + matches[2]

def parsetime(string):
	matches = re.compile("[ ]*([A-Za-z]+)[ ]+(.+)").search(string).groups()
	return matches[0] + ", " + matches[1]

def parselocation(string):
	matches = re.compile("[ ]*([A-Z]{3})[ ]+([0-9]+)").search(string).groups()
	return matches[0] + ", " + matches[1]

# Parsing code
f = open('cmsc.txt', 'r')
classname = ""

for line in f:
	if checkmatch(line, "CMSC[0-9]{3}"):
		classname = line.rstrip()
	elif checkmatch(line, "0[0-9]{3}"):
		print("\n"),
		print(classname.rstrip() + ", " + line.rstrip()),
	elif checkmatch(line, "Seats.*"):
		print(", " + parseseats(line)),
	elif checkmatch(line, "[A-Z]{3}[ ]+[0-9]+"):
		print(", " + parselocation(line)),
	elif checkmatch(line, "(M|Tu|W|Th|F)+[ ]+.*"):
		print(", " + parsetime(line)),
	else:
		if line.strip():
			print(", " + line.rstrip()),