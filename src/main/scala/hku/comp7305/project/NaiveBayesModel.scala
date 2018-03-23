package hku.comp7305.project

import org.apache.spark.SparkContext

class NaiveBayesModel {
    /**
      * Loads the Sentiment140 file from the specified path using SparkContext.
      *
      * @param sc                   -- Spark Context.
      * @param sentiment140FilePath -- Absolute file path of Sentiment140.
      * @return -- Spark DataFrame of the Sentiment file with the tweet text and its polarity.
      */
    def loadSentiment140File(sc: SparkContext, sentiment140FilePath: String) = {
//        val sqlContext = SQLContextSingleton.getInstance(sc)
//        val tweetsDF = sqlContext.read
//          .format("com.databricks.spark.csv")
//          .option("header", "false")
//          .option("inferSchema", "true")
//          .load(sentiment140FilePath)
//          .toDF("polarity", "id", "date", "query", "user", "status")
//
//        // Drop the columns we are not interested in.
//        tweetsDF.drop("id").drop("date").drop("query").drop("user")
    }
}
