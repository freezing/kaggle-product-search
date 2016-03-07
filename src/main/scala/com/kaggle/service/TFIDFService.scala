package com.kaggle.service

import com.kaggle.model.{CleanTestItem, CleanTrainItem, ProductId}
import com.kaggle.nlp.TFIDF.{TFIDFDocument, TFIDF}

/**
  * Created by freezing on 07/03/16.
  */
class TFIDFService(trainData: List[CleanTrainItem], testData: List[CleanTestItem])(implicit val attributeService: AttributeService, descriptionService: DescriptionService) {

  lazy val documents = makeDocuments
  lazy val tfidf = new TFIDF(documents.values.toList)

  def tfidf(word: String, productId: ProductId): Double = {
    tfidf.tfidf(word, documents(productId))
  }

  private def makeDocuments: Map[ProductId, TFIDFDocument] = {
    (trainData map { item =>
      val id = item.original.rawData.productId
      id -> TFIDFDocument(item.cleanTitle.tokens map { _.stemmedValue } union
        (attributeService.getClean(id) flatMap { _._2.cleanValue map { _.stemmedValue } }).toList union
        (descriptionService.getClean(id) map { _.stemmedValue }))
    }).toMap
  }
}
