package com.kaggle.feature.extraction

import com.kaggle.feature.{TestFeature, TrainFeature, Feature}
import com.kaggle.model.{CleanTestItem, CleanTrainItem, RawData}
import com.kaggle.nlp.CleanToken
import com.kaggle.service.{DescriptionService, AttributeService}
import org.apache.spark.rdd.RDD

/**
  * Created by freezing on 2/25/16.
  */
class SimpleFeatureExtractor(implicit val attributeService: AttributeService, descriptionService: DescriptionService) {
  def extract(item: RawData, cleanTitle: List[CleanToken], cleanSearchTerm: List[CleanToken]): Feature = {
    val titleWords = item.title.split(" ")
    val words = item.searchTerm.value.split(" ")

    val cnt = words count { x => titleWords.contains(x) }
    val jaccard = cnt / (titleWords.length + words.length)
    val queryMatch = cnt / words.length.toDouble

    val attrs = attributeService.get(item.productId)
    val attrCnt = attrs count { attr => (words collect { case w if attr.value.toLowerCase().contains(w.toLowerCase()) => w }).length > 0 }
    Feature(List(cnt, jaccard, queryMatch, attrCnt, 1))
  }

  def processTrainData(data: RDD[CleanTrainItem]): RDD[TrainFeature] = {
    data map { item =>
      val feature = extract(item.original.rawData, item.cleanTitle, item.cleanSearchTerm)
      TrainFeature(feature, item.original.relevance, item.original.rawData.id)
    }
  }

  def processTestData(data: RDD[CleanTestItem]): RDD[TestFeature] = {
    data map { item =>
      val feature = extract(item.original, item.cleanTitle, item.cleanSearchTerm)
      TestFeature(feature, item.original.id)
    }
  }
}

object SimpleFeatureExtractor extends SimpleFeatureExtractor