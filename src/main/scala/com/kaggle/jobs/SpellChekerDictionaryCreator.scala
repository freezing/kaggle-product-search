package com.kaggle.jobs

import java.nio.file.Paths
import java.util.logging.Logger
import com.kaggle.file.SpellCheckerDictionaryFileCreator
import com.kaggle.model.{Description, RawAttribute, TestItem, TrainItem}
import com.kaggle.nlp.{NlpUtils, DataLexer}
import com.kaggle.service.{LanguageModelService, AttributeService, DescriptionService, CsvReader}

import scala.collection.mutable

/**
  * 1. Read all files
  */
object SpellChekerDictionaryCreator extends App {
  val logger = Logger.getLogger(getClass.getName)

  val dictionaryPath = args.toSeq sliding 2 collectFirst {
    case Seq("--dictionaryPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No output path specified") }

  val languageModelService = new LanguageModelService

  logger.info("Parsing words from all files...")
  val words = languageModelService.wordCounts.keys
  logger.info("Parsing words has finished.")
  val dictionary = new scala.collection.mutable.HashMap[String, scala.collection.mutable.MutableList[String]]

  logger.info(s"Creating dictionary for ${words.size} words...")
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
}
