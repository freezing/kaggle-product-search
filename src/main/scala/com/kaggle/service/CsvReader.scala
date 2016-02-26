package com.kaggle.service

import java.nio.file.{Paths, Files}

/**
  * Created by freezing on 2/25/16.
  */
object CsvReader {
  def readFile(file: String): List[String] = {
    val path = getClass.getResource(file).getFile
    new String(Files.readAllBytes(Paths.get(path.toString))).split("\n").toList
  }
}
