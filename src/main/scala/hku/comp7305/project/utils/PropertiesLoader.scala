package hku.comp7305.project.utils

import java.io.FileInputStream
import java.util.Properties


object PropertiesLoader {

  val CONFIG_FILE_PATH = "application.conf"
  val DEFAULT_SENTIMENT140_TRAIN_DATA_PATH = "hdfs:///tsa/trainingandtestdata/training.1600000.processed.noemoticon.csv"
  val DEFAULT_SENTIMENT140_TEST_DATA_PATH = "hdfs:///tsa/trainingandtestdata/testdata.manual.2009.06.14.csv"
  val DEFAULT_MODEL_PATH = "hdfs:///tsa/model"
  val DEFAULT_NLTK_STOPWORDS_PATH = "hdfs:///tsa/trainingandtestdata/NLTK_English_Stopwords_Corpus.txt"
  val DEFAULT_TEST_DATA_PATH = "hdfs:///data"
  val DEFAULT_MODEL_ITERATION_NUM = "2000"
  val DEFAULT_MIN_PARTITIONS = "30"
  val DEFAULT_ES_RESOURCE = "tweets/tweet"

  private val props = new Properties()
  try {
    props.load(new FileInputStream(CONFIG_FILE_PATH))
  } catch {
    case e:java.lang.Exception =>
      println("ERROR: Failed to load configuration file: " + CONFIG_FILE_PATH)
      e.printStackTrace()
      System.exit(-1)
  }

  val SENTIMENT140_TRAIN_DATA_PATH = props.getProperty("SENTIMENT140_TRAIN_DATA_PATH", DEFAULT_SENTIMENT140_TRAIN_DATA_PATH).trim
  val SENTIMENT140_TEST_DATA_PATH = props.getProperty("SENTIMENT140_TEST_DATA_PATH", DEFAULT_SENTIMENT140_TEST_DATA_PATH).trim
  val MODEL_PATH = props.getProperty("MODEL_PATH", DEFAULT_MODEL_PATH).trim
  val MODEL_ITERATION_NUM = Integer.parseInt(props.getProperty("MODEL_ITERATION_NUM", DEFAULT_MODEL_ITERATION_NUM).trim)
  val MIN_PARTITIONS = Integer.parseInt(props.getProperty("MIN_PARTITIONS", DEFAULT_MIN_PARTITIONS).trim)
  val NLTK_STOPWORDS_PATH = props.getProperty("NLTK_STOPWORDS_PATH", DEFAULT_NLTK_STOPWORDS_PATH).trim
  val TEST_DATA_PATH = props.getProperty("TEST_DATA_PATH", DEFAULT_TEST_DATA_PATH).trim
  val ES_RESOURCE = props.getProperty("ES_RESOURCE", DEFAULT_ES_RESOURCE).trim
}
