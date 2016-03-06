package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  */
class DataSemanticExtraction extends Serializable {
  def process(token: ClassifiedToken): CleanToken = CleanToken(token.originalValue, token.stemmedValue, token.tokenType, UNKNOWN_SEMANTIC_TYPE)

  def process(tokens: List[ClassifiedToken]): List[CleanToken] = tokens map process
}
