package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  */
class DataLexer {
  // TODO: Not implemented
  def tokenize(data: String): List[Token] = data.split(" ").toList map { x => Token(x) }
}
