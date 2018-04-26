package hku.comp7305.project.utils

import org.apache.spark.SparkContext


/*
cat map.txt | awk '{if(NF==3) {print "geoMap += (\""$1" "$2"\" -> \""$NF"\")"} else {print "geoMap += (\""$1"\" -> \""$NF"\")"}}'
 */

object GeoMapper {
  val DEFAULT_GEO = "41.8781136000,-87.6297982000"

  def loadCityGeoMap(sc: SparkContext, cityGeoMap: String): scala.collection.Map[String, String] = {
    sc.textFile(cityGeoMap).filter(
      line => {
        !line.isEmpty && !line.trim.startsWith("#") && line.contains("=")
      }
    ).map(line => line.split("=")).filter(f => f.length == 2).map(s => (s(0).trim.toLowerCase, s(1).trim)).collectAsMap
  }
}
