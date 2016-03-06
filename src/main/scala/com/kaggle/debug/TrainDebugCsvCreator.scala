package com.kaggle.debug

import java.io.PrintWriter
import java.nio.file.Path

import com.kaggle.feature.{TrainFeature, TestFeature}
import com.kaggle.ml.decisiontree.DecisionTreeFeatures
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

  implicit val listOrder: Ordering[List[Double]] = Ordering.by(a => (a.head, a(1), a(2), a(3), a(4)))


  private def makeContents: String = {
    val header = "product_id,title,search,clean title,clean search,decisions,features,relevance,prediction\n"
    val data = evaluations zip trainFeatures zip cleanTrainData sortBy { case (trainFeature, _) =>
      trainFeature._2.feature.linearRegressionFeature.coordinates
    } map { case ((Evaluation(_, prediction), trainFeature), trainItem) =>
      s"${trainItem.original.rawData.productId.value}," +
        s"${"\"" + trainItem.original.rawData.title + "\""},${"\"" + trainItem.original.rawData.searchTerm.value + "\""}," +
        s"${cleanTerm(trainItem.cleanTitle)}," +
        s"${cleanTerm(trainItem.cleanSearchTerm)}," +
        s"${dtFeatureString(trainFeature.feature.decisionTreeFeatures)}," +
        s"${featureString(trainFeature.feature.linearRegressionFeature.coordinates)},${trainItem.original.relevance.value},${prediction.value}"
    } mkString "\n"
    header + data
  }

  private def dtFeatureString(features: DecisionTreeFeatures): String = {
    features.vector map { _.value } mkString " : "
  }

  private def cleanTerm(term: CleanTerm): String = { "\"" +
    (term.tokens map {_.stemmedValue} mkString " ") ++ "  | " ++ (term.attributes map { case (k, v) => s"[$k, ${ v map { _.stemmedValue } mkString " " }]"} mkString " ") + "\""
  }

  private def featureString(a: List[Double]): String = a map { "" + _ } mkString ","

}