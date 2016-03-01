package com.kaggle.service

import java.util.logging.Logger

import com.kaggle.model.{Description, ProductId}

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

  def get(productId: ProductId): Description = descriptionMap(productId)

  def getAllRaw = descriptionMap
}
