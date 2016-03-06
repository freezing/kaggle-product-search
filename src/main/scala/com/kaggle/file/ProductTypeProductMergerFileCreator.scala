package com.kaggle.file

import java.io.PrintWriter
import java.nio.file.Path

import com.kaggle.model.TrainItem

/**
  * Created by freezing on 06/03/16.
  */
class ProductTypeProductMergerFileCreator(v: List[(String, TrainItem)]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      v foreach { case (k, v) =>
        write(makeContents(k, v))
      }
      close()
    }
  }

  private def makeContents(k: String, v: TrainItem): String = s"${v.rawData.productId} $k," +
    s"${"\"" + v.rawData.title + "\""},${"\"" + v.rawData.searchTerm.value + "\""},${v.relevance.value}" + "\n"
}
