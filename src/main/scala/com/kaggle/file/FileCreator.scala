package com.kaggle.file

import java.io.PrintWriter
import java.nio.file.Path

/**
  * Created by freezing on 07/03/16.
  */
trait FileCreator {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      write(makeContents)
      close()
    }
  }

  protected def makeContents: String
}
