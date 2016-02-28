package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  * Stem and normalize the tokens.
  */
class DataStemmer {
  def process(token: Token): StemmedToken = ???

  def process(tokens: List[Token]): List[StemmedToken] = tokens map process
}
