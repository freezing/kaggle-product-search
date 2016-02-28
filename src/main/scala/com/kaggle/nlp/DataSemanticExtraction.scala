package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  */
class DataSemanticExtraction {
  def process(token: ClassifiedToken): CleanToken = ???

  def process(tokens: List[ClassifiedToken]): List[CleanToken] = tokens map process
}
