package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  * Stem and normalize the tokens.
  */
class DataStemmer {
  // TODO: Not implemented
  def process(token: Token): StemmedToken = StemmedToken(token.value, token.value.toLowerCase)

  def process(tokens: List[Token]): List[StemmedToken] = tokens map process
}
