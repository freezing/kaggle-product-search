package com.kaggle

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by freezing on 2/25/16.
  */
package object service {
  val DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"

  object SparkFileReader {
    import com.kaggle.sc
    def readTextFile(file: String): RDD[String] = {
      val path = getClass.getResource(file).getFile
      sc.textFile(path.toString)
    }
  }
}
