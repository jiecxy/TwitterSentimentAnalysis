package hku.comp7305.project

import hku.comp7305.project.utils.{PropertiesLoader, SQLContextSingleton}
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.mllib.linalg.Vector

object NaiveBayesModel {

  val hashingTF = new HashingTF()

  def createAndSaveModel(sc: SparkContext, stopWordsList: Broadcast[List[String]]): Unit = {
    val tweetsDF: DataFrame = loadSentiment140File(sc, PropertiesLoader.sentiment140TrainingFilePath)

    val labeledRDD = tweetsDF.select("polarity", "status").rdd.map {
      case Row(polarity: Int, tweet: String) =>
        val tweetInWords: Seq[String] = getCleanedTweetText(tweet, stopWordsList.value)
        LabeledPoint(polarity, textToFeatureVector(tweetInWords))
    }
    labeledRDD.cache()

    val naiveBayesModel: NaiveBayesModel = NaiveBayes.train(labeledRDD, lambda = 1.0, modelType = "multinomial")
    naiveBayesModel.save(sc, PropertiesLoader.naiveBayesModelPath)
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
}
