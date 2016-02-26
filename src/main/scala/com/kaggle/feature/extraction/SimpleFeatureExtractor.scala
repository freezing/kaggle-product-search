package com.kaggle.feature.extraction

import com.kaggle.feature.Feature
import com.kaggle.model.{Data, TestItem}

/**
  * Created by freezing on 2/25/16.
  */
object SimpleFeatureExtractor {
  def extract(item: Data): Feature = {
    val titleWords = item.product.title.split(" ")
    val words = item.searchTerm.value.split(" ")

    val cnt = words count { x => titleWords.contains(x) }
    Feature(List(cnt))
  }
}
