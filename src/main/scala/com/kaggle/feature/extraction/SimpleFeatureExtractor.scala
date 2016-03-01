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
    val searchInTitleContained = containCounts(cleanSearchTerm, cleanTitle).toDouble / cleanSearchTerm.length
    val titleInSearchContained = containCounts(cleanTitle, cleanSearchTerm).toDouble / cleanSearchTerm.length
    val abbreviationMatches = abbreviationCounts(cleanSearchTerm, cleanTitle).toDouble / cleanSearchTerm.length
 //   val brandFeature = extractBrandFeature(item.productId, cleanSearchTerm)

    Feature(List(jaccard, queryMatch, searchInTitleContained, titleInSearchContained, abbreviationMatches))
  }

  private def abbreviationCounts(searchTerm: List[CleanToken], cleanTitle: List[CleanToken]): Int = {
    multiTokenAbbreviationCount(searchTerm, cleanTitle) + singleTokenAbbreviationCount(searchTerm, cleanTitle)
  }

  private def singleTokenAbbreviationCount(searchTerm: List[CleanToken], cleanTitle: List[CleanToken]): Int = {
    // Filter only tokens of length 2 and 3
    searchTerm filter { x => x.stemmedValue.length >= 2 && x.stemmedValue.length <= 3 } count { case CleanToken(_, s, _, _) =>
      existsAbbreviation(s, cleanTitle)
    }
  }

  private def existsAbbreviation(s: String, cleanTitle: List[CleanToken]): Boolean =
    cleanTitle map { _.stemmedValue } sliding s.length map { x => x map { _.charAt(0) } mkString "" } contains s

  private def multiTokenAbbreviationCount(searchTerm: List[CleanToken], cleanTitle: List[CleanToken]): Int = {
    val abbreviations2 = (cleanTitle map { _.stemmedValue.charAt(0) } sliding 2 map { _ mkString "" }).toSet
    val cnt2 = searchTerm map { _.stemmedValue } filter { _.length == 1 } sliding 2 map { _ mkString "" } count { x => abbreviations2.contains(x) }

    val abbreviations3 = (cleanTitle map { _.stemmedValue.charAt(0) } sliding 3 map { _ mkString "" }).toSet
    val cnt3 = searchTerm map { _.stemmedValue } filter { _.length == 1 } sliding 3 map { _ mkString "" } count { x => abbreviations3.contains(x) }

    cnt2 + cnt3
  }

  private def containCounts(a: List[CleanToken], b: List[CleanToken]): Int = {
    a map { case CleanToken (_, s, _, _) =>
//      println("Checking: " + s)
      (b map { case CleanToken(_, w, _, _) =>
//          println("   with: " + w)
          if (w.contains(s)) 1 else 0
      }).sum
    } count { _ > 0 }
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