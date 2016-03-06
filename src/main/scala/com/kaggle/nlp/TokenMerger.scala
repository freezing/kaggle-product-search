package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 06/03/16.
  */
class TokenMerger {
  // TODO: Better implementation would look for any two pairs and merge them,
  // but that would require figuring out the position later (maybe)
  // and it's harder to implement
  def process(toMerge: CleanTerm, against: CleanTerm): CleanTerm = {
    if (toMerge.tokens.isEmpty) toMerge
    else {
      // Add dummy token at the end (it will not be added anyway)
      val dummyToken = CleanToken("", "<dummy>", NOUN, UNKNOWN_SEMANTIC_TYPE)

      // Mutable part
      var lastMerged = false
      val tokens = ((toMerge.tokens ++ List(dummyToken)) sliding 2 map { case Seq(current, next) =>
        if (lastMerged) {
          lastMerged = false
          None
        } else if (canMerge(current, next, against.tokens)) {
          lastMerged = true
          Some(current.copy(stemmedValue = current.stemmedValue ++ next.stemmedValue))
        } else {
          lastMerged = false
          Some(current)
        }
      } collect { case Some(t) => t }).toList
      toMerge.copy(tokens = tokens)
    }
  }

  private def canMerge(current: CleanToken, next: CleanToken, tokens: List[CleanToken]): Boolean = {
    val merged = current.stemmedValue ++ next.stemmedValue
    (tokens count { t => NlpUtils.equal(merged, t.stemmedValue) }) > 0
  }
}
