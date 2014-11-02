#!/usr/bin/python
import pandas as pd

worldcup = pd.read_csv('worldcup.csv')
worldcupPivoted = worldcup.pivot(index='country', columns='year', values='place')

print worldcupPivoted