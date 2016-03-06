package com.kaggle.file

import java.io.PrintWriter
import java.nio.file.Path

/**
  * Created by freezing on 06/03/16.
  */
class StemFileCreator(stems: Map[String, List[String]]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      stems foreach { case (k, v) =>
        write(makeContents(k, v))
      }
      close()
    }
  }

  private def makeContents(k: String, v: List[String]): String = {
    s"$k:${v mkString ","}"
  }

  //private def makeContents(k: String, v: List[String]): String = s"$k:$v" + "\n"
}
