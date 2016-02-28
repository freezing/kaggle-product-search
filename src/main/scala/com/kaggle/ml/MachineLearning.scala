package com.kaggle.ml

import com.kaggle.feature.{TestFeature, TrainFeature, Feature}
import com.kaggle.model.{Evaluation, Id, Relevance}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.mllib.feature.{StandardScalerModel, StandardScaler}
import org.apache.spark.mllib.optimization.SquaredL2Updater
import org.apache.spark.mllib.regression
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.regression.{LinearRegressionModel, LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.mllib.linalg._

/**
  * Created by freezing on 2/26/16.
  */
class MachineLearning extends Serializable {
  import com.kaggle.sc

  def scale(data: RDD[TrainFeature]): StandardScalerModel = {
    val features = data map { trainFeature => toVector(trainFeature.feature) }
    new StandardScaler(true, true) fit features
  }

  def train(data: RDD[TrainFeature], scalerModel: StandardScalerModel): LinearRegressionModel = {
    val labeledData = data map { trainFeature =>
      LabeledPoint(trainFeature.relevance.value, toVector(trainFeature.feature, scalerModel))
    }
    labeledData.cache()

    val algorithm = new LinearRegressionWithSGD()
    algorithm.optimizer
      .setNumIterations(10)
      .setRegParam(0.01)
      .setStepSize(0.1)
      .setUpdater(new SquaredL2Updater)

    val model = algorithm run labeledData
    model.save(sc, "/home/freezing/Desktop/tmp")
    model
  }

  def predict(model: LinearRegressionModel, data: RDD[TestFeature], scalerModel: StandardScalerModel): RDD[Evaluation] = {
    val vectors = data map { tf => toVector(tf.feature, scalerModel) }
    val predictions = model predict vectors
    predictions zip data map { case (prediction, testFeature) => Evaluation(testFeature.id, Relevance(prediction)) }
  }

  def trainAndPredict(trainData: RDD[TrainFeature], testData: RDD[TestFeature], scalerModel: StandardScalerModel): RDD[Evaluation] = {
    val model = train(trainData.cache, scalerModel)
    predict(model, testData.cache, scalerModel)
  }

  private def toVector(feature: Feature, scalerModel: StandardScalerModel): Vector = scalerModel transform toVector(feature)

  private def toVector(feature: Feature): Vector = Vectors dense feature.coordinates.toArray
}

object MachineLearning extends MachineLearning
