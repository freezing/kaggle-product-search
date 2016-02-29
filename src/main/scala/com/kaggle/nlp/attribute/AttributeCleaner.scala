package com.kaggle.nlp.attribute

import java.util.logging.Logger

import com.kaggle.model.{CleanAttribute, RawAttribute, AttributeName}
import com.kaggle.nlp.DataCleaner

/**
  * Created by freezing on 29/02/16.
  */
class AttributeCleaner {
  private val logger = Logger.getLogger(getClass.getName)

  def processName(name: String): AttributeName = {
    val lower = name.toLowerCase
    val clean = {
      if (lower.contains("brand")) BRAND
      else if (lower.contains("material")) MATERIAL
      else UNKNOWN
    }
    AttributeName(name, clean)
  }

  def processAttribute(attr: RawAttribute): CleanAttribute = {
    val cleanValue = DataCleaner.process(attr.value)
    CleanAttribute(attr.productId, attr.name, attr.value, cleanValue)
  }

  def processAttributes(attributes: List[RawAttribute]): Map[CleanAttributeName, CleanAttribute] = (attributes map { x =>
    val cleanAttribute = processAttribute(x)
    cleanAttribute.name.clean -> cleanAttribute
  }).toMap
}

object AttributeCleaner extends AttributeCleaner