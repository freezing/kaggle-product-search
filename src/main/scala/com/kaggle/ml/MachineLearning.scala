package com.kaggle.ml

import com.kaggle.feature.{TestFeature, TrainFeature, Feature}
import com.kaggle.model.{Evaluation, Id, Relevance}
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.regression.{LinearRegressionModel, LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.mllib.linalg._

/**
  * Created by freezing on 2/26/16.
  */
class MachineLearning {
  def train(data: RDD[TrainFeature]): LinearRegressionModel = {
    val labeledData = data map { trainFeature =>
      LabeledPoint(trainFeature.relevance.value, toVector(trainFeature.feature))
    }
    LinearRegressionWithSGD.train(labeledData, 2000, 0.2, 1.0)
  }

  def predict(model: LinearRegressionModel, data: RDD[TestFeature]): RDD[Evaluation] = {
    ???
  }

  def trainAndPredict(trainData: RDD[TrainFeature], testData: RDD[TestFeature]): RDD[Evaluation] = {
    val model = train(trainData)
    predict(model, testData)
  }

//class MachineLearning(implicit val sc: SparkContext) extends Serializable {
//
//  var model: LinearRegressionModel = null
//
//  def train(trainingData: List[(Feature, Relevance)]): Unit = {
//    val training = sc.parallelize(createTrainingSet(trainingData)).cache()
//    model = LinearRegressionWithSGD.train(training, 2000, 0.2)
//  }
//
//  def evaluate(testData: List[(Id, Feature)]): List[Evaluation] = {
//    testData map { case (id, feature) =>
//      val prediction = Math.min(3.0, Math.max(model.predict(toVector(feature)), 1.0))
//      Evaluation(id, Relevance(prediction))
//    }
//  }
//
//  private def createTestSet(data: List[(Id, Feature)]): Seq[Vector] = {
//    data map { case (id, feature) => toVector(feature) }
//  }
//
//  private def createTrainingSet(data: List[(Feature, Relevance)]): Seq[LabeledPoint] = {
//    data map { case (feature, Relevance(relevance)) =>
//      LabeledPoint(relevance, toVector(feature))
//    }
//  }
//
  private def toVector(feature: Feature): Vector = {
    Vectors dense feature.coordinates.toArray
  }
}

object MachineLearning extends MachineLearning
