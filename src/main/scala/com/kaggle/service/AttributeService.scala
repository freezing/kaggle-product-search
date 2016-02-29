package com.kaggle.service

import java.util.logging.Logger

import com.kaggle.model.{CleanAttribute, ProductId, RawAttribute}
import com.kaggle.nlp.attribute.{CleanAttributeName, AttributeCleaner}
import com.kaggle.parser.CsvParser

/**
  * Created by freezing on 2/25/16.
  */
class AttributeService extends Serializable {
  private val logger = Logger.getLogger(getClass.getName)

  private lazy val idAttributesMap: Map[ProductId, List[RawAttribute]] = {
    logger.info("Reading attributes from file...")
    val lines = CsvReader.readTextFile("/attributes.csv")
    val data = lines filter { line => line.split(DELIMITER).length == 3}
    val attributes = data map { line =>
      val rawAttribute = CsvParser.parseAttribute(line)
      (rawAttribute.productId, rawAttribute)
    } groupBy { case (productId, _) => productId } map { case (k, v) =>  k -> (v map { case (id, attr) => attr }) } withDefaultValue List.empty[RawAttribute]
    logger.info("Reading attributes has finished.")
    attributes
  }

  def get(productId: ProductId): List[RawAttribute] = idAttributesMap(productId)

  def getClean(productId: ProductId): Map[CleanAttributeName, CleanAttribute] = cleanAttributeMap(productId)

  lazy val cleanAttributeMap: Map[ProductId, Map[CleanAttributeName, CleanAttribute]] = {
    logger.info("Cleaning attributes")
    val cleanMap = idAttributesMap map { case (k, v) => k -> AttributeCleaner.processAttributes(v) }
    logger.info("Cleaning attributes finished.")
    cleanMap withDefaultValue Map.empty[CleanAttributeName, CleanAttribute]
  }
}
