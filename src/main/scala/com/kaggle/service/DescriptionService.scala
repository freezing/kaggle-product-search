package com.kaggle.service

import java.util.logging.Logger

import com.kaggle.model.{Description, ProductId}
import com.kaggle.nlp.{DataCleaner, CleanToken}

/**
  * Created by freezing on 2/25/16.
  */
class DescriptionService extends Serializable {
  private val logger = Logger.getLogger(getClass.getName)

  private lazy val descriptionMap: Map[ProductId, Description] = {
    logger.info("Reading descriptions from file...")
    val lines = CsvReader.readTextFile("/product_descriptions.csv")
    val descriptions = (lines map { line =>
      val cols = line.split(DELIMITER)
      ProductId(cols.head) -> cols.last
    }).toMap
    logger.info("Reading descriptions has finished.")
    descriptions
  }

  private lazy val cleanDescriptions: Map[ProductId, List[CleanToken]] = {
    descriptionMap map { case (k, v) => k -> DataCleaner.process(v) }
  }

  def get(productId: ProductId): Description = descriptionMap(productId)

  def getClean(productId: ProductId): List[CleanToken] = cleanDescriptions(productId)

  def getAllRaw = descriptionMap
}
