package com.kaggle.parser

import com.kaggle.model._
import com.kaggle.service._

/**
  * Created by freezing on 28/02/16.
  */
object CsvParser {
  def parseTrainData(line: String): TrainItem = {
    val cols = line.split(DELIMITER)
    val rawData = new RawData {
      override val searchTerm: RawSearchTerm = RawSearchTerm(cols(3))
      override val title: RawTitle = cols(2)
      override val productId: ProductId = ProductId(cols(1))
      override val id: Id = Id(cols.head)
    }
    TrainItem(rawData, Relevance(cols(4).toDouble))
  }

  def parseTestData(line: String): TestItem = {
    val cols = line.split(DELIMITER)
    val id = Id(cols.head)
    val productId = ProductId(cols(1))
    val title = cols(2)
    val searchTerm = RawSearchTerm(cols(3))
    TestItem(id, productId, title, searchTerm)
  }

  def parseAttribute(line: String): RawAttribute = {
    val cols = line.split(DELIMITER)
    val productId = ProductId(cols.head)
    val attributeName = cols(1)
    val attributeValue = cols(2)
    RawAttribute(productId, attributeName, attributeValue)
  }
}
