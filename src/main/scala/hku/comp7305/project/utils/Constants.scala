package hku.comp7305.project.utils

import scala.collection.mutable

/*
cat map.txt | awk '{if(NF==3) {print "geoMap += (\""$1" "$2"\" -> \""$NF"\")"} else {print "geoMap += (\""$1"\" -> \""$NF"\")"}}'
 */

object Constants {
  var geoMap : Map[String, String] = Map()
  geoMap += ("New York" -> "40.7127753000,-74.0059728000")
  geoMap += ("Los Angeles" -> "34.0522342000,-118.2436849000")
  geoMap += ("Chicago" -> "41.8781136000,-87.6297982000")
  geoMap += ("Houston" -> "29.7604267000,-95.3698028000")
  geoMap += ("Phoenix" -> "33.4483771000,-112.0740373000")
  geoMap += ("Philadelphia" -> "39.9525839000,-75.1652215000")
  geoMap += ("San Antonio" -> "29.4241219000,-98.4936282000")
  geoMap += ("San Diego" -> "32.7157380000,-117.1610838000")
  geoMap += ("Dallas" -> "32.7766642000,-96.7969879000")
  geoMap += ("San Jose" -> "37.3382082000,-121.8863286000")
  geoMap += ("Austin" -> "30.2671530000,-97.7430608000")
  geoMap += ("Jacksonville" -> "30.3321838000,-81.6556510000")
  geoMap += ("San Francisco" -> "37.7749295000,-122.4194155000")
  geoMap += ("Columbus" -> "39.9611755000,-82.9987942000")
  geoMap += ("Indianapolis" -> "39.7684030000,-86.1580680000")
  geoMap += ("Fort Worth" -> "32.7554883000,-97.3307658000")
  geoMap += ("Charlotte" -> "35.2270869000,-80.8431267000")
  geoMap += ("Seattle" -> "47.6062095000,-122.3320708000")
  geoMap += ("Denver" -> "39.7392358000,-104.9902510000")
  geoMap += ("El Paso" -> "31.7618778000,-106.4850217000")
  geoMap += ("Washington" -> "47.7510741000,-120.7401385000")
  geoMap += ("Boston" -> "42.3600825000,-71.0588801000")
  geoMap += ("Detroit" -> "42.3314270000,-83.0457538000")
  geoMap += ("Nashville" -> "36.1626638000,-86.7816016000")
  geoMap += ("Memphis" -> "35.1495343000,-90.0489801000")
  geoMap += ("Portland" -> "45.5230622000,-122.6764815000")
  geoMap += ("Oklahoma City" -> "35.4675602000,-97.5164276000")
  geoMap += ("Las Vegas" -> "36.1699412000,-115.1398296000")
  geoMap += ("Louisville" -> "38.2526647000,-85.7584557000")
  geoMap += ("Baltimore" -> "39.2903848000,-76.6121893000")
  geoMap += ("Milwaukee" -> "43.0389025000,-87.9064736000")
  geoMap += ("Albuquerque" -> "35.0843859000,-106.6504220000")
  geoMap += ("Tucson" -> "32.2226066000,-110.9747108000")
  geoMap += ("Fresno" -> "36.7377981000,-119.7871247000")
  geoMap += ("Sacramento" -> "38.5815719000,-121.4943996000")
  geoMap += ("Mesa" -> "33.4151843000,-111.8314724000")
  geoMap += ("Kansas City" -> "39.0997265000,-94.5785667000")
  geoMap += ("Atlanta" -> "33.7489954000,-84.3879824000")
  geoMap += ("Long Beach" -> "33.7700504000,-118.1937395000")
  geoMap += ("Colorado Springs" -> "38.8338816000,-104.8213634000")
  geoMap += ("Raleigh" -> "35.7795897000,-78.6381787000")
  geoMap += ("Miami" -> "25.7616798000,-80.1917902000")
  geoMap += ("Virginia Beach" -> "36.8529263000,-75.9779850000")
  geoMap += ("Omaha" -> "41.2565369000,-95.9345034000")
  geoMap += ("Oakland" -> "37.8043637000,-122.2711137000")
  geoMap += ("Minneapolis" -> "44.9777530000,-93.2650108000")
  geoMap += ("Tulsa" -> "36.1539816000,-95.9927750000")
  geoMap += ("Arlington" -> "38.8816208000,-77.0909809000")
  geoMap += ("New Orleans" -> "29.9510658000,-90.0715323000")
  geoMap += ("Wichita" -> "37.6871761000,-97.3300530000")
}
