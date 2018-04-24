package hku.comp7305.project

import hku.comp7305.project.SVMModelCreator.{createAndSaveModel, loadStopWords, validateAccuracyOfModel}
import hku.comp7305.project.utils.{LogUtil, PropertiesLoader}
import org.apache.spark.sql.SparkSession

/**
 * java -cp target/TwitterSentimentAnalysis-1.0.jar hku.comp7305.project.TrainModel
 */
object TrainModel {
  def main(args: Array[String]): Unit = {

    println("\nConfiguration:" + "\n" +
      "\t" + "SENTIMENT140_TRAIN_DATA_PATH = " + PropertiesLoader.SENTIMENT140_TRAIN_DATA_PATH + "\n" +
      "\t" + "SENTIMENT140_TEST_DATA_PATH = " + PropertiesLoader.SENTIMENT140_TEST_DATA_PATH + "\n" +
      "\t" + "NLTK_STOPWORDS_PATH = " + PropertiesLoader.NLTK_STOPWORDS_PATH + "\n" +
      "\t" + "MODEL_ITERATION_NUM = " + PropertiesLoader.MODEL_ITERATION_NUM + "\n" +
      "\t" + "MODEL_PATH = " + PropertiesLoader.MODEL_PATH + "\n")

    val spark = SparkSession.builder
                    .appName("Twitter Movie Reviews Sentiment Analysis (Train Model)")
                    .getOrCreate()
    val sc = spark.sparkContext
    LogUtil.info("Starting training model...")
    val stopWordsList = sc.broadcast(loadStopWords(sc, PropertiesLoader.NLTK_STOPWORDS_PATH))
    createAndSaveModel(sc, stopWordsList, PropertiesLoader.MODEL_ITERATION_NUM)
    LogUtil.info("Validating  model...")
    validateAccuracyOfModel(sc, stopWordsList)
    LogUtil.info("Finished training model...")
  }
}
