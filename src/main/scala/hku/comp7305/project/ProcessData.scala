package hku.comp7305.project

import hku.comp7305.project.utils.PropertiesLoader
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

object ProcessData {
  def main(args: Array[String]): Unit = {

  }

  def processBySVM(sc: SparkContext) = {
//      process(sc, PropertiesLoader.TEST_DATA_PATH)

  }

  def process(sc: SparkContext, pathName: String, stopWordsList: Broadcast[List[String]]) = {
    val hadoopConf = sc.hadoopConfiguration
    val hdfs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
    val path = new Path(pathName)

    import org.apache.hadoop.fs.FileUtil
    import org.json4s._
    import org.json4s.jackson.JsonMethods._

    //    case class Tweet(nbr_retweet:Int,
    //                      nbr_favorite:Int,
    //                      user_id:String,
    //                      url:String,
    //                      text:String,
    //                      usernameTweet:String,
    //                      datetime:String,
    //                      is_retweet:Boolean,
    //                      ID:String,
    //                      nbr_reply:Int,
    //                      is_reply:Boolean
    //    )
    //
    //    case class Tweet(text:String)
    //    implicit val formats = Serialization.formats(NoTypeHints)
    //    val str = """{"nbr_retweet": 0,"nbr_favorite": 0,"user_id": "3247178202","url": "/MoAMPmusic/status/980664030061834240","text": "Everyone Wants to be a Lion. \nChances are most don't get the opportunity to be. \n#zoology","usernameTweet": "MoAMPmusic","datetime": "2018-04-02 12:31:39","is_retweet": false,"ID": "980664030061834240","nbr_reply": 0,"is_reply": false},"""
    //    val tweetObj = parse(str).extract[Tweet]
    //    parse(str).values

    /*
    {"nbr_retweet": 0,
    "nbr_favorite": 0,
    "user_id": "3247178202",
    "url": "/MoAMPmusic/status/980664030061834240",
    "text": "Everyone Wants to be a Lion. \nChances are most don't get the opportunity to be. \n#zoology",
    "usernameTweet": "MoAMPmusic",
    "datetime": "2018-04-02 12:31:39",
    "is_retweet": false,
    "ID": "980664030061834240",
    "nbr_reply": 0,
    "is_reply": false},
     */

    import hku.comp7305.project.SVMModelCreator
    val model = SVMModelCreator.loadModel(sc)

    val cityPath = hdfs.listStatus(path)
    val cities = FileUtil.stat2Paths(cityPath)
    for (city <- cities) {
      println("city: " + city.getName)
      val cityName = city.getName
      val genrePath = hdfs.listStatus(city)
      val genres = FileUtil.stat2Paths(genrePath)
      for (genre <- genres) {
        println("genre: " + genre.getName)
        val genreName = genre.getName
        val movies = sc.wholeTextFiles(genre.toString)
        val cleanedMovies = movies.filter(
          x => {
            val pathSplits = x._1.split("/")
            val fileName = pathSplits(pathSplits.length - 1)
            val splits = fileName.split('-')
            splits.length == 3
          }
        )
        val moviesTweets = cleanedMovies.flatMap {
          case (pathName:String, tweets:String) => {
            val pathSplits = pathName.split("/")
            val fileName = pathSplits(pathSplits.length - 1)
            val splits = fileName.split('-')
            val movieName = splits(0).substring(0, splits(0).length - "near".length).replaceAll("_", " ").trim.replaceAll("#", " ").trim
            tweets.split("\n").map(
              t => {
                implicit val formats = DefaultFormats
                val text:String = (parse(t) \ "text").extract[String]

                // ===
                val sentiment = SVMModelCreator.predict(model, text, stopWordsList)
                // ===

                (cityName, genreName, movieName, text, sentiment)
                // TODO
              }
            )
          }
        }
//        println(moviesTweets.collect().toList.toString())
      }
    }
  }

  def extractInfoFromFileName(path: String): String = {
    //     hdfs://student10:9000/test/Chicago/drama/#Zoology_near-"Chicago"_since-2016
    //    val fileName = new Path(path).getName
    val pathSplits = path.split("/")
    val fileName = pathSplits(pathSplits.length - 1)
    val splits = fileName.split('-')
    if (splits.length != 3) {
      println("ERROR: invalid file " + fileName)
      null
    } else {
      val movieName = splits(0).substring(0, splits(0).length - "near".length).replaceAll("_", " ").trim.replaceAll("#", " ").trim
      //      val sinceYear = splits(2).toInt
      movieName
    }
  }
}
