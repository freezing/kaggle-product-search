package com.kaggle.service

import com.kaggle.model.{ProductId, RawAttribute}
import com.kaggle.parser.CsvParser
import org.apache.spark.rdd.{PairRDDFunctions, RDD}

/**
  * Created by freezing on 2/25/16.
  */
class AttributeService extends Serializable {
  private lazy val idAttributesMap: Map[ProductId, List[RawAttribute]] = {
    val lines = CsvReader.readFile("/attributes.csv")
    val data = lines filter { line => line.split(DELIMITER).length == 3}
    data map { line =>
      val rawAttribute = CsvParser.parseAttribute(line)
      (rawAttribute.productId, rawAttribute)
    } groupBy { case (productId, _) => productId } map { case (k, v) =>  k -> (v map { case (id, attr) => attr }) } withDefaultValue List.empty[RawAttribute]
  }

  def get(productId: ProductId): List[RawAttribute] = idAttributesMap(productId)
}
