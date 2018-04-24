package hku.comp7305.project

import hku.comp7305.project.SVMModelCreator.{createAndSaveModel, loadStopWords, validateAccuracyOfModel}
import hku.comp7305.project.utils.{LogUtil, PropertiesLoader}
import org.apache.spark.sql.SparkSession

/**
 * java -cp target/TwitterSentimentAnalysis-1.0.jar hku.comp7305.project.App
 * java -cp target/TwitterSentimentAnalysis-1.0.jar hku.comp7305.project.NaiveBayesModelCreator
 */
object TrainModel {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
                    .appName("Twitter Movie Reviews Sentiment Analysis (Train Model)")
                    .getOrCreate()
    val sc = spark.sparkContext

    LogUtil.info("Starting training model...")
    LogUtil.info("\nConfiguration:" +
      "\t" + "SENTIMENT140_TRAIN_DATA_PATH: " + PropertiesLoader.SENTIMENT140_TRAIN_DATA_PATH +
      "\t" + "SENTIMENT140_TEST_DATA_PATH: " + PropertiesLoader.SENTIMENT140_TEST_DATA_PATH +
      "\t" + "NLTK_STOPWORDS_PATH: " + PropertiesLoader.NLTK_STOPWORDS_PATH +
      "\t" + "MODEL_ITERATION_NUM: " + PropertiesLoader.MODEL_ITERATION_NUM +
      "\t" + "MODEL_PATH: " + PropertiesLoader.MODEL_PATH + "\n")
    val stopWordsList = sc.broadcast(loadStopWords(sc, PropertiesLoader.NLTK_STOPWORDS_PATH))
    createAndSaveModel(sc, stopWordsList, PropertiesLoader.MODEL_ITERATION_NUM.toInt)
    LogUtil.info("Validating  model...")
    validateAccuracyOfModel(sc, stopWordsList)
    LogUtil.info("Finished training model...")
  }
}
