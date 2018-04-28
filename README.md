# TwitterSentimentAnalysis
COMP7305 Group02

### Installation
1) Package jar

Under project path:

```shell
mvn clean package
```

2) Install HDFS, Spark and ElasticSearch cluster


### Execution
1) Train Model

```shell
/opt/spark-2.2.1-bin-hadoop2.7/bin/spark-submit \
--class hku.comp7305.project.TrainModel \
--master yarn \
--deploy-mode client \
--files application.conf \
--executor-memory 2g \
--executor-cores 1 \
--driver-memory 2g \
--num-executors 11 \
original-TwitterSentimentAnalysis-1.0.jar
```

2) Process Data

```
/opt/spark-2.2.1-bin-hadoop2.7/bin/spark-submit \
--class hku.comp7305.project.ProcessData \
--master yarn \
--deploy-mode client \
--files application.conf \
--jars ~/elasticsearch-hadoop-6.2.3/dist/elasticsearch-spark-20_2.11-6.2.3.jar \
--conf spark.es.nodes=student10-x2,student10-x1,student9-x1,student9-x2 \
--conf spark.es.port=9200 \
--executor-memory 2g \
--executor-cores 1 \
--driver-memory 2g \
--num-executors 11 \
original-TwitterSentimentAnalysis-1.0.jar
```