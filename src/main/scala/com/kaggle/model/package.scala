package com.kaggle

import com.kaggle.nlp.{TokenType, SemanticType, CleanToken}
import com.kaggle.nlp.attribute.CleanAttributeName

/**
  * Created by freezing on 2/25/16.
  */
package object model {
  type RawTitle = String
  type Description = String
  type AttributeValue = String

  case class AttributeName(original: String, clean: CleanAttributeName)
  case class Id(value: String) extends AnyVal
  case class ProductId(value: String) extends AnyVal
  case class Relevance(value: Double) extends AnyVal

  case class RawSearchTerm(value: String) extends AnyVal
  case class RawAttribute(productId: ProductId, name: AttributeName, value: AttributeValue)
  case class RawDescription(productId: ProductId, value: Description)

  case class CleanAttribute(productId: ProductId, name: AttributeName, value: AttributeValue, cleanValue: List[CleanToken])

  trait RawData {
    val id: Id
    val productId: ProductId
    val title: RawTitle
    val searchTerm: RawSearchTerm

    override def toString = s"RawData($id, $productId, $title, $searchTerm)"
  }

  case class TestItem(id: Id, productId: ProductId, title: RawTitle, searchTerm: RawSearchTerm) extends RawData
  case class CleanTestItem(original: TestItem, cleanTitle: CleanTerm, cleanSearchTerm: CleanTerm)

  case class CleanTerm(tokens: List[CleanToken], attributes: Map[SemanticType, List[CleanToken]])

  case class TrainItem(rawData: RawData, relevance: Relevance)
  case class CleanTrainItem(original: TrainItem, cleanTitle: CleanTerm, cleanSearchTerm: CleanTerm)

  case class Evaluation(id: Id, relevance: Relevance)
}
