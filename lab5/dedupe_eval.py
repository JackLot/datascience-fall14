#!/usr/bin/python

import csv
import pandas as pd

truth = pd.read_csv('product_mapping.csv')
dedupe = pd.read_csv('products_out.csv')[['Cluster ID', 'source', 'id']]

dedupe_amazon = dedupe[dedupe['source'] == 'amazon']
dedupe_google = dedupe[dedupe['source'] == 'google']

dedupe_grouped = dedupe_amazon.merge(dedupe_google, on = 'Cluster ID')
dedupe_grouped.rename(columns={'id_x': 'idAmazon', 'id_y': 'idGoogleBase'}, inplace=True)

matches = dedupe_grouped.merge(truth, on=['idAmazon', 'idGoogleBase'])

num_correct_matches = len(matches)

precision = float(num_correct_matches) / len(dedupe_grouped)
recall = float(num_correct_matches) / len(truth)

print "Precision: " + str(precision) + ", Recall: " + str(recall)