package com.kaggle.parser

import com.kaggle.model._
import com.kaggle.nlp.DataCleaner
import com.kaggle.nlp.attribute.AttributeCleaner
import com.kaggle.service._

/**
  * Created by freezing on 28/02/16.
  */
object CsvParser {
  def parseTrainData(line: String): TrainItem = {
    val cols = line.split(DELIMITER) map cleanQuotes
    val rawData = new RawData {
      override val searchTerm: RawSearchTerm = RawSearchTerm(cols(3))
      override val title: RawTitle = cols(2)
      override val productId: ProductId = ProductId(cols(1))
      override val id: Id = Id(cols.head)
    }
    TrainItem(rawData, Relevance(cols(4).toDouble))
  }

  def parseTestData(line: String): TestItem = {
    val cols = line.split(DELIMITER) map cleanQuotes
    val id = Id(cols.head)
    val productId = ProductId(cols(1))
    val title = cols(2)
    val searchTerm = RawSearchTerm(cols(3))
    TestItem(id, productId, title, searchTerm)
  }

  def parseAttribute(line: String): RawAttribute = {
    val cols = line.split(DELIMITER) map cleanQuotes
    val productId = ProductId(cols.head)
    val attributeName = AttributeCleaner.processName(cols(1))
    val attributeValue = cols(2)
    RawAttribute(productId, attributeName, attributeValue)
  }

  private def cleanQuotes(value: String): String = {
    //if (value.startsWith("\"") && value.endsWith("\"") value
    value.replace("\"", "")
  }
}
