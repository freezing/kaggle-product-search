package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 06/03/16.
  */
object SearchCorrectionTest extends App {
  val correction = new SearchCorrection
  val empty = Map.empty[SemanticType, List[CleanToken]]

  println(correction.correct(CleanTerm(List(CleanToken("", "drumel", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty),
    CleanTerm(List(CleanToken("", "dremel", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty)
  ))

  println(correction.correct(CleanTerm(List(CleanToken("", "screw", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty),
    CleanTerm(List(CleanToken("", "screwdriv", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty)
  ))

  println(correction.correct(CleanTerm(List(CleanToken("", "parts", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty),
    CleanTerm(List(CleanToken("", "spiral", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty)
  ))

  println(correction.correct(CleanTerm(List(CleanToken("", "atribute", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty),
    CleanTerm(List(CleanToken("", "attributes", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty)
  ))

  println(correction.correct(CleanTerm(List(CleanToken("", "flourescent", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty),
    CleanTerm(List(CleanToken("", "fluorescent", NOUN, UNKNOWN_SEMANTIC_TYPE)), empty)
  ))
}
