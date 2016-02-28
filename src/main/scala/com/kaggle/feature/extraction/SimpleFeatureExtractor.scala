package com.kaggle.feature.extraction

import com.kaggle.feature.Feature
import com.kaggle.model.{Data, TestItem}
import com.kaggle.nlp.Preprocessor
import com.kaggle.service.AttributeService

/**
  * Created by freezing on 2/25/16.
  */
object SimpleFeatureExtractor {
  def extract(data: Data): Feature = {
    val item = new Preprocessor(data).preprocess()

    val titleWords = item.product.title.split(" ")
    val words = item.searchTerm.value.split(" ")

    val cnt = words count { x => titleWords.contains(x) }
    val jaccard = cnt / (titleWords.length + words.length)
    val queryMatch = cnt / words.length.toDouble

    val attrs = AttributeService.attributes(item.product.id.value)
    val attrCnt = attrs count { attr => (words collect { case w if attr.value.toLowerCase().contains(w.toLowerCase()) => w }).length > 0 }
    Feature(List(cnt, jaccard, queryMatch, attrCnt, 1))
  }
}
