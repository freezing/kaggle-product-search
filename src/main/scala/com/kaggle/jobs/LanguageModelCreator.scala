package com.kaggle.jobs

import java.nio.file.Paths
import java.util.logging.Logger

import com.kaggle.file.{BigramDictionaryFile, WordDictionaryFile}
import com.kaggle.model._
import com.kaggle.nlp._
import com.kaggle.service.{CsvReader, AttributeService, DescriptionService}

/**
  * Created by freezing on 02/03/16.
  */
object LanguageModelCreator extends App {
  val logger = Logger.getLogger(getClass.getName)

  val ngram1OutputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--ngram1Path", path) => Paths.get(path)
  } getOrElse { throw new Exception("No ngram1 output path specified") }

  val ngram2OutputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--ngram2Path", path) => Paths.get(path)
  } getOrElse { throw new Exception("No ngram1 output path specified") }

  val lexer = new DataLexer()
  val descriptionService = new DescriptionService
  val attributeService = new AttributeService

  val trainData = CsvReader.readTrainData("/train.csv")
  val testData = CsvReader.readTestData("/test.csv")
  val descriptions = descriptionService.getAllRaw.values.toList
  val attributes = attributeService.getAllRaw.values

  logger.info("Parsing sentences...")
  val sentences = parseTrainWords(trainData) union parseTestWords(testData) union parseAttributes(attributes) union parseDescriptions(descriptions)
  logger.info("Parsing sentences has finished.")

  logger.info("Creating word counts...")
  val wordCounts = sentences flatMap { _.words } groupBy { x => x } map { case (k, v) => (k, v.length) }
  logger.info("Creating word counts has finished.")

  logger.info("Creating bigrams...")
  val bigrams = sentences flatMap { x =>
    x.words.length match {
      case 1 => List.empty[Bigram]
      case _ => x.words sliding 2 map { t => Bigram(t.head, t.last) }
    }
  }
  logger.info("Creating bigrams has finished.")

  logger.info("Creating bigram counts...")
  val bigramCounts = bigrams groupBy { x => x } map { case (k, v) => (k, v.length) }
  logger.info("Creating bigram counts has finished.")

  logger.info("Saving word counts...")
  new WordDictionaryFile(wordCounts).save(ngram1OutputPath)
  logger.info("Saving word counts has finished.")

  logger.info("Saving bigram counts...")
  new BigramDictionaryFile(bigramCounts).save(ngram2OutputPath)
  logger.info("Saving bigram counts has finished.")

  private def postLexerProcess(token: Token): String = {
    if (NlpUtils.isNumber(token.value)) {
      "NUMBER"
    } else {
      token.value.toLowerCase
    }
  }

  private def parseDescriptions(descriptions: List[Description]): List[Sentence] = {
    logger.info("Parsing descriptions...")
    val ret = descriptions flatMap { _.split(".") map { x => Sentence(lexer.tokenize(x) map postLexerProcess) } }
    logger.info("Parsing descriptions has finished.")
    ret
  }

  private def parseAttributes(attributes: Iterable[List[RawAttribute]]): List[Sentence] = {
    logger.info("Parsing attributes...")
    val ret = (attributes flatMap { attr => attr map { x =>
      List(
        Sentence(lexer.tokenize(x.name.original) map postLexerProcess),
        Sentence(lexer.tokenize(x.value) map postLexerProcess)
      )} }).toList.flatten
    logger.info("Parsing attributes has finished.")
    ret
  }

  private def parseTrainWords(trainData: List[TrainItem]): List[Sentence] = {
    logger.info("Parsing train words...")
    val ret = trainData flatMap { item => lexer.tokenize(item.rawData.title) } map postLexerProcess
    logger.info("Parsing train words has finished.")
    List(Sentence(ret))
  }

  private def parseTestWords(testData: List[TestItem]): List[Sentence] = {
    logger.info("Parsing test words...")
    val ret = testData flatMap { item => lexer.tokenize(item.title) } map postLexerProcess
    logger.info("Parsing test words has finished")
    List(Sentence(ret))
  }
}
