package com.kaggle.ml

import java.util.logging.Logger

import scala.collection.mutable
import scala.util.Random

/**
  *
  * @param featureSize number of features per entry
  * @param numberOfSteps maximal number ofsteps for gradient descent
  * @param alpha step size
  * @param lambda regularization parameter
  * @param normalize if true features will be scaled to [-1, 1] using mean and standard deviation
  * @param threshold gradient descent converge threshold
  */
class LinearRegression(featureSize: Int, numberOfSteps: Int, alpha: Double, lambda: Double, normalize: Boolean, threshold: Double) {
  private val logger = Logger.getLogger(getClass.getName)

  val rand = Random
  // All mutable since these are calculated in train method
  // and should be saved for prediction
  var theta: List[Double] = init(featureSize)
  // Default values for mean and stdDev if normalize is set to false
  var means: List[Double] = initMeans(featureSize)
  var stdDevs: List[Double] = initStdDevs(featureSize)

  def train(data: List[LabeledFeature]) = {
    logger.info(s"Started training on set of size: ${data.length}")
    if (normalize) {
      logger.info("Normalize is set")
      calculateNormalizationParameters(data)
    }
    val normalizedData = scale(data)

    // Mutable counter to take care of maximal number of steps
    // And converge to tell us if we have converged
    var currentStep = 0
    var converged = false
    while (currentStep < numberOfSteps && !converged) {
      if (currentStep % 30 == 0) {
        logger.info(s"Current gradient descent step: $currentStep")
      }
      val tmp = gradientDescentStep(theta, normalizedData)
      converged = isConverged(tmp, theta)
      currentStep += 1
      theta = tmp
    }
    println(s"Converged after $currentStep steps")
  }

  def thetas = theta

  def predict(feature: Feature): Double = predictInternal(scale(feature), theta)

  def predict(features: List[Feature]): List[Double] = features map { feature => predict(feature) }

  private def predictInternal(feature: Feature, theta: List[Double]) = (1.0 :: feature.coordinates) X theta

  /**
    *
    * @param n number of features per item
    * @return Initial thetas of length n + 1 (1 is for bias)
    */
  private def init(n: Int): List[Double] = (0 to n map { x => (rand.nextDouble() - 0.5) * 2.0 }).toList

  /**
    * Default means should be 0.0
 *
    * @param n number of features per item
    * @return List of n zeroes.
    */
  private def initMeans(n: Int) = (0 until n map { x => 0.0 }).toList

  /**
    * Default standard deviations should be 1.0.
 *
    * @param n number of features per item
    * @return List of n ones.
    */
  private def initStdDevs(n: Int) = (0 until n map { x => 1.0 }).toList

  private def gradientDescentStep(theta: List[Double], data: List[LabeledFeature]): List[Double] = {
    val m = data.length
    // Mutable part to calculate next theta
    val tmp = new mutable.MutableList[Double]
    tmp += theta.head - alpha / m * gradientSum(theta, data)
    for (j <- 1 until theta.length) {
      tmp += theta(j) * (1.0 - alpha * lambda / m) - alpha / m * gradientSum(theta, data, j - 1)
    }
    tmp.toList
  }

  private def gradientSum(theta: List[Double], data: List[LabeledFeature], j: Int): Double = {
    (data map { case LabeledFeature(feature, label) =>
      (predictInternal(feature, theta) - label) * feature.coordinates(j)
    }).sum
  }

  private def gradientSum(theta: List[Double], data: List[LabeledFeature]): Double = {
    (data map { case LabeledFeature(feature, label) => predictInternal(feature, theta) - label }).sum
  }

  private def isConverged(tmp: List[Double], theta: List[Double]) = {
    val firstNonConverged = tmp zip theta collectFirst { case (a, b) => Math.abs(a - b) > threshold }
    firstNonConverged.isEmpty
  }

  private def calculateNormalizationParameters(data: List[LabeledFeature]): Unit = {
    logger.info("Calculating means...")
    // Calculate means
    val tmpMeans = new mutable.MutableList[Double]
    for (j <- 0 until featureSize) {
      tmpMeans += (data map { x => x.feature.coordinates(j) }).sum / data.length
    }
    means = tmpMeans.toList

    logger.info("Means calculated. Calculating standard deviations...")
    // Calculate standard deviations
    val tmpSds = new mutable.MutableList[Double]
    for (j <- 0 until featureSize) {
      tmpSds += Math.sqrt((data map { x => Math.pow(x.feature.coordinates(j) - means(j), 2.0) }).sum / data.length)
    }
    stdDevs = tmpSds.toList
    logger.info("Normalizing finished")
  }

  private def scale(feature: Feature): Feature = Feature(feature.coordinates zip means zip stdDevs map { case ((x, mean), stdDev) => (x - mean) / stdDev })

  private def scale(data: List[LabeledFeature]): List[LabeledFeature] =
    data map { case LabeledFeature(feature, label) => LabeledFeature(scale(feature), label) }
}