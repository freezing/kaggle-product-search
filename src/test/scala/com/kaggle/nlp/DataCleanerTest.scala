package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 06/03/16.
  */
object DataCleanerTest extends App {
  val title = DataCleaner.process("Con-Tact Creative Covering 18 in. x 75 ft. Rosebud Multipurpose Shelf Liner, 1 Roll")
  val search = DataCleaner.process("duck shelf liner")

  println(title)
  println(search)

  val correction = new SearchCorrection

  val empty = Map.empty[SemanticType, List[CleanToken]]
  println(correction.correct(CleanTerm(search, empty), CleanTerm(title, empty)))
}
