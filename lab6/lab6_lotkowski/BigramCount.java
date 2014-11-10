package org.myorg;

	import java.io.IOException;
	import java.util.*;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
    import org.apache.hadoop.mapred.jobcontrol.*;
	
	public class BigramCount {
	
        public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

        	//Local variables
            private final static IntWritable one = new IntWritable(1);
            private Text word = new Text();

            public void map(LongWritable key, Text value, OutputCollector output, Reporter reporter) throws IOException 
            {
                String line = value.toString();
                StringTokenizer tokenizer = new StringTokenizer(line);

                String nextWord = "";
                String currWord = "";
                int position = 0;

                while (tokenizer.hasMoreTokens()) {

                	if(position == 0){

                		currWord = tokenizer.nextToken();

                		//Make sure that the line doesn't only have 
                		//one word (which would break nextToken() method)
                		if(!tokenizer.hasMoreTokens()){
                			break;
                		}

                	}else{
                		currWord = nextWord;
                	}

                	nextWord = tokenizer.nextToken();

                    word.set(currWord + "," + nextWord);
                    output.collect(word, one);

                    position++;
                }
            }
        }
	
        public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
            public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException 
            {
                int sum = 0;

                while (values.hasNext()) {

                    sum += values.next().get();

                }

                output.collect(key, new IntWritable(sum));
            }
        }

        public static class WordToBigramMap extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

            private Text word1 = new Text();
            private Text word2 = new Text();
            private Text bigramWithCount = new Text();

            public void map(LongWritable key, Text value, OutputCollector output, Reporter reporter) throws IOException 
            {
                String[] bigrams = value.toString().split(",");
                bigramWithCount.set(value.toString());

                //Map each bigram into two lines, one with the key of the first word, and one of the second
                output.collect(new Text(bigrams[0]), bigramWithCount);
                output.collect(new Text(bigrams[1].split("\\t")[0]), bigramWithCount);

            }
        }

        public static class WordToBigramReduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
            public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {

                ArrayList<Bigram> instances = new ArrayList<Bigram>();
                String outputList = "";

                while (values.hasNext()) {
                    instances.add(new Bigram(values.next().toString()));
                }

                Collections.sort(instances, new Comparator<Bigram>() {
                    public int compare(Bigram b1, Bigram b2){
                       return b1.occurances - b2.occurances;
                    }
                });

                Collections.reverse(instances);

                int i = 0;
                for(Bigram bi : instances){
                    if(i < 5){
                        outputList = outputList + bi.toString();
                    }else{
                        break;
                    }
                    i++;
                }

                output.collect(key, new Text(outputList));
                
            }

            public static class Bigram{
                int occurances;
                String bigram;
                public Bigram(String bigram){
                    this.bigram = bigram;
                    occurances = Integer.parseInt(bigram.split("\\t")[1]);
                }

                public String toString(){
                    return "(" + bigram + ")";
                }
            }

        }

	
	   public static void main(String[] args) throws Exception {

        String tempDirectory = "bigramcount/tmp";

        // Create first bigram count job
        // ---------------------------------------------------------
        JobConf conf = new JobConf(BigramCount.class);
        
        conf.setJobName("bigramcount");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(tempDirectory));

        JobClient.runJob(conf);


        // Create word --> bigram, count mapper job
        // ----------------------------------------------------------
        JobConf conf2 = new JobConf(BigramCount.class);
        conf2.setJobName("wordtobigramcount");

        conf2.setOutputKeyClass(Text.class);
        conf2.setOutputValueClass(Text.class);

        conf2.setMapperClass(WordToBigramMap.class);
        //conf2.setCombinerClass(WordToBigramReduce.class);
        conf2.setReducerClass(WordToBigramReduce.class);

        conf2.setInputFormat(TextInputFormat.class);
        conf2.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf2, new Path(tempDirectory));
        FileOutputFormat.setOutputPath(conf2, new Path(args[1]));

        JobClient.runJob(conf2);

	   }
	}
	
