package com.kaggle.service

/**
  * Created by freezing on 01/03/16.
  */
class SpellCheckerService {
  lazy val dictionary = {
    (CsvReader.readTextFile("/spell_checker_dictionary.txt") map { line =>
      val cols = line.split(":")
      val key = cols.head
      val values = cols.last.split(",")
      key -> values.toList
    }).toMap
  }

  def getMatches(key: String) = dictionary.getOrElse(key, List(key))
}
