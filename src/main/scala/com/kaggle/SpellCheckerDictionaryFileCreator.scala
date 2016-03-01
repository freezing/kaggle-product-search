package com.kaggle

import java.io.PrintWriter
import java.nio.file.Path

/**
  * Created by freezing on 01/03/16.
  */
class SpellCheckerDictionaryFileCreator(dictionary: scala.collection.mutable.HashMap[String, scala.collection.mutable.MutableList[String]]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      write(makeContents)
      close()
    }
  }

  private def makeContents: String = dictionary map { case (k, v) => s"$k:${v mkString ","}" } mkString "\n"
}
