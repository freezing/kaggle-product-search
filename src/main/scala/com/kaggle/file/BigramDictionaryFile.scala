package com.kaggle.file

import java.io.PrintWriter
import java.nio.file.Path

import com.kaggle.nlp.Bigram

/**
  * Created by freezing on 02/03/16.
  */
class BigramDictionaryFile(bigramCounts: Map[Bigram, Int]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      bigramCounts foreach { case (k, v) =>
        write(makeContents(k, v))
      }
      close()
    }
  }

  private def makeContents(k: Bigram, v: Int): String = s"${k.w1},${k.w2},$v" + "\n"
}
