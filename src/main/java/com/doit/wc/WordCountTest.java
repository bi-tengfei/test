package com.doit.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountTest {

    private static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private Text k2 = new Text();
        private IntWritable v2 = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] words = value.toString().split("\\s+");
            for (String word : words) {
                k2.set(word);
                context.write(k2, v2);
            }
        }
    }

    private static class WordCountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable v3 = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable value : values) {
                count += value.get();
            }
            v3.set(count);
            context.write(key, v3);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        //设置运行模式
        conf.set("mapreduce.framework.name", "yarn");
        //设置ResourceManager位置
        conf.set("yarn.resourcemanager.hostname", "linux03");
        // 设置MapReduce程序运行在windows上的跨平台参数
        conf.set("mapreduce.app-submission.cross-platform", "true");

        Job job = Job.getInstance(conf, "wordCount");
        // 设置主类
        job.setJarByClass(WordCountTest.class);


        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReduce.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        Path inputPath = new Path("/wc/input");
        Path outputPath = new Path("/wc/out");

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        job.waitForCompletion(true);
    }
}
