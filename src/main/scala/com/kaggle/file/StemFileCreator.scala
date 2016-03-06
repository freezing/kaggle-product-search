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
    //s"$k:${v mkString ","}" + "\n"
    if (k.length < 6) {
      ""
    } else {
      val stem = commonPrefix(v)
      v map { s => s"$s,$stem" } mkString("", "\n", "\n")
    }
  }

  private def commonPrefix(v: List[String]): String = {
    var pref = v.head
    v foreach { x => pref = commonPrefix(pref, x) }
    pref
  }

  private def commonPrefix(s: String, w: String): String = {
    s.zip(w).takeWhile(Function.tupled(_ == _)).map(_._1).mkString
  }

  //private def makeContents(k: String, v: List[String]): String = s"$k:$v" + "\n"
}
