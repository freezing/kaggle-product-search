package com.kaggle.nlp

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
  def process(data: String): List[CleanToken] = {
    dataLexer.tokenize(data) map
      dataSpellChecker.process map
      dataStemmer.process map
      dataTokenClassification.process map
      dataSemanticExtraction.process
  }

  def processTestData(data: List[TestItem]): List[CleanTestItem] = {
    data map { item =>
      val cleanTitle = process(item.title)
      val cleanSearchTerm = process(item.searchTerm.value)
      CleanTestItem(item, cleanTitle, cleanSearchTerm)
    }
  }

  def processTrainData(data: List[TrainItem]): List[CleanTrainItem] = {
    data map { item =>
      val cleanTitle = process(item.rawData.title)
      val cleanSearchTerm = process(item.rawData.searchTerm.value)
      CleanTrainItem(item, cleanTitle, cleanSearchTerm)
    }
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
