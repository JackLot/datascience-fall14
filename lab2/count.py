#!/usr/bin/python

import avro.schema
import pandas as pd
from avro.datafile import DataFileReader
from avro.io import DatumReader

reader = DataFileReader(open("countries.avro", "r"), DatumReader())

countries = pd.DataFrame.from_records(reader)
print len(countries[countries.population > 10000000].index)

