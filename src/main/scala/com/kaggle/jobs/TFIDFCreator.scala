package com.kaggle.jobs

import java.nio.file.Paths
import java.util.logging.Logger

import com.kaggle.file.TfidfFileCreator
import com.kaggle.model.ProductId
import com.kaggle.nlp.DataCleaner
import com.kaggle.nlp.TFIDF.{TFIDFDocument, TFIDF}
import com.kaggle.service.{DescriptionService, AttributeService, CsvReader}

object TFIDFCreator extends App {
  val logger = Logger.getLogger(getClass.getName)

  val outputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--outputPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No stem output path specified") }

  val attributeService = new AttributeService
  val descriptionService = new DescriptionService

  // 1. Read data
  val trainData = CsvReader.readTrainData("/train.csv")
  val testData = CsvReader.readTestData("/test.csv")

  // 2. Clean data
  val cleanTrainData = DataCleaner.processTrainData(trainData)
  val cleanTestData = DataCleaner.processTestData(testData)

  val documents = makeDocuments
  val tfidf = new TFIDF(documents.values.toList)

  // For each search
  val tfidfs = (cleanTrainData flatMap { item =>
    val id = item.original.rawData.productId
    item.cleanSearchTerm.tokens map { t => (id, t.stemmedValue) -> tfidf.tfidf(t.stemmedValue, documents(id)) }
  }).toMap

  // Save to file
  new TfidfFileCreator(tfidfs).save(outputPath)

  private def makeDocuments: Map[ProductId, TFIDFDocument] = {
    (cleanTrainData map { item =>
      val id = item.original.rawData.productId
      id -> TFIDFDocument(item.cleanTitle.tokens map { _.stemmedValue } union
        (attributeService.getClean(id) flatMap { _._2.cleanValue map { _.stemmedValue } }).toList union
        (descriptionService.getClean(id) map { _.stemmedValue }))
    }).toMap
    // TODO: Add union of test data
  }
}
