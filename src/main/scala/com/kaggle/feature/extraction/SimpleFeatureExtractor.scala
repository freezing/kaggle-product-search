package com.kaggle.feature.extraction

import java.util.logging.Logger

import com.kaggle.feature.{TestFeature, TrainFeature}
import com.kaggle.ml.{LinearRegressionFeature, Feature}
import com.kaggle.ml.decisiontree.{DecisionTreeFeatures, DecisionTreeFeature}
import com.kaggle.model._
import com.kaggle.nlp.{NlpUtils, SemanticType, CleanToken}
import com.kaggle.service.{TFIDFService, DescriptionService, AttributeService}
import org.apache.spark.rdd.RDD

/**
  * Created by freezing on 2/25/16.
  */
class SimpleFeatureExtractor(implicit val attributeService: AttributeService, descriptionService: DescriptionService, tfidfService: TFIDFService) extends Serializable {
  private val logger = Logger.getLogger(getClass.getName)
  import com.kaggle.nlp.attribute._

  def extract(item: RawData, cleanTitle: CleanTerm, cleanSearchTerm: CleanTerm): Feature = {
    val allWords = getAllWords(item.productId, cleanTitle.tokens, cleanSearchTerm.tokens)

    val titleMatchCount = similarCounts(cleanTitle.tokens, cleanSearchTerm.tokens)

    val jaccard = tryDivide(titleMatchCount.toDouble, cleanTitle.tokens.size + cleanSearchTerm.tokens.size)
    val queryMatch = tryDivide(titleMatchCount.toDouble, cleanSearchTerm.tokens.size)
    val searchInTitleContained = tryDivide(containCounts(cleanSearchTerm.tokens, cleanTitle.tokens).toDouble, cleanSearchTerm.tokens.length)
    val titleInSearchContained = tryDivide(containCounts(cleanTitle.tokens, cleanSearchTerm.tokens).toDouble, cleanSearchTerm.tokens.length)
    val abbreviationMatches = tryDivide(abbreviationCounts(cleanSearchTerm.tokens, cleanTitle.tokens).toDouble, cleanSearchTerm.tokens.length)
    val searchTermCountAgainstAllWords = tryDivide(containExactCounts(allWords map { _.stemmedValue }, cleanSearchTerm.tokens), cleanSearchTerm.tokens.length)

    val searchDimensionAttributes = getDimensionAttributes(cleanSearchTerm.attributes)
    val titleDimensionAttributes = getDimensionAttributes(cleanTitle.attributes)

    val dimensionsSpecified = if (searchDimensionAttributes.nonEmpty) 1.0 else 0.0
    val dimensionsFeature = tryDivide(similarCounts(searchDimensionAttributes, titleDimensionAttributes), searchDimensionAttributes.length)

    val tfidfFeature = extractTfidfFeature(cleanSearchTerm, item.productId)
    assert(tfidfFeature.length == 4)
    val tfidfTier = {
      if (tfidfFeature.head < 0.5) 0.0
      else 1.0
    }

    val brandFeature = extractBrandFeature(item.productId, cleanSearchTerm.tokens)
    val materialMatches = extractMaterialFeature(item.productId, cleanSearchTerm.tokens)
    val productTypeMatches = extractProductTypeFeature(item.productId, cleanSearchTerm.tokens)
    val queryMatchDecisionTree = if (queryMatch > 0.1) 1.0 else 0.0
    val cleanSearchTermTier = cleanSearchTerm.tokens.length match {
      case 0 => 0.0
//      case 1 | 2 => 1.0
      case _ => 2.0
    }
    val searchTitleCountRatio = {
      val ratio = cleanSearchTerm.tokens.length.toDouble / cleanTitle.tokens.length
      if (ratio <= 0.33) 0.0
      else if (ratio <= 0.66) 1.0
      else 2.0
    }

    // TODO: THIS MUST BE REFACTORED
    Feature(
      LinearRegressionFeature(
      tfidfFeature ++ List(jaccard, queryMatch, searchInTitleContained, titleInSearchContained, abbreviationMatches, searchTermCountAgainstAllWords, dimensionsFeature) // Linear Regression features
      ),
      DecisionTreeFeatures(List(
        //DecisionTreeFeature(searchTitleCountRatio), // TODO: Maybe have it back
        DecisionTreeFeature(brandFeature),
//        DecisionTreeFeature(tfidfTier),
        DecisionTreeFeature(productTypeMatches),
        DecisionTreeFeature(materialMatches),
        // TODO: Add clean search term tier
        //DecisionTreeFeature(queryMatchDecisionTree),
        DecisionTreeFeature(cleanSearchTermTier),
        DecisionTreeFeature(dimensionsSpecified)
      ))  // Decision Tree features
    )
  }

  private def extractTfidfFeature(search: CleanTerm, id: ProductId): List[Double] = {
    val weights = search.tokens map { t => tfidfService.tfidf(t.stemmedValue, id) }
    val extendedWeights = {
      if (weights.length < 3) {
        val toExtend = 3 - weights.length
        0 until toExtend map { t => 0.0 } union weights
      } else {
        weights
      }
    }
    extendedWeights.sum :: (extendedWeights sortBy { t => t } takeRight 3).toList
  }

  private def containExactCounts(inSet: Set[String], from: List[CleanToken]): Int = {
    from count { x => inSet.contains(x.stemmedValue) }
  }

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

  // TODO: This should be the same function as the one below
  private def similarCounts(a: List[CleanToken], b: List[CleanToken]): Int = {
    a map { case CleanToken (_, s, _, _) =>
      (b map { case CleanToken(_, w, _, _) =>
        if (w == s) 1 else 0
      }).sum
    } count { _ > 0 }
  }

  private def containCounts(a: List[CleanToken], b: List[CleanToken]): Int = {
    a map { case CleanToken (_, s, _, _) =>
      (b map { case CleanToken(_, w, _, _) =>
        if (w.length <= 3 && s.length <= 3) {
          if (s==w) 1 else 0
        } else if (w.contains(s)) 1 else 0
      }).sum
    } count { _ > 0 }
  }

  private def extractBrandFeature(productId: ProductId, cleanSearchTerm: List[CleanToken]): Double = {
    val attr = attributeService.getClean(productId)
    attr.get(BRAND) match {
      case Some(value) if similarCounts(value.cleanValue, cleanSearchTerm) > 0 => 1.0
      case _ => 0.0
      // TODO: CleanToken should say if word is BRAND which can be used to compare the brand, and if is wrong to return -1
      //        calcMatchCount((value.cleanValue map { _.stemmedValue }).toSet, cleanSearchTerm).toDouble / value.cleanValue.length
    }
  }

  private def extractMaterialFeature(productId: ProductId, cleanSearchTerm: List[CleanToken]): Double = {
    val attr = attributeService.getClean(productId)
    attr.get(MATERIAL) match {
      case Some(value) if similarCounts(value.cleanValue, cleanSearchTerm) > 0 => 1.0
      case _ => 0.0
      // TODO: CleanToken should say if word is BRAND which can be used to compare the brand, and if is wrong to return -1
      //        calcMatchCount((value.cleanValue map { _.stemmedValue }).toSet, cleanSearchTerm).toDouble / value.cleanValue.length
    }
  }

  private def extractProductTypeFeature(productId: ProductId, cleanSearchTerm: List[CleanToken]): Double = {
    val attr = attributeService.getClean(productId)
    attr.get(PRODUCT_TYPE) match {
      case Some(value) if similarCounts(value.cleanValue, cleanSearchTerm) > 0 => 1.0
      case _ => 0.0
      // TODO: CleanToken should say if word is BRAND which can be used to compare the brand, and if is wrong to return -1
      //        calcMatchCount((value.cleanValue map { _.stemmedValue }).toSet, cleanSearchTerm).toDouble / value.cleanValue.length
    }
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