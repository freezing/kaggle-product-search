package com.kaggle.feature.extraction

import java.util.logging.Logger

import com.kaggle.feature.{TestFeature, TrainFeature}
import com.kaggle.ml.Feature
import com.kaggle.model.{ProductId, CleanTestItem, CleanTrainItem, RawData}
import com.kaggle.nlp.CleanToken
import com.kaggle.service.{DescriptionService, AttributeService}
import org.apache.spark.rdd.RDD

/**
  * Created by freezing on 2/25/16.
  */
class SimpleFeatureExtractor(implicit val attributeService: AttributeService, descriptionService: DescriptionService) extends Serializable {
  private val logger = Logger.getLogger(getClass.getName)
  import com.kaggle.nlp.attribute._

  def extract(item: RawData, cleanTitle: List[CleanToken], cleanSearchTerm: List[CleanToken]): Feature = {
    val cleanTitleSet = (cleanTitle map { _.stemmedValue }).toSet

    val titleMatchCount = calcMatchCount(cleanTitleSet, cleanSearchTerm)

    val jaccard = titleMatchCount.toDouble / (cleanTitleSet.size + cleanSearchTerm.size)
    val queryMatch = titleMatchCount.toDouble / cleanSearchTerm.size
    val searchInTitleContainCounts = containCounts(cleanSearchTerm, cleanTitle).toDouble / cleanSearchTerm.length
    val titleInSearchContainCounts = containCounts(cleanTitle, cleanSearchTerm).toDouble / cleanSearchTerm.length
 //   val brandFeature = extractBrandFeature(item.productId, cleanSearchTerm)

    Feature(List(jaccard, queryMatch, searchInTitleContainCounts, titleInSearchContainCounts))
  }

  private def containCounts(a: List[CleanToken], b: List[CleanToken]): Int = {
    a count { case CleanToken(_, s, _, _) => (b collectFirst { case CleanToken(_, w, _, _) => w.contains(s) }).isDefined }
  }

  private def extractBrandFeature(productId: ProductId, cleanSearchTerm: List[CleanToken]): Double = {
    val attr = attributeService.getClean(productId)
    attr.get(BRAND) match {
      case None => 0.0
      case Some(value) =>
        // TODO: CleanToken should say if word is BRAND which can be used to compare the brand, and if is wrong to return -1
        calcMatchCount(value.cleanValue, cleanSearchTerm).toDouble / value.cleanValue.length
    }
  }

  private def calcMatchCount(a: List[CleanToken], b: List[CleanToken]): Int = {
    val bSet = b.toSet
    a count { x => bSet contains x }
  }

  private def calcMatchCount(titleSet: Set[String], searchTerm: List[CleanToken]): Double = {
    searchTerm count { token => titleSet.contains(token.stemmedValue) }
  }

  def processTrainData(data: List[CleanTrainItem]): List[TrainFeature] = {
    logger.info(s"Feature extraction for train data...")
    val features = data map { item =>
      val feature = extract(item.original.rawData, item.cleanTitle, item.cleanSearchTerm)
      TrainFeature(feature, item.original.relevance, item.original.rawData.id)
    }
    logger.info("Feature extraction for train data finished.")
    features
  }

  def processTestData(data: List[CleanTestItem]): List[TestFeature] = {
    logger.info(s"Feature extraction for test data...")
    val features = data map { item =>
      val feature = extract(item.original, item.cleanTitle, item.cleanSearchTerm)
      TestFeature(feature, item.original.id)
    }
    logger.info(s"Feature extraction for test data finished.")
    features
  }

  def processTrainDataSpark(data: RDD[CleanTrainItem]): RDD[TrainFeature] = {
    data map { item =>
      val feature = extract(item.original.rawData, item.cleanTitle, item.cleanSearchTerm)
      TrainFeature(feature, item.original.relevance, item.original.rawData.id)
    }
  }

  def processTestDataSpark(data: RDD[CleanTestItem]): RDD[TestFeature] = {
    data map { item =>
      val feature = extract(item.original, item.cleanTitle, item.cleanSearchTerm)
      TestFeature(feature, item.original.id)
    }
  }
}

object SimpleFeatureExtractor extends SimpleFeatureExtractor