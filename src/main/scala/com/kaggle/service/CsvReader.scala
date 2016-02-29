package com.kaggle.service

import java.nio.file.{Paths, Files}

import com.kaggle.model.{TestItem, TrainItem}
import com.kaggle.parser.CsvParser

/**
  * Created by freezing on 2/25/16.
  */
object CsvReader {
  def readTextFile(file: String): List[String] = {
    val path = getClass.getResource(file).getFile
    new String(Files.readAllBytes(Paths.get(path.toString))).split("\n").toList
  }

  def readTrainData(file: String): List[TrainItem] = readTextFile(file) map { line => CsvParser.parseTrainData(line) }
  def readTestData(file: String): List[TestItem] = readTextFile(file) map { line => CsvParser.parseTestData(line) }
}
