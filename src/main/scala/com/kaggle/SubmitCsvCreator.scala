package com.kaggle

import java.io.PrintWriter
import java.nio.file.Path

import com.kaggle.model.Evaluation

/**
  * Created by freezing on 2/25/16.
  */
class SubmitCsvCreator(evaluations: List[Evaluation]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      write(makeContents)
      close()
    }
  }

  private def makeContents: String = {
    val header = "id,relevance\n"
    val data = evaluations map { e =>
      s"${e.id.value},${e.relevance.value}"
    } mkString "\n"
    header + data
  }
}
