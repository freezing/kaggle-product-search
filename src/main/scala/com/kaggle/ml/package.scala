package com.kaggle

import com.kaggle.ml.decisiontree.{DecisionTreeFeatures, DecisionTreeFeature}

/**
  * Created by freezing on 29/02/16.
  */
package object ml {
  implicit class VectorMultiplication(a: List[Double]) {
    def X (b: List[Double]) = {
      if (a.length != b.length) throw new IllegalArgumentException("Different lengths")
      (a zip b map { case (c, d) => c * d }).sum
    }
  }

  case class LinearRegressionFeature(coordinates: List[Double])
  case class Feature(linearRegressionFeature: LinearRegressionFeature, decisionTreeFeatures: DecisionTreeFeatures)
  case class LabeledFeature(feature: LinearRegressionFeature, label: Double)
}
