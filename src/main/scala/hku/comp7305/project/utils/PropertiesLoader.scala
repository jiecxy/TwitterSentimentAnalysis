package hku.comp7305.project.utils

import java.io.FileInputStream
import java.util.Properties


object PropertiesLoader {
//  private val conf: Config = ConfigFactory.load("application.conf")
//
//  val sentiment140TrainingFilePath = conf.getString("SENTIMENT140_TRAIN_DATA_ABSOLUTE_PATH")
//  val sentiment140TestingFilePath = conf.getString("SENTIMENT140_TEST_DATA_ABSOLUTE_PATH")
//  val nltkStopWords = conf.getString("NLTK_STOPWORDS_FILE_NAME ")
//
//  val naiveBayesModelPath = conf.getString("NAIVEBAYES_MODEL_ABSOLUTE_PATH")
//  val modelAccuracyPath = conf.getString("NAIVEBAYES_MODEL_ACCURACY_ABSOLUTE_PATH ")
//
//  val tweetsRawPath = conf.getString("TWEETS_RAW_ABSOLUTE_PATH")
//  val saveRawTweets = conf.getBoolean("SAVE_RAW_TWEETS")
//
//  val tweetsClassifiedPath = conf.getString("TWEETS_CLASSIFIED_ABSOLUTE_PATH")
//
//  val consumerKey = conf.getString("CONSUMER_KEY")
//  val consumerSecret = conf.getString("CONSUMER_SECRET")
//  val accessToken = conf.getString("ACCESS_TOKEN_KEY")
//  val accessTokenSecret = conf.getString("ACCESS_TOKEN_SECRET")
//
//  val microBatchTimeInSeconds = conf.getInt("STREAMING_MICRO_BATCH_TIME_IN_SECONDS")
//  val totalRunTimeInMinutes = conf.getInt("TOTAL_RUN_TIME_IN_MINUTES")
  private val props = new Properties()
  props.load(new FileInputStream("application.conf"))
//  val masterURL =
  val sentiment140TrainingFilePath = props.getProperty("SENTIMENT140_TRAIN_DATA_ABSOLUTE_PATH")
  val sentiment140TestingFilePath = props.getProperty("SENTIMENT140_TEST_DATA_ABSOLUTE_PATH")
  val naiveBayesModelPath = props.getProperty("NAIVEBAYES_MODEL_ABSOLUTE_PATH")
  val nltkStopWords = props.getProperty("NLTK_STOPWORDS_FILE_NAME")
}
