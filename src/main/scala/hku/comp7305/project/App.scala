package hku.comp7305.project

import hku.comp7305.project.NaiveBayesModelCreator.{createAndSaveModel, loadStopWords, validateAccuracyOfModel}
import hku.comp7305.project.utils.PropertiesLoader
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.SparkSession


/**
 * java -cp target/TwitterSentimentAnalysis-1.0.jar hku.comp7305.project.App
 * java -cp target/TwitterSentimentAnalysis-1.0.jar hku.comp7305.project.NaiveBayesModelCreator
 */
object App {
  def main(args: Array[String]): Unit = {
  //      file:///Users/jiecxy/Desktop/Project/TwitterSentimentAnalysis/spark-warehouse/
  //        val conf = new SparkConf()
  //          .setAppName(this.getClass.getSimpleName)
  //          .setMaster("spark://localhost")
  //          .set("spark.serializer", classOf[KryoSerializer].getCanonicalName)
  //        val sc = SparkContext.getOrCreate(conf)
    val log = Logger.getLogger(getClass)
    if (args.length != 1) {
//      println("Need indicate 'train' or 'show' as argument!")
      log.error("Need indicate 'train' or 'show' as argument!")
      return
    }

    var trainMode = true
    if (args(0).toString.trim.equals("train")) {
      trainMode = true
    }  else if (args(0).toString.trim.equals("show")) {
      log.warn("Not implemented yet!")
      trainMode = false
      return
    } else {
      log.error("Only 'train' or 'show' are valid arguments!")
      return
    }

    val spark = SparkSession.builder
                    .appName("Twitter Movie Reviews Sentiment Analysis v0.1")
                    .getOrCreate()


    // .master("local")
    if (trainMode) {
      log.info("Starting training model...")
      val sc = spark.sparkContext
      val stopWordsList = sc.broadcast(loadStopWords(spark.sparkContext, PropertiesLoader.nltkStopWords))
      createAndSaveModel(spark, stopWordsList)
      log.info("Validating  model...")
      validateAccuracyOfModel(spark, stopWordsList)
    } else {
      log.warn("Not implemented yet!")
      //      println("Not implemented yet!")

    }
  }
}
