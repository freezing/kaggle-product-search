package com.kaggle.file

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
      s"${e.id.value},${clamp(e.relevance.value, 1, 3)}"
    } mkString "\n"
    header + data
  }

  private def clamp(value: Double, minVal: Double, maxVal: Double): Double = Math.min(maxVal, Math.max(minVal, value))
}
