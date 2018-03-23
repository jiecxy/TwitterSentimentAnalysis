package hku.comp7305.project.utils

import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

object SQLContextSingleton {

  @transient
  @volatile private var instance: SQLContext = _

  def getInstance(sparkContext: SparkContext): SQLContext = {
    if (instance == null) {
      synchronized {
        if (instance == null) {
          //TODO             .master("local[*]")
          val spark: SparkSession = SparkSession.builder
            .appName("Spark Application")
            .getOrCreate
//          instance = SQLContext.getOrCreate(sparkContext)
          instance = spark.sqlContext
        }
      }
    }
    instance
  }
}
