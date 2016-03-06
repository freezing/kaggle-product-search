package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 06/03/16.
  */
class SearchCorrection {
  def correct(searchTerm: CleanTerm, title: CleanTerm): CleanTerm = {
    val tokens = searchTerm.tokens map { t =>
      findSimilar(t.stemmedValue, title.tokens) match {
        case Some(similar) => t.copy(stemmedValue = similar)
        case None => t
      }
    }
    CleanTerm(tokens, searchTerm.attributes)
  }

  private def findSimilar(s: String, tokens: List[CleanToken]): Option[String] = {
    tokens collectFirst { case t if NlpUtils.equal(s, t.stemmedValue) => t.stemmedValue }
  }
}
