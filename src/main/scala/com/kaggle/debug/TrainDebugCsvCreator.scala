package com.kaggle.debug

import java.io.PrintWriter
import java.nio.file.Path

import com.kaggle.feature.{TrainFeature, TestFeature}
import com.kaggle.model._
import com.kaggle.nlp.CleanToken

/**
  * Created by freezing on 29/02/16.
  */
class TrainDebugCsvCreator(evaluations: List[Evaluation], trainFeatures: List[TrainFeature], cleanTrainData: List[CleanTrainItem]) {
  def save(path: Path): Unit = {
    new PrintWriter(path.toAbsolutePath.toString) {
      write(makeContents)
      close()
    }
  }

  private def makeContents: String = {
    val header = "title,search,clean title,clean search,features,relevance,prediction\n"
    val data = evaluations zip trainFeatures zip cleanTrainData map { case ((Evaluation(_, prediction), trainFeature), trainItem) =>
      s"${"\"" + trainItem.original.rawData.title + "\""},${"\"" + trainItem.original.rawData.searchTerm.value + "\""}," +
        s"${cleanValue(trainItem.cleanTitle)}" +
        s"${cleanValue(trainItem.cleanSearchTerm)},${featureString(trainFeature.feature.coordinates)},${trainItem.original.relevance.value},${prediction.value}"
    } mkString "\n"
    header + data
  }

  private def cleanValue(clean: List[CleanToken]): String = clean map { _.stemmedValue } mkString("\"", " ", "\"")

  private def featureString(a: List[Double]): String = a map { "" + _ } mkString ","

}
