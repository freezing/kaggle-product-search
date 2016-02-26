package com.kaggle.service

import com.kaggle.model._

/**
  * Created by freezing on 2/25/16.
  */
object TestItemService {
  lazy val testItems = {
    val lines = CsvReader.readFile("/test.csv")
    val data = lines takeRight (lines.length - 1)
    (data map { line =>
      val cols = line.split(DELIMITER)
      val id = Id(cols.head)
      val productId = ProductId(cols(1))
      val title = cols(2)
      val searchTerm = SearchTerm(cols(3))
      val product = Product(productId, title)
      id -> TestItem(id, product, searchTerm)
    }).toMap
  }
}
