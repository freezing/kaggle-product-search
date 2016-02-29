package com.kaggle

import com.kaggle.ml.Feature
import com.kaggle.model.{Id, Relevance}

/**
  * Created by freezing on 2/25/16.
  */
package object feature {
  case class TrainFeature(feature: Feature, relevance: Relevance, id: Id)
  case class TestFeature(feature: Feature, id: Id)
}
