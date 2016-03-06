package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  */
class DataTokenClassification extends Serializable {
  // TODO: Not implemented
  def process(token: StemmedToken): ClassifiedToken = {
    val tokenType = {
      if (NlpUtils.isNumber(token.stemmed)) NUMBER
      else NOUN
    }
    ClassifiedToken(token.original, token.stemmed, tokenType)
  }

  def process(tokens: List[StemmedToken]): List[ClassifiedToken] = tokens map process
}
