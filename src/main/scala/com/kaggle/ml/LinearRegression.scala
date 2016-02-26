package com.kaggle.ml

import com.kaggle.feature.Feature
import com.kaggle.model.{Evaluation, Id, Relevance}
import org.apache.commons.math3.stat.regression.{SimpleRegression, GLSMultipleLinearRegression}

/**
  * Created by freezing on 2/26/16.
  */
class LinearRegression {
  val regression = new SimpleRegression

  def train(trainingData: List[(Feature, Relevance)]): Unit = {
    trainingData foreach {
      case (feature, relevance) => regression.addData(feature.coordinates.head, relevance.value)
    }
  }

  def evaluate(testData: List[(Id, Feature)]): List[Evaluation] = {
    testData map { case (id, feature) =>
      Evaluation(id, Relevance(regression.predict(feature.coordinates.head)))
    }
  }
}
