package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  */
class DataLexer extends Serializable {
  // TODO: Not implemented
  def tokenize(data: String): List[Token] = data.split("[\\s-,/]+").toList map { x => Token(x) }
}
