package hku.comp7305.project

import hku.comp7305.project.utils.{LogUtil, PropertiesLoader, SQLContextSingleton}
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.mllib.linalg.Vector

object TrainNaiveBayesModel {

  def main(args: Array[String]): Unit = {
    println("\nConfiguration:" + "\n" +
      "\t" + "SENTIMENT140_TRAIN_DATA_PATH = " + PropertiesLoader.SENTIMENT140_TRAIN_DATA_PATH + "\n" +
      "\t" + "SENTIMENT140_TEST_DATA_PATH = " + PropertiesLoader.SENTIMENT140_TEST_DATA_PATH + "\n" +
      "\t" + "NLTK_STOPWORDS_PATH = " + PropertiesLoader.NLTK_STOPWORDS_PATH + "\n" +
      "\t" + "MODEL_ITERATION_NUM = " + PropertiesLoader.MODEL_ITERATION_NUM + "\n" +
      "\t" + "MODEL_PATH = " + PropertiesLoader.MODEL_PATH + "\n")

    val spark = SparkSession.builder
      .appName("Twitter Movie Reviews Sentiment Analysis (Train Model - NaiveBayes )")
      .getOrCreate()
    val sc = spark.sparkContext
    LogUtil.info("Starting training model...")
    val stopWordsList = sc.broadcast(loadStopWords(sc, PropertiesLoader.NLTK_STOPWORDS_PATH))
    createAndSaveModel(sc, stopWordsList, PropertiesLoader.MODEL_ITERATION_NUM)
    LogUtil.info("Validating  model...")
    validateAccuracyOfModel(sc, stopWordsList)
    LogUtil.info("Finished training model...")
  }

  val hashingTF = new HashingTF()

  def createAndSaveModel(sc: SparkContext, stopWordsList: Broadcast[List[String]], iterations: Int): Unit = {
    LogUtil.info("Loading training file...")
    val tweetsDF: DataFrame = loadSentiment140File(sc, PropertiesLoader.SENTIMENT140_TRAIN_DATA_PATH)
    val labeledRDD = tweetsDF.select("polarity", "text").rdd.map {
      case Row(polarity: Int, tweet: String) =>
        val tweetInWords: Seq[String] = getCleanedTweetText(tweet, stopWordsList.value)
        LabeledPoint(polarity, textToFeatureVector(tweetInWords))
    }
    LogUtil.info("Starting training model...")
    val model: NaiveBayesModel = NaiveBayes.train(labeledRDD, lambda = 0.1, modelType = "multinomial")
    LogUtil.info("Training model finished!")
    LogUtil.info("Saving model...")
    checkModelSavePath(sc, PropertiesLoader.MODEL_PATH)
    model.save(sc, PropertiesLoader.MODEL_PATH)
    LogUtil.info("Saving model finished!")
  }

  def checkModelSavePath(sc: SparkContext, pathName: String): Unit = {
    val hadoopConf = sc.hadoopConfiguration
    val hdfs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
    val path = new Path(pathName)
    if (hdfs.isDirectory(path)) {
      hdfs.delete(path, true)
    }
  }

  def replaceNewLines(tweet: String) = {
    tweet.replaceAll("\n", "")
  }

  def validateAccuracyOfModel(sc: SparkContext, stopWordsList: Broadcast[List[String]]): Unit = {
    val model: NaiveBayesModel = NaiveBayesModel.load(sc, PropertiesLoader.MODEL_PATH)
    val tweetsDF: DataFrame = loadSentiment140File(sc, PropertiesLoader.SENTIMENT140_TEST_DATA_PATH)
    val actualVsPredictionRDD = tweetsDF.select("polarity", "text").rdd.map {
      case Row(polarity: Int, tweet: String) =>
        val tweetText = replaceNewLines(tweet)
        val tweetInWords: Seq[String] = getCleanedTweetText(tweetText, stopWordsList.value)
        (polarity.toDouble,
          model.predict(textToFeatureVector(tweetInWords)),
          tweetText)
    }
    val accuracy = 100.0 * actualVsPredictionRDD.filter(x => x._1 == x._2).count() / tweetsDF.count()
    println(f"""\n\t<==******** NaiveBayes: Prediction accuracy compared to actual: $accuracy%.2f%% ********==>\n""")
  }

  def loadSentiment140File(sc: SparkContext, sentiment140FilePath: String): DataFrame = {
    val sqlContext = SQLContextSingleton.getInstance(sc)
    val tweetsDF = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .option("inferSchema", "true")
      .load(sentiment140FilePath)
      .toDF("polarity", "id", "date", "query", "user", "text")
    // Drop the columns we are not interested in.
    tweetsDF.drop("id").drop("date").drop("query").drop("user")
  }

  def getCleanedTweetText(tweetText: String, stopWordsList: List[String]): Seq[String] = {
    //Remove URLs, RT, MT and other redundant chars / strings from the tweets.
    tweetText.toLowerCase()
      .replaceAll("\n", "")
      .replaceAll("rt\\s+", "")
      .replaceAll("\\s+@\\w+", "")
      .replaceAll("@\\w+", "")
      .replaceAll("\\s+#\\w+", "")
      .replaceAll("#\\w+", "")
      .replaceAll("(?:https?|http?)://[\\w/%.-]+", "")
      .replaceAll("(?:https?|http?)://[\\w/%.-]+\\s+", "")
      .replaceAll("(?:https?|http?)//[\\w/%.-]+\\s+", "")
      .replaceAll("(?:https?|http?)//[\\w/%.-]+", "")
      .split("\\W+")
      .filter(_.matches("^[a-zA-Z]+$"))
      .filter(!stopWordsList.contains(_))
  }

  def textToFeatureVector(tweetText: Seq[String]): Vector = {
    hashingTF.transform(tweetText)
  }

  def loadStopWords(sc: SparkContext, stopWordsFileName: String): List[String] = {
    sc.textFile(stopWordsFileName).collect().toList
  }
}
