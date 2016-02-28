package com.kaggle

/**
  * Created by freezing on 2/25/16.
  */
package object model {
  type Title = String
  type Description = String
  type AttributeName = String
  type AttributeValue = String

  case class Id(value: String) extends AnyVal
  case class ProductId(value: String) extends AnyVal
  case class Relevance(value: Double) extends AnyVal
  case class SearchTerm(value: String) extends AnyVal

  case class Attribute(name: AttributeName, value: AttributeValue)
  case class Product(id: ProductId, title: Title)

  trait Data {
    val id: Id
    val product: Product
    val searchTerm: SearchTerm
  }
  case class TestItem(id: Id, product: Product, searchTerm: SearchTerm) extends Data
  case class TrainItem(data: Data, relevance: Relevance)
  case class Evaluation(id: Id, relevance: Relevance)

  case class Token(original: String, normalized: String)
}
