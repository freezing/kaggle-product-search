package com.kaggle.service

import com.kaggle.model.ProductId

/**
  * Created by freezing on 07/03/16.
  */
class TFIDFService(implicit val attributeService: AttributeService, descriptionService: DescriptionService) {

  lazy val tfidfs: Map[(ProductId, String), Double] = {
    (CsvReader.readTextFile("/tfidf.csv") map { line =>
      val cols = line.split(",")
      val id = ProductId(cols.head.substring("ProductId(".length, cols.head.length - 1))
      val tfidf = cols(2).toDouble
      (id, cols(1)) -> tfidf
    }).toMap
  }

  def tfidf(word: String, productId: ProductId): Double = tfidfs((productId, word))
}
