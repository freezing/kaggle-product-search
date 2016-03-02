package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  */
class DataLexer extends Serializable {
  def tokenize(data: String): List[Token] =
    (data.split("[\\s-,/\\(\\)\\.:]+") filter { _.length > 0 }).toList flatMap { x =>
      // Split letters and numbers in between
      """\d+|\D+""".r.findAllIn(x) map { t => Token(t) }
    }
}
