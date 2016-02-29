package com.kaggle

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


  case class Feature(coordinates: List[Double])
  case class LabeledFeature(feature: Feature, label: Double)
}
