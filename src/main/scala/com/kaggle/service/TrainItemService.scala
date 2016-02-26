package com.kaggle.service

import com.kaggle.model._

/**
  * Created by freezing on 2/25/16.
  */
object TrainItemService {
  lazy val trainItems = {
    val lines = CsvReader.readFile("/train.csv")
    val data = lines takeRight (lines.length - 1)
    (data map { line =>
      val cols = line.split(DELIMITER)
      val id = Id(cols.head)
      val productId = ProductId(cols(1))
      val title = cols(2)
      val relevance = Relevance(cols(4).toDouble)

      val data = new Data {
        override val id: Id = Id(cols.head)
        override val product: Product = Product(productId, title)
        override val searchTerm: SearchTerm = SearchTerm(cols(3))
      }
      id -> TrainItem(data, relevance)
    }).toMap
  }
}
