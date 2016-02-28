package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  */
class DataTokenClassification {
  def process(token: StemmedToken): ClassifiedToken = ???

  def process(tokens: List[StemmedToken]): List[ClassifiedToken] = tokens map process
}
