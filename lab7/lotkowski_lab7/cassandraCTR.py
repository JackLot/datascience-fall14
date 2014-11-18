#!/usr/bin/env python

import logging

log = logging.getLogger()
#log.setLevel('DEBUG')
log.setLevel('WARN')
handler = logging.StreamHandler()
handler.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s"))
log.addHandler(handler)

from cassandra import ConsistencyLevel
from cassandra.cluster import Cluster
from cassandra.query import SimpleStatement

KEYSPACE = "ctrpython"


def main():
    cluster = Cluster(['127.0.0.1'])
    session = cluster.connect()

    log.info("creating keyspace...")
    session.execute("""
        CREATE KEYSPACE IF NOT EXISTS %s
        WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '2' }
        """ % KEYSPACE)

    log.info("setting keyspace...")
    session.set_keyspace(KEYSPACE)

    log.info("creating table...")
    session.execute("""
        CREATE TABLE IF NOT EXISTS clickimpressions (
            OwnerId int,
            AdId int,
            numImpressions int,
            numClicks int,
            PRIMARY KEY (OwnerId, AdId)
        )
        """)
    values = [ 
        [1,1,10,1], [1,2,5,0], [1,3,20,1], [1,4,15,0],
        [2,1,10,0], [2,2,55,0], [2,3,13,0], [2,4,21,0],
        [3,1,32,1], [3,2,23,0], [3,3,44,2], [3,4,36,1]
        ]

    prepared = session.prepare("""
        INSERT INTO clickimpressions (OwnerId, AdId, numImpressions, numClicks)
        VALUES (?, ?, ?, ?)
        """)

    for vals in values:
        session.execute(prepared, (vals[0], vals[1], vals[2], vals[3]))


    print "-----------------------------( 1 )------------------------------"

    future = session.execute_async("SELECT * FROM clickimpressions")

    try:
        rows = future.result()
    except Exception:
        log.exeception()

    print ("OwnerId, AdId, CTR")
    for row in rows:
        ctr = float(row[2])/float(row[3])
        print row[0], row[1], ctr

    print "-----------------------------------------------------------"

    print "-----------------------------( 2 )------------------------------"

    future = session.execute_async("SELECT * FROM clickimpressions")

    try:
        rows = future.result()
    except Exception:
        log.exeception()


    clicks = {}
    impressions = {}

    print ("OwnerId, CTR")

    for row in rows:

        OwnerId = row[0]
        AdId = row[1]
        numImpressions = row[3]
        numClicks = row[2]

        if OwnerId in clicks.keys():
            clicks[OwnerId] = clicks[OwnerId] + numClicks
            impressions[OwnerId] = impressions[OwnerId] + numImpressions
        else:
            clicks[OwnerId] = numClicks
            impressions[OwnerId] = numImpressions

    for key in clicks:
        print key, float(clicks[key])/float(impressions[key])

    print "-----------------------------------------------------------"

    print "-----------------------------( 3 )------------------------------"

    future = session.execute_async("SELECT * FROM clickimpressions WHERE OwnerId = 1 AND AdId = 3")

    try:
        rows = future.result()
    except Exception:
        log.exeception()

    print ("OwnerId, AdId, CTR")
    for row in rows:
        ctr = float(row[2])/float(row[3])
        print row[0], row[1], ctr

    print "-----------------------------------------------------------"

    print "-----------------------------( 4 )------------------------------"

    future = session.execute_async("SELECT * FROM clickimpressions WHERE OwnerId = 2")

    try:
        rows = future.result()
    except Exception:
        log.exeception()

    clicks = 0
    impressions = 0

    print ("OwnerId, CTR")

    for row in rows:

        numImpressions = row[3]
        numClicks = row[2]

        clicks = clicks + numClicks
        impressions = impressions + numImpressions

    print "2", float(clicks)/float(impressions)

    print "-----------------------------------------------------------"


    session.execute("DROP KEYSPACE " + KEYSPACE)

if __name__ == "__main__":
    main()