package hku.comp7305.project

import java.io.FileInputStream

import hku.comp7305.project.utils.{PropertiesLoader, SQLContextSingleton}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.mllib.linalg.Vector

import scala.io.Source

object NaiveBayesModelCreator {

  final val TRAIN_FILE_FIELD_0 = 1

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Simple Application")
      .master("local")
      .getOrCreate()
    val sc = spark.sparkContext
    val stopWordsList = sc.broadcast(loadStopWords(PropertiesLoader.nltkStopWords))
    createAndSaveModel(spark, stopWordsList)
    validateAccuracyOfModel(spark, stopWordsList)
  }

  val hashingTF = new HashingTF()

  def createAndSaveModel(sc: SparkSession, stopWordsList: Broadcast[List[String]]): Unit = {
    val tweetsDF: DataFrame = loadSentiment140File(sc, PropertiesLoader.sentiment140TrainingFilePath)
    val labeledRDD = tweetsDF.select("polarity", "text").rdd.map {
      case Row(polarity: Int, tweet: String) =>
        val tweetInWords: Seq[String] = getCleanedTweetText(tweet, stopWordsList.value)
        LabeledPoint(polarity, textToFeatureVector(tweetInWords))
    }
    labeledRDD.cache()
    val naiveBayesModel: NaiveBayesModel = NaiveBayes.train(labeledRDD, lambda = 1.0, modelType = "multinomial")
    naiveBayesModel.save(sc.sparkContext, PropertiesLoader.naiveBayesModelPath)
  }

  def replaceNewLines(tweet: String) = {
    tweet.replaceAll("\n", "")
  }

  def validateAccuracyOfModel(sc: SparkSession, stopWordsList: Broadcast[List[String]]): Unit = {
    val naiveBayesModel: NaiveBayesModel = NaiveBayesModel.load(sc.sparkContext, PropertiesLoader.naiveBayesModelPath)

    val tweetsDF: DataFrame = loadSentiment140File(sc, PropertiesLoader.sentiment140TestingFilePath)
    val actualVsPredictionRDD = tweetsDF.select("polarity", "text").rdd.map {
      case Row(polarity: Int, tweet: String) =>
        val tweetText = replaceNewLines(tweet)
        val tweetInWords: Seq[String] = getCleanedTweetText(tweetText, stopWordsList.value)
        (polarity.toDouble,
          naiveBayesModel.predict(textToFeatureVector(tweetInWords)),
          tweetText)
    }
    val accuracy = 100.0 * actualVsPredictionRDD.filter(x => x._1 == x._2).count() / tweetsDF.count()
    /*actualVsPredictionRDD.cache()
    val predictedCorrect = actualVsPredictionRDD.filter(x => x._1 == x._2).count()
    val predictedInCorrect = actualVsPredictionRDD.filter(x => x._1 != x._2).count()
    val accuracy = 100.0 * predictedCorrect.toDouble / (predictedCorrect + predictedInCorrect).toDouble*/
    println(f"""\n\t<==******** Prediction accuracy compared to actual: $accuracy%.2f%% ********==>\n""")
//    saveAccuracy(sc, actualVsPredictionRDD)
  }

  def loadSentiment140File(sc: SparkSession, sentiment140FilePath: String): DataFrame = {
    val sqlContext = SQLContextSingleton.getInstance(sc)
    val tweetsDF = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "false")
      .option("inferSchema", "true")
      .load(sentiment140FilePath)
      .toDF("polarity", "id", "date", "query", "user", "text")
    // Drop the columns we are not interested in.
    tweetsDF.drop("id").drop("date").drop("query").drop("user")

    // TODO cached
    tweetsDF.cache()
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

  def loadStopWords(stopWordsFileName: String): List[String] = {
//    Source.fromInputStream(getClass.getResourceAsStream(stopWordsFileName)).getLines().toList
    Source.fromInputStream(new FileInputStream(stopWordsFileName)).getLines().toList
  }
}
