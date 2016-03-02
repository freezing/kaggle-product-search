package com.kaggle.file

import java.io.PrintWriter
import java.nio.file.Path

/**
  * Created by freezing on 01/03/16.
  */
class SpellCheckerDictionaryFileCreator(dictionary: scala.collection.mutable.HashMap[String, scala.collection.mutable.MutableList[String]]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      dictionary foreach { case (k, v) =>
        write(makeContents(k, v))
      }
      close()
    }
  }

  private def makeContents(k: String, v: scala.collection.mutable.MutableList[String]): String = s"$k:${v mkString ","}" + "\n"
}
