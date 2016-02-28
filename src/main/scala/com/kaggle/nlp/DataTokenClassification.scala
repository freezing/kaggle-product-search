package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  */
class DataTokenClassification extends Serializable {
  // TODO: Not implemented
  def process(token: StemmedToken): ClassifiedToken = ClassifiedToken(token.original, token.stemmed, NOUN)

  def process(tokens: List[StemmedToken]): List[ClassifiedToken] = tokens map process
}
