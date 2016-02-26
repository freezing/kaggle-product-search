package com.kaggle.service

/**
  * Created by freezing on 2/25/16.
  */
object DescriptionService {
  lazy val description = {
    val lines = CsvReader.readFile("/product_descriptions.csv")
    val data = lines takeRight (lines.length - 1)
    (data map { line =>
      val cols = line.split(DELIMITER)
      cols.head -> cols.tail
    }).toMap
  }
}
