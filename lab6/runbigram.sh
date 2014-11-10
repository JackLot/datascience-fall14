#!/bin/bash
cd ~/hadoop_apps/
cd bigram_classes/
rm org -r
cd ../
javac -classpath ${HADOOP_HOME}/share/hadoop/common/hadoop-common-2.3.0-cdh5.1.2.jar:${HADOOP_HOME}/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.3.0-cdh5.1.2.jar -d bigram_classes BigramCount.java
jar cvf bigramcount.jar -C bigram_classes org
hadoop fs -rm -f -r bigramcount/output
hadoop fs -rm -f -r bigramcount/tmp
hadoop jar bigramcount.jar org.myorg.BigramCount bigramcount/input bigramcount/output
