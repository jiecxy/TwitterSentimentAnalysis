package hku.comp7305.project

import hku.comp7305.project.utils.PropertiesLoader
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.SparkSession


/**
 * java -cp target/TwitterSentimentAnalysis-1.0.jar hku.comp7305.project.App
 *
 */
object App {
    def main(args: Array[String]): Unit = {
        println( "Hello World!" )
//      file:///Users/jiecxy/Desktop/Project/TwitterSentimentAnalysis/spark-warehouse/
//        val conf = new SparkConf()
//          .setAppName(this.getClass.getSimpleName)
//          .setMaster("spark://localhost")
//          .set("spark.serializer", classOf[KryoSerializer].getCanonicalName)
//        val sc = SparkContext.getOrCreate(conf)
        val spark = SparkSession.builder
                        .appName("Simple Application")
                        .master("local")
                        .getOrCreate()
        NaiveBayesModel.loadSentiment140File(spark, PropertiesLoader.sentiment140TestingFilePath)
    }
}
