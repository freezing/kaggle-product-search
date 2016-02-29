package com.kaggle.service

import com.kaggle.model.{Description, ProductId}

/**
  * Created by freezing on 2/25/16.
  */
class DescriptionService extends Serializable {
  private lazy val descriptionMap: Map[ProductId, Description] = {
    val lines = CsvReader.readTextFile("/product_descriptions.csv")
    (lines map { line =>
      val cols = line.split(DELIMITER)
      ProductId(cols.head) -> cols.last
    }).toMap
  }

  def get(productId: ProductId): Description = descriptionMap(productId)
}
