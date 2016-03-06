package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 06/03/16.
  */
object DataCleanerTest extends App {
//  val title = DataCleaner.process("Leviton 15 Amp Single-Pole Toggle Switch - Ivory")
//  val search = DataCleaner.process("15 a ivory s. p switch")

  val title = DataCleaner.process("LG Electronics 12,000 BTU 230/208-Volt Window Air Conditioner with Cool, Heat and Remote")
  val search = DataCleaner.process("A/c window unit")

  println(title)
  println(search)

  val correction = new SearchCorrection

  val empty = Map.empty[SemanticType, List[CleanToken]]
  println(correction.correct(CleanTerm(search, empty), CleanTerm(title, empty)))
}
