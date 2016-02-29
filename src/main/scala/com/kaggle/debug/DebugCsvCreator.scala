package com.kaggle.debug

import java.io.PrintWriter
import java.nio.file.Path

import com.kaggle.feature.TestFeature
import com.kaggle.model.{Evaluation, Id, TestItem}

/**
  * Created by freezing on 29/02/16.
  */
class DebugCsvCreator(evaluations: List[Evaluation], testFeatures: List[TestFeature], testData: List[TestItem]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      write(makeContents)
      close()
    }
  }

  private def makeContents: String = {
    val header = "id,title,search,features,relevance\n"
    val data = evaluations zip testFeatures zip testData map { case ((Evaluation(Id(id), relevance), testFeature), testItem) =>
      s"$id,${"\"" + testItem.title + "\""},${"\"" + testItem.searchTerm.value + "\""},${featureString(testFeature.feature.coordinates)},${relevance.value}"
    } mkString "\n"
    header + data
  }

  private def featureString(a: List[Double]): String = a map { "" + _ } mkString ","
}
