package com.kaggle.service

/**
  * Created by freezing on 03/03/16.
  */
class ErrorModelService {
  // TODO: TO be implemented, for now only dummy implementation
  def logProbability(input: String, correction: String): Double = {
    if (input == correction) {
      Math.log(0.7)
    } else {
      Math.log(0.3)
    }
  }
}
