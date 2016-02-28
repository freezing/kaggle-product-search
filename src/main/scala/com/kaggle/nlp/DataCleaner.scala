package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  * 1. DataLexer - split data in the list of tokens
  * 2. DataSpellChecker - find spell errors and fix them
  * 3. DataStemmer - stem the tokens
  * 4. DataTokenClassification - classify tokens by word type such as noun, verb, ...
  * 5. DataSemanticExtraction - extract semantics for each token such as: color, product type, brand, material, ...
  */
class DataCleaner(implicit val dataLexer: DataLexer, dataSpellChecker: DataSpellChecker, dataStemmer: DataStemmer,
                  dataTokenClassification: DataTokenClassification, dataSemanticExtraction: DataSemanticExtraction) {
  def process(data: String): List[CleanToken] = {
    dataLexer.tokenize(data) map
      dataSpellChecker.process map
      dataStemmer.process map
      dataTokenClassification.process map
      dataSemanticExtraction.process
  }
}
