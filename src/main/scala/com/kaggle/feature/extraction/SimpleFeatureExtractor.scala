package com.kaggle.feature.extraction

import java.util.logging.Logger

import com.kaggle.feature.{TestFeature, TrainFeature}
import com.kaggle.ml.{LinearRegressionFeature, Feature}
import com.kaggle.ml.decisiontree.{DecisionTreeFeatures, DecisionTreeFeature}
import com.kaggle.model._
import com.kaggle.nlp.{SemanticType, CleanToken}
import com.kaggle.service.{DescriptionService, AttributeService}
import org.apache.spark.rdd.RDD

/**
  * Created by freezing on 2/25/16.
  */
class SimpleFeatureExtractor(implicit val attributeService: AttributeService, descriptionService: DescriptionService) extends Serializable {
  private val logger = Logger.getLogger(getClass.getName)
  import com.kaggle.nlp.attribute._

  def extract(item: RawData, cleanTitle: CleanTerm, cleanSearchTerm: CleanTerm): Feature = {
    val allWords = getAllWords(item.productId, cleanTitle.tokens, cleanSearchTerm.tokens)

    val cleanTitleSet = (cleanTitle.tokens map { _.stemmedValue }).toSet

    val titleMatchCount = calcMatchCount(cleanTitleSet, cleanSearchTerm.tokens)

    val jaccard = tryDivide(titleMatchCount.toDouble, cleanTitleSet.size + cleanSearchTerm.tokens.size)
    val queryMatch = tryDivide(titleMatchCount.toDouble, cleanSearchTerm.tokens.size)
    val searchInTitleContained = tryDivide(containCounts(cleanSearchTerm.tokens, cleanTitle.tokens).toDouble, cleanSearchTerm.tokens.length)
    val titleInSearchContained = tryDivide(containCounts(cleanTitle.tokens, cleanSearchTerm.tokens).toDouble, cleanSearchTerm.tokens.length)
    val abbreviationMatches = tryDivide(abbreviationCounts(cleanSearchTerm.tokens, cleanTitle.tokens).toDouble, cleanSearchTerm.tokens.length)
    val searchTermCountAgainstAllWords = tryDivide(calcMatchCount(allWords map { _.stemmedValue }, cleanSearchTerm.tokens), cleanSearchTerm.tokens.length)

    val searchDimensionAttributes = getDimensionAttributes(cleanSearchTerm.attributes)
    val titleDimensionAttributes = getDimensionAttributes(cleanTitle.attributes)

    val dimensionsSpecified = if (searchDimensionAttributes.nonEmpty) 1.0 else 0.0
    val dimensionsFeature = tryDivide(containCounts(searchDimensionAttributes, titleDimensionAttributes), searchDimensionAttributes.length)
    
    val brandFeature = extractBrandFeature(item.productId, cleanSearchTerm.tokens)
    val productTypeMatches = extractProductTypeFeature(item.productId, cleanSearchTerm.tokens)
    val queryMatchDecisionTree = if (queryMatch > 0.1) 1.0 else 0.0
    val cleanSearchTermTier = cleanSearchTerm.tokens.length match {
      case 0 => 0.0
      case 1 | 2 => 1.0
      case _ => 2.0
    }

    // TODO: THIS MUST BE REFACTORED
    Feature(
      LinearRegressionFeature(
      List(jaccard, queryMatch, searchInTitleContained, titleInSearchContained, abbreviationMatches, searchTermCountAgainstAllWords, dimensionsFeature) // Linear Regression features
      ),
      DecisionTreeFeatures(List(
        DecisionTreeFeature(brandFeature),
        DecisionTreeFeature(productTypeMatches),
        // TODO: Add clean search term tier
        //DecisionTreeFeature(queryMatchDecisionTree),
        DecisionTreeFeature(cleanSearchTermTier),
        DecisionTreeFeature(dimensionsSpecified)
      ))  // Decision Tree features
    )
  }

  private def equal(w: String, s: String): Boolean = {
    if (s.length <= 3 || w.length <= 3) w == s
    else {
      // Get all letters (union)
      val wCounts = letterCounts(w)
      val sCounts = letterCounts(s)
      val differenceCount = (wCounts map { case (k, v) => Math.abs(v - sCounts(k)) }).sum
      val similarity = differenceCount.toDouble / Math.max(w.length, s.length)
      similarity > 0.8
    }
  }

  private def letterCounts(s: String): Map[Char, Int] = s groupBy { x => x } map { case (k, v) => k -> v.length } withDefaultValue 0

  private def getDimensionAttributes(attributes: Map[SemanticType, List[CleanToken]]) = (attributes.keys flatMap { attributes(_) }).toList

  private def tryDivide(a: Double, b: Double): Double = if (Math.abs(b) < 1e-4) 0.0 else a / b

  private def getAllWords(productId: ProductId, cleanTitle: List[CleanToken], cleanSearchTerm: List[CleanToken]): Set[CleanToken] = {
    // TODO: ADd attribute name
    (attributeService.getClean(productId) flatMap { case (k, v) => v.cleanValue }).toSet union cleanTitle.toSet union descriptionService.getClean(productId).toSet
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
      (b map { case CleanToken(_, w, _, _) =>
          if (equal(w, s)) 1 else 0
      }).sum
    } count { _ > 0 }
  }

  private def extractBrandFeature(productId: ProductId, cleanSearchTerm: List[CleanToken]): Double = {
    val attr = attributeService.getClean(productId)
    attr.get(BRAND) match {
      case Some(value) if calcMatchCount((value.cleanValue map { _.stemmedValue }).toSet, cleanSearchTerm) > 0 => 1.0
      case _ => 0.0
        // TODO: CleanToken should say if word is BRAND which can be used to compare the brand, and if is wrong to return -1
//        calcMatchCount((value.cleanValue map { _.stemmedValue }).toSet, cleanSearchTerm).toDouble / value.cleanValue.length
    }
  }

  private def extractProductTypeFeature(productId: ProductId, cleanSearchTerm: List[CleanToken]): Double = {
    val attr = attributeService.getClean(productId)
    attr.get(PRODUCT_TYPE) match {
      case Some(value) if calcMatchCount((value.cleanValue map { _.stemmedValue }).toSet, cleanSearchTerm) > 0 => 1.0
      case _ => 0.0
      // TODO: CleanToken should say if word is BRAND which can be used to compare the brand, and if is wrong to return -1
      //        calcMatchCount((value.cleanValue map { _.stemmedValue }).toSet, cleanSearchTerm).toDouble / value.cleanValue.length
    }
  }

  private def calcMatchCount(titleSet: Set[String], searchTerm: List[CleanToken]): Int = {
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