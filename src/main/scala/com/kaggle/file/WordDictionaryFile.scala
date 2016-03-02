package com.kaggle.file

import java.io.PrintWriter
import java.nio.file.Path

/**
  * Created by freezing on 02/03/16.
  */
class WordDictionaryFile(wordCounts: Map[String, Int]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      wordCounts foreach { case (k, v) =>
        write(makeContents(k, v))
      }
      close()
    }
  }

  private def makeContents(k: String, v: Int): String = s"$k,$v" + "\n"
}
