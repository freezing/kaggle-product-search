package com.kaggle.service

import java.util.logging.Logger

/**
  * Created by freezing on 01/03/16.
  */
class SpellCheckerService {
  val logger = Logger.getLogger(getClass.getName)
  lazy val dictionary = {
    val ret = (CsvReader.readTextFile("/spell_checker_dictionary.txt") map { line =>
      val cols = line.split(":")
      val key = cols.head
      val values = cols.last.split(",")
      key -> values.toSet
    }).toMap
    logger.info("Spell checker dictionary completed.")
    ret
  }

  def getMatches(key: String) = dictionary.getOrElse(key, Set.empty[String])
}
