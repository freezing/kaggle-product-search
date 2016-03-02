package com.kaggle

import com.kaggle.model.{TestItem, TrainItem}
import com.kaggle.parser.CsvParser
import org.apache.spark.rdd.RDD

/**
  * Created by freezing on 2/25/16.
  */
package object service {
  val DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"

  object SparkFileReader {
    import com.kaggle.sc
    def readTextFile(file: String): RDD[String] = {
      val path = getClass.getResource(file).getFile
      sc.textFile(path.toString, 1)
//      sc.parallelize(CsvReader.readFile(file)).cache
    }

    def readTrainData(file: String): RDD[TrainItem] = readTextFile(file) map { line => CsvParser.parseTrainData(line) }
    def readTestData(file: String): RDD[TestItem] = readTextFile(file) map { line => CsvParser.parseTestData(line) }
  }

  // Initialize implicit services
  implicit val attributesService = new AttributeService
  implicit val descriptionService = new DescriptionService
  implicit val spellCheckerService = new SpellCheckerService
  implicit val languageModelService = new LanguageModelService
}
