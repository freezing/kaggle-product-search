package com.kaggle.pipeline

import com.kaggle.SubmitCsvCreator
import com.kaggle.feature.extraction.SimpleFeatureExtractor
import com.kaggle.ml.MachineLearning
import com.kaggle.model.{TrainItem, TestItem, Id, Evaluation}

/**
  * Created by freezing on 2/25/16.
  *
  */

//TODO: Change Map[Id, Item] to List[(Id, Item)] because there might be duplicate keys
class ProductRelevanceEvaluator(trainData: Map[Id, TrainItem], testData: Map[Id, TestItem]) extends Serializable {
  val featureExtractor = SimpleFeatureExtractor
  val machineLearning = new MachineLearning

  def evaluate(): List[Evaluation] = {
    val trainingFeatures = trainData.toList map { case (id, item) => (featureExtractor.extract(item.data), item.relevance) }

    // TODO: How scala knows there aren't same ids
    val testFeatures = testData.toList map { case (id, item) => (id, featureExtractor.extract(item)) }

    machineLearning.train(trainingFeatures)
    machineLearning.evaluate(testFeatures)
  }
}
