package com.kaggle

import com.kaggle.nlp.CleanToken

/**
  * Created by freezing on 2/25/16.
  */
package object model {
  type RawTitle = String
  type Description = String
  type AttributeName = String
  type AttributeValue = String

  case class Id(value: String) extends AnyVal
  case class ProductId(value: String) extends AnyVal
  case class Relevance(value: Double) extends AnyVal

  case class RawSearchTerm(value: String) extends AnyVal
  case class RawAttribute(productId: ProductId, name: AttributeName, value: AttributeValue)
  case class RawDescription(productId: ProductId, value: Description)

  trait RawData {
    val id: Id
    val productId: ProductId
    val title: RawTitle
    val searchTerm: RawSearchTerm
  }

  case class TestItem(id: Id, productId: ProductId, title: RawTitle, searchTerm: RawSearchTerm) extends RawData
  case class CleanTestItem(original: TestItem, cleanTitle: List[CleanToken], cleanSearchTerm: List[CleanToken])

  case class TrainItem(rawData: RawData, relevance: Relevance)
  case class CleanTrainItem(original: TrainItem, cleanTitle: List[CleanToken], cleanSearchTerm: List[CleanToken])

  case class Evaluation(id: Id, relevance: Relevance)

  case class Token(original: String, normalized: String)
}
