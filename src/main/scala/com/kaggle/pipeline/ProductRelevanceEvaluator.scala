package com.kaggle.pipeline

import com.kaggle.SubmitCsvCreator
import com.kaggle.feature.extraction.SimpleFeatureExtractor
import com.kaggle.ml.LinearRegression
import com.kaggle.model.{TrainItem, TestItem, Id, Evaluation}

/**
  * Created by freezing on 2/25/16.
  *
  */

//TODO: Change Map[Id, Item] to List[(Id, Item)] because there might be duplicate keys
class ProductRelevanceEvaluator(trainData: Map[Id, TrainItem], testData: Map[Id, TestItem]) {
  val featureExtractor = SimpleFeatureExtractor
  val linearRegression = new LinearRegression

  def evaluate(): List[Evaluation] = {
    val trainingFeatures = trainData.toList map { case (id, item) => (featureExtractor.extract(item.data), item.relevance) }

    // TODO: How scala knows there aren't same ids
    val testFeatures = testData.toList map { case (id, item) => (id, featureExtractor.extract(item)) }

    linearRegression.train(trainingFeatures)
    linearRegression.evaluate(testFeatures)
  }
}
