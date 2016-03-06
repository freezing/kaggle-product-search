package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 06/03/16.
  */
object TokenMergerTest extends App {
  val empty = Map.empty[SemanticType, List[CleanToken]]
  val title = DataCleaner.process("LG Electronics 12,000 BTU 230/208-Volt WindowUnit Air Conditioner with Cool, Heat and Remote")
  val search = DataCleaner.process("A/c window unit")
  val titleTerm = CleanTerm(title, empty)

  println(title)
  println(search)

  val correction = new SearchCorrection
  val merger = new TokenMerger

  val correctedSearch = correction.correct(CleanTerm(search, empty), CleanTerm(title, empty))
  val mergedSearch = merger.process(correctedSearch, titleTerm)
  val mergedTitle = merger.process(titleTerm, mergedSearch)

  println(correctedSearch)
  println(mergedSearch)
  println(mergedTitle)
}
