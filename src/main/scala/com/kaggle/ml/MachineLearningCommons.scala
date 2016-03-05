package com.kaggle.ml

import com.kaggle.feature.{TestFeature, TrainFeature}
import com.kaggle.model.{Relevance, Evaluation}
import org.apache.commons.math3.stat.regression.{OLSMultipleLinearRegression, MultipleLinearRegression}

/**
  * Created by freezing on 2/29/16.
  */
class MachineLearningCommons {
  val regression = new OLSMultipleLinearRegression()

  def train(data: List[TrainFeature]): Unit = {
    val x = (data map { _.feature.linearRegressionFeature.coordinates.toArray }).toArray
    val y = (data map { _.relevance.value}).toArray
    regression.setNoIntercept(false)
    regression.newSampleData(y, x)
  }

  def predict(data: List[TestFeature]): List[Evaluation] = {
    val b = regression.estimateRegressionParameters()
    val b0 = b.head
    val betas = b takeRight (b.length - 1)
    b foreach { println }
    data map { item =>
      val prediction = b0 + (betas zip item.feature.linearRegressionFeature.coordinates map { case (c, d) => c * d }).sum
      Evaluation(item.id, Relevance(prediction))
    }
  }
}

object MachineLearningCommons extends MachineLearningCommons
