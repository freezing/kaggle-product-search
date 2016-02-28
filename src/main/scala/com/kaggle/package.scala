package com

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by freezing on 28/02/16.
  */
package object kaggle {
  implicit val sc = new SparkContext(new SparkConf().setAppName("KaggleProductRelevance").setMaster("local[*]"))
}
