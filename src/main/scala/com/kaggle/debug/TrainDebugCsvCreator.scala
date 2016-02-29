package com.kaggle.debug

import java.io.PrintWriter
import java.nio.file.Path

import com.kaggle.feature.{TrainFeature, TestFeature}
import com.kaggle.model.{TrainItem, Id, TestItem, Evaluation}

/**
  * Created by freezing on 29/02/16.
  */
class TrainDebugCsvCreator(evaluations: List[Evaluation], trainFeatures: List[TrainFeature], testData: List[TrainItem]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      write(makeContents)
      close()
    }
  }

  private def makeContents: String = {
    val header = "title,search,features,relevance,prediction\n"
    val data = evaluations zip trainFeatures zip testData map { case ((Evaluation(_, prediction), trainFeature), trainItem) =>
      s"${"\"" + trainItem.rawData.title + "\""},${"\"" + trainItem.rawData.searchTerm.value + "\""},${featureString(trainFeature.feature.coordinates)},${trainItem.relevance.value},${prediction.value}"
    } mkString "\n"
    header + data
  }

  private def featureString(a: List[Double]): String = a map { "" + _ } mkString ","

}
