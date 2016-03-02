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
                  dataTokenClassification: DataTokenClassification, dataSemanticExtraction: DataSemanticExtraction) extends Serializable {
  private val logger = Logger.getLogger(getClass.getName)

  def process(data: String): List[CleanToken] = {
    dataLexer.tokenize(data) map
      dataSpellChecker.process map
      dataStemmer.process map
      dataTokenClassification.process map
      dataSemanticExtraction.process
  }

  def processTestData(data: List[TestItem]): List[CleanTestItem] = {
    logger.info(s"Cleaning test data...")
    val cleaned = data map { item =>
      val cleanTitle = process(item.title)
      if ((cleanTitle count { _.stemmedValue.length == 0 }) > 0 ) println(s"${item.title}")
      val cleanSearchTerm = process(item.searchTerm.value)
      CleanTestItem(item, cleanTitle, cleanSearchTerm)
    }
    logger.info(s"Cleaning test data finished.")
    cleaned
  }

  def processTrainData(data: List[TrainItem]): List[CleanTrainItem] = {
    logger.info(s"Cleaning training data...")
    val cleaned = data map { item =>
      val cleanTitle = process(item.rawData.title)
      if ((cleanTitle count { _.stemmedValue.length == 0 }) > 0 ) println(s"${item.rawData.title}")
      val cleanSearchTerm = process(item.rawData.searchTerm.value)
      CleanTrainItem(item, cleanTitle, cleanSearchTerm)
    }
    logger.info("Cleaning training data finished.")
    cleaned
  }

  def processTestDataSpark(data: RDD[TestItem]): RDD[CleanTestItem] = {
    data map { item =>
      val cleanTitle = process(item.title)
      val cleanSearchTerm = process(item.searchTerm.value)
      CleanTestItem(item, cleanTitle, cleanSearchTerm)
    }
  }

  def processTrainDataSpark(data: RDD[TrainItem]): RDD[CleanTrainItem] = {
    data map { item =>
      val cleanTitle = process(item.rawData.title)
      val cleanSearchTerm = process(item.rawData.searchTerm.value)
      CleanTrainItem(item, cleanTitle, cleanSearchTerm)
    }
  }
}

object DataCleaner extends DataCleaner
