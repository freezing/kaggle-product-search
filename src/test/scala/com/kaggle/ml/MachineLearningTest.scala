package com.kaggle.ml

import com.kaggle.feature.{TestFeature, TrainFeature}
import com.kaggle.ml.decisiontree.{DecisionTree, DecisionTreeFeatures, DecisionTreeFeature}
import com.kaggle.model.{Id, Relevance, TrainItem}

import scala.util.Random

/**
  * Created by freezing on 05/03/16.
  */
object MachineLearningTest extends App {
  val rnd = new Random
  val trainData = (0 to 2 flatMap { x =>
    val decisionTreeFeature = DecisionTreeFeatures(List(DecisionTreeFeature(x)))
    for {
      idx <- 0 to 4000
      a <- Option(rnd.nextInt(10000))
      b <- Option(rnd.nextInt(10000))
    } yield {
      val relevance = Relevance(x match {
        case 0 => f(a, b)
        case 1 => g(a, b)
        case 2 => z(a, b)
      })
      val lrFeature = LinearRegressionFeature(List(a, b))
      val feature = Feature(lrFeature, decisionTreeFeature)
      TrainFeature(feature, relevance, Id(idx.toString))
    }
  }).toList

  val machineLearning = new MachineLearning(2000, 0.1, 0.1, true, 0.1)
  machineLearning.train(trainData)

  // Test
  val testData = (for {
    typeIdx <- 0 to 2
    idx <- 0 to 1000
    decisionTreeFeatures <- Option(DecisionTreeFeatures(List(DecisionTreeFeature(typeIdx))))
    a <- Option(rnd.nextInt(10000))
    b <- Option(rnd.nextInt(10000))
    expectedRelevance <- Option(Relevance(fgz(a, b, typeIdx)))
    lrFeature <- Option(LinearRegressionFeature(List(a, b)))
    feature <- Option(Feature(lrFeature, decisionTreeFeatures))
  } yield {
    TrainFeature(feature, expectedRelevance, Id(idx + ""))
  }).toList


  val rms = machineLearning.RMS(testData)
  println(s"RMS = $rms")

  def fgz(a: Int, b: Int, typeIdx: Int) = typeIdx match {
    case 0 => f(a, b)
    case 1 => g(a, b)
    case 2 => z(a, b)
  }

  def f(a: Int, b: Int) = 10 * a + 23 * b + 11

  def g(a: Int, b: Int) = -5 * a + 17 * b - 77

  def z(a: Int, b: Int) = 11 * a - 83 * b + 64
}
