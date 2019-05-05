#! /bin/bash

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

dataset=$1
ontology=$2
output=$3
jar="$root/summarization-spark/target/abstat-spark-0.0.1-SNAPSHOT.jar"
ont_name=$(echo $ontology | sed 's/.*\///')
echo $jar
echo "files: $ontology"
echo "dataset: $dataset"
echo "output: $output"

cmd="spark2-submit --class it.unimib.disco.abstat.distributed.application.Summarization --master yarn --deploy-mode cluster --files $files $jar yarn-cluster  $dataset $ont_name $output"
eval $cmd

