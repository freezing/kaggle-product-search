package com.kaggle.ml

import com.kaggle.feature.{TestFeature, TrainFeature}
import com.kaggle.model.{Relevance, Evaluation}

/**
  * Created by freezing on 29/02/16.
  */
class MachineLearning {
  val numberOfSteps = 500
  val alpha = 0.3
  val lambda = 0.1
  val normalize = true
  val threshold = 0.1

  var linearRegression: LinearRegression = null

  def train(trainDataFeatures: List[TrainFeature]): Unit = {
    val featureSize = trainDataFeatures.head.feature.coordinates.size
    linearRegression = new LinearRegression(featureSize, numberOfSteps, alpha, lambda, normalize, threshold)

    val labeledFeatures = trainDataFeatures map { case TrainFeature(feature, relevance, id) => LabeledFeature(feature, relevance.value) }
    linearRegression.train(labeledFeatures)
    println("Thetas")
    linearRegression.thetas foreach { theta => println(theta)}
  }

  def predict(testDataFeatures: List[TestFeature]): List[Evaluation] = {
    val testData = testDataFeatures map { _.feature }
    val predictions = linearRegression.predict(testData)
    testDataFeatures zip predictions map { case (TestFeature(_, id), prediction) => Evaluation(id, Relevance(prediction)) }
  }

  def RMS(labeledData: List[TrainFeature]): Double = {
    Math.sqrt((labeledData map { case TrainFeature(feature, Relevance(label), id) =>
      val prediction = linearRegression.predict(feature)
      val error = prediction - label
      error * error
    }).sum / labeledData.length)
  }
}