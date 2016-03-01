package com.kaggle.jobs

import java.nio.file.Paths
import java.util.logging.Logger

import com.kaggle.SpellCheckerDictionaryFileCreator
import com.kaggle.model.{Description, RawAttribute, TestItem, TrainItem}
import com.kaggle.nlp.{NlpUtils, DataLexer}
import com.kaggle.service.{AttributeService, DescriptionService, CsvReader}

import scala.collection.mutable

/**
  * 1. Read all files
  */
object SpellChekerDictionaryCreator extends App {
  val dictionaryPath = args.toSeq sliding 2 collectFirst {
    case Seq("--dictionaryPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No output path specified") }

  val logger = Logger.getLogger(getClass.getName)
  val lexer = new DataLexer()
  val descriptionService = new DescriptionService
  val attributeService = new AttributeService

  val trainData = CsvReader.readTrainData("/train.csv")
  val testData = CsvReader.readTestData("/test.csv")
  val descriptions = descriptionService.getAllRaw.values.toList
  val attributes = attributeService.getAllRaw.values

  logger.info("Parsing words from all files...")
  val words = (parseTrainWords(trainData) union parseTestWords(testData) union parseAttributes(attributes) union parseDescriptions(descriptions)
    flatMap { w => w.split("[\\W\\d]") } filter { _.length > 0 } map { _.toLowerCase }).distinct
  logger.info("Parsing words has finished.")
  val dictionary = new scala.collection.mutable.HashMap[String, scala.collection.mutable.MutableList[String]]

  logger.info(s"Creating dictionary for ${words.length} words...")
  words foreach { w =>
    val variations = NlpUtils.smallErrorsFailSafe(w)

    variations foreach { s =>
      if (!dictionary.contains(s)) dictionary.put(s, new mutable.MutableList[String])
      if (!dictionary.get(s).get.contains(w)) dictionary.get(s).get += w
    }
  }
  logger.info("Dictionary has been created.")

  logger.info(s"Saving dictionary to file: $dictionaryPath")
  new SpellCheckerDictionaryFileCreator(dictionary).save(dictionaryPath)
  logger.info("Dictionary has been saved.")

  private def parseDescriptions(descriptions: List[Description]): List[String] = {
    logger.info("Parsing descriptions...")
    val ret = descriptions flatMap { x => lexer.tokenize(x) } map { _.value }
    logger.info("Parsing descriptions has finished.")
    ret
  }

  private def parseAttributes(attributes: Iterable[List[RawAttribute]]): List[String] = {
    logger.info("Parsing attributes...")
    val ret = (attributes flatMap { _ flatMap { x => lexer.tokenize(x.name.original) map { _.value } union (lexer.tokenize(x.value) map { _.value }) } }).toList
    logger.info("Parsing attributes has finished.")
    ret
  }

  private def parseTrainWords(trainData: List[TrainItem]): List[String] = {
    logger.info("Parsing train words...")
    val ret = trainData flatMap { item => lexer.tokenize(item.rawData.title) } map { _.value }
    logger.info("Parsing train words has finished.")
    ret
  }

  private def parseTestWords(testData: List[TestItem]): List[String] = {
    logger.info("Parsing test words...")
    val ret = testData flatMap { item => lexer.tokenize(item.title) } map { _.value }
    logger.info("Parsing test words has finished")
    ret
  }
}
