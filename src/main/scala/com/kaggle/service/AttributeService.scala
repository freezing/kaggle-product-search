package com.kaggle.service

import com.kaggle.model.Attribute

/**
  * Created by freezing on 2/25/16.
  */
object AttributeService {
  lazy val attributes = {
    val lines = CsvReader.readFile("/attributes.csv")
    val data = lines takeRight (lines.length - 1) filter { line => line.split(DELIMITER).length == 3}
    data map { line =>
      val cols = line.split(DELIMITER)
      val productId = cols.head
      val attributeName = cols(1)
      val attributeValue = cols(2)
      productId -> Attribute(attributeName, attributeValue)
    } groupBy { case (productId, _) => productId } map { case (k, v) =>  k -> (v map { case (id, attr) => attr }) } withDefaultValue List.empty[Attribute]
  }
}
