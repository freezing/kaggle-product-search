package com.kaggle.jobs

import java.nio.file.Paths
import java.util.logging.Logger

import com.kaggle.file.StemFileCreator
import com.kaggle.service.LanguageModelService

/**
  * Created by freezing on 06/03/16.
  */
object StemmerCreator extends App {
  val logger = Logger.getLogger(getClass.getName)

  val outputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--outputPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No stem output path specified") }

  val languageModelService = new LanguageModelService
  val words = languageModelService.wordCounts.keys

  val stems = words groupBy { w => if (w.length > 5) w.substring(0, 5) else w } map { case (k, v) => k -> v.toList }
  new StemFileCreator(stems).save(outputPath)
}
