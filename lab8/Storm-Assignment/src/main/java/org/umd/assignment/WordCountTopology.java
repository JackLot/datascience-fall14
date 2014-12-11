/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.umd.assignment;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.ShellBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.umd.assignment.spout.RandomSentenceSpout;
import org.umd.assignment.spout.TwitterSampleSpout;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This topology demonstrates Storm's stream groupings and multilang capabilities.
 */
public class WordCountTopology {

  public static class SplitSentence extends ShellBolt implements IRichBolt {

    public SplitSentence() {
      super("python", "splitsentence.py");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
      return null;
    }

  }

  public static class WordCount extends BaseBasicBolt {

    Map<String, Integer> counts = new HashMap<String, Integer>();
    Set<String> stopwords = new HashSet<String>();
    String stops = "a about above across after again against all almost alone along already also although always among an and another any anybody anyone anything anywhere are area areas around as ask asked asking asks at away b back backed backing backs be became because become becomes been before began behind being beings best better between big both but by c came can cannot case cases certain certainly clear clearly come could d did differ different differently do does done down down downed downing downs during e each early either end ended ending ends enough even evenly ever every everybody everyone everything everywhere f face faces fact facts far felt few find finds first for four from full fully further furthered furthering furthers g gave general generally get gets give given gives go going good goods got great greater greatest group grouped grouping groups h had has have having he her here herself high high high higher highest him himself his how however i if important in interest interested interesting interests into is it its itself j just k keep keeps kind knew know known knows l large largely last later latest least less let lets like likely long longer longest m made make making man many may me member members men might more most mostly mr mrs much must my myself n necessary need needed needing needs never new new newer newest next no nobody non noone not nothing now nowhere number numbers o of off often old older oldest on once one only open opened opening opens or order ordered ordering orders other others our out over p part parted parting parts per perhaps place places point pointed pointing points possible present presented presenting presents problem problems put puts q quite r rather really right right room rooms s said same saw say says second seconds see seem seemed seeming seems sees several shall she should show showed showing shows side sides since small smaller smallest so some somebody someone something somewhere state states still still such sure t take taken than that the their them then there therefore these they thing things think thinks this those though thought thoughts three through thus to today together too took toward turn turned turning turns two u under until up upon us use used uses v very w want wanted wanting wants was way ways we well wells went were what when where whether which while who whole whose why will with within without work worked working works would x y year years yet you young younger youngest your yours z";

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {

      
		// ----------------- Task 2	---------------------------------
		//
		//
		//	Modify this code to exclude stop-words from counting.
		//  Stopword list is provided in the lab folder. 
		//
		//
		// ---------------------------------------------------------

		// Read in the words from the stopwords file and add them
    	// to this hashset for fast lookup later
    	if(stopwords.isEmpty()){

    		StringTokenizer st = new StringTokenizer(stops);
 
			while (st.hasMoreElements()) {
				stopwords.add(st.nextElement().toString());
			}

			System.out.println("-------------------------------------------------------------------------");
			System.out.println(stopwords);
			System.out.println("-------------------------------------------------------------------------");

    	}


    	// Read the tweet words
		String word = tuple.getString(0);

		if(!stopwords.contains(word)){

			Integer count = counts.get(word);

			if (count == null)
				count = 0;

			count++;
			counts.put(word, count);
			collector.emit(new Values(word, count));

		}

    }

	@Override
	public void cleanup()
	{
		// ------------------------  Task 3 ---------------------------------------
		//
		//
		//	This function gets called when the Stream processing finishes.
		//	MODIFY this function to print the most frequent words that co-occur 
		//	with Obama [The TwitterSimpleSpout already gives you Tweets that contain
		//  the word obama].
		//
		//	Since multiple threads will be doing the same cleanup operation, writing the
		//	output to a file might not work as desired. One way to do this would be
		//  print the output (using System.out.println) and do a grep/awk/sed on that.
		//  For a simple example see inside the runStorm.sh.
		//
		//--------------------------------------------------------------------------

		//Put all elements in a TreeMap to be sorted. I didn't to change the counts hashmap to a treemap
		//becauseI wasn't sure how the cleanup method split up the data structure for use by multiple threads

		Set<Entry<String, Integer>> set = counts.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);

        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }

        } );
		
		System.out.println("-------------------------------------------------------------------------");
		System.out.println("CLEANUP");

		int i = 0;

		for(Map.Entry<String, Integer> entry : list){
			if(i == 10){
				break;
			}
            System.out.println(entry.getKey() + ", " + entry.getValue());
            i++;
        }
		
		System.out.println("-------------------------------------------------------------------------");

	}

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word", "count"));
    }
  }

  public static void main(String[] args) throws Exception {

    TopologyBuilder builder = new TopologyBuilder();

	// ---------------------------- Task 1 -------------------------------------
	//
	//		You need to use TwitterSampleSpout() for the assignemt. But, it won't work
	//		unless you set up the access token correctly in the TwitterSampleSpout.java
	//
	//		RandomSentenceSpout() simply spits out a random sentence. 
	//
	//--------------------------------------------------------------------------

	// Setting up a spout
    //builder.setSpout("spout", new RandomSentenceSpout(), 3); 
    builder.setSpout("spout", new TwitterSampleSpout(), 3);

	// Setting up bolts
    builder.setBolt("split", new SplitSentence(), 3).shuffleGrouping("spout");
    builder.setBolt("count", new WordCount(), 3).fieldsGrouping("split", new Fields("word"));

    Config conf = new Config();
    conf.setDebug(true);


    if (args != null && args.length > 0) {
      conf.setNumWorkers(3);

      StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
    }
    else {
      conf.setMaxTaskParallelism(3);

      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("word-count", conf, builder.createTopology());

	  // --------------------------- Task 4 ---------------------------------
	  //
	  //	The sleep time simply indicates for how long you want to keep your
	  //	system up and running. 10000 (miliseconds) here means 10 seconds.
	  // 	
	  //
	  // ----------------------------------------------------------------------

      int TEN_MINUTES = 600000;

      Thread.sleep(TEN_MINUTES);

      cluster.shutdown(); // blot "cleanup" function is called when cluster is shutdown (only works in local mode)
    }
  }
}
