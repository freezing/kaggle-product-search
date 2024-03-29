package com.kaggle.nlp

import java.util.logging.Logger

import com.kaggle.model.{TestItem, CleanTestItem, CleanTrainItem, TrainItem}
import org.apache.spark.rdd.RDD

/**
  * Created by freezing on 28/02/16.
  * 1. DataLexer - split data in the list of tokens
  * 2. DataSpellChecker - find spell errors and fix them
  * 3. DataStemmer - stem the tokens
  * 4. DataTokenClassification - classify tokens by word type such as noun, verb, ...
  * 5. DataSemanticExtraction - extract semantics for each token such as: color, product type, brand, material, ...
  */
class DataCleaner(implicit val dataLexer: DataLexer, dataSpellChecker: DataSpellChecker, dataStemmer: DataStemmer,
                  dataTokenClassification: DataTokenClassification, dataSemanticExtraction: DataSemanticExtraction,
                  termSemanticExtraction: TermSemanticExtraction, searchCorrection: SearchCorrection, tokenMerger: TokenMerger) extends Serializable {
  private val logger = Logger.getLogger(getClass.getName)

  val USE_SPELL_CORRECTION = true
  val DONT_USE_SPELL_CORRECTION = false

  def process(data: String, spellCorrection: Boolean = DONT_USE_SPELL_CORRECTION): List[CleanToken] = {
    val readyData = {
      val tokenizedData = dataLexer.tokenize(data)
      // Spell checker is checking bigrams so need to process as whole list
//      if (spellCorrection) dataSpellChecker.process(tokenizedData)
//      else tokenizedData
      tokenizedData
    }

    readyData map
      dataStemmer.process map
      dataTokenClassification.process map
      dataSemanticExtraction.process
  }

  def processTestData(data: List[TestItem]): List[CleanTestItem] = {
    logger.info(s"Cleaning test data...")
    val cleaned = data map { item =>
      val cleanTitle = termSemanticExtraction.process(process(item.title))
      val cleanSearchTerm = termSemanticExtraction.process(process(item.searchTerm.value, USE_SPELL_CORRECTION))
      val mergedTitle = tokenMerger.process(cleanTitle, cleanSearchTerm)
      val mergedSearch = tokenMerger.process(cleanSearchTerm, cleanTitle)
      val correctedSearchTerm = searchCorrection.correct(mergedSearch, mergedTitle)
      CleanTestItem(item, mergedTitle, correctedSearchTerm)
    }
    logger.info(s"Cleaning test data finished.")
    cleaned
  }

  def processTrainData(data: List[TrainItem]): List[CleanTrainItem] = {
    logger.info(s"Cleaning training data...")
    val cleaned = data map { item =>
      val cleanTitle = termSemanticExtraction.process(process(item.rawData.title))
      val cleanSearchTerm = termSemanticExtraction.process(process(item.rawData.searchTerm.value, USE_SPELL_CORRECTION))
      val mergedTitle = tokenMerger.process(cleanTitle, cleanSearchTerm)
      val mergedSearch = tokenMerger.process(cleanSearchTerm, cleanTitle)
      val correctedSearchTerm = searchCorrection.correct(mergedSearch, mergedTitle)
      CleanTrainItem(item, mergedTitle, correctedSearchTerm)
    }
    logger.info("Cleaning training data finished.")
    cleaned
  }

  def processTestDataSpark(data: RDD[TestItem]): RDD[CleanTestItem] = {
    data map { item =>
      val cleanTitle = termSemanticExtraction.process(process(item.title))
      val cleanSearchTerm = termSemanticExtraction.process(process(item.searchTerm.value, USE_SPELL_CORRECTION))
      CleanTestItem(item, cleanTitle, cleanSearchTerm)
    }
  }

  def processTrainDataSpark(data: RDD[TrainItem]): RDD[CleanTrainItem] = {
    data map { item =>
      val cleanTitle = termSemanticExtraction.process(process(item.rawData.title))
      val cleanSearchTerm = termSemanticExtraction.process(process(item.rawData.searchTerm.value, USE_SPELL_CORRECTION))
      CleanTrainItem(item, cleanTitle, cleanSearchTerm)
    }
  }
}

object DataCleaner extends DataCleaner
