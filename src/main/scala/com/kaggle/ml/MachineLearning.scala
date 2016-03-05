package com.kaggle.ml

import com.kaggle.feature.{TestFeature, TrainFeature}
import com.kaggle.ml.decisiontree.{DecisionTreeBuilder, DecisionTree, DecisionTreeNodeId}
import com.kaggle.model.{Relevance, Evaluation}

/**
  * Created by freezing on 29/02/16.
  */
class MachineLearning(numberOfSteps: Int, alpha: Double, lambda: Double, normalize: Boolean, threshold: Double) {
  var decisionTree: DecisionTree = null
  val linearRegressions = collection.mutable.HashMap.empty[DecisionTreeNodeId, LinearRegression]

  def train(trainDataFeatures: List[TrainFeature]): Unit = {
    // Create DecisionTree
    val decisionTreeData = trainDataFeatures map { _.feature.decisionTreeFeatures }
    decisionTree = new DecisionTreeBuilder(decisionTreeData).build()

    // Distribute data by DecisionTreeNodeId
    val trainData = trainDataFeatures groupBy { x => decisionTree.findLeafId(x.feature.decisionTreeFeatures) }

    // Feature size is the same for all
    val featureSize = trainDataFeatures.head.feature.linearRegressionFeature.coordinates.size

    println(s"Total data: ${trainDataFeatures.length}")
    trainData map { case (k, v) =>
      println(s"Distribution for node: $k is: ${v.length}")
      val linearRegression = new LinearRegression(featureSize, numberOfSteps, alpha, lambda, normalize, threshold)
      val labeledFeatures = v map { case TrainFeature(feature, relevance, id) => LabeledFeature(feature.linearRegressionFeature, relevance.value) }
      linearRegression.train(labeledFeatures)
      linearRegressions.put(k, linearRegression)
    }
  }

  def predict(testDataFeatures: List[TestFeature]): List[Evaluation] = {
    val predictions = testDataFeatures map { x =>
      val nodeId = decisionTree.findLeafId(x.feature.decisionTreeFeatures)
      val linearRegressionFeature = x.feature.linearRegressionFeature
      linearRegressions.get(nodeId).get.predict(linearRegressionFeature)
    }
    testDataFeatures zip predictions map { case (TestFeature(_, id), prediction) => Evaluation(id, Relevance(prediction)) }
  }

  def RMS(labeledData: List[TrainFeature]): Double = {
    Math.sqrt((labeledData map { case TrainFeature(feature, Relevance(label), id) =>
      val nodeId = decisionTree.findLeafId(feature.decisionTreeFeatures)
      val linearRegression = linearRegressions.get(nodeId).get
      val prediction = linearRegression.predict(feature.linearRegressionFeature)
      val error = prediction - label
      error * error
    }).sum / labeledData.length)
  }
}