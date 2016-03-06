package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 06/03/16.
  */
object TokenMergerTest extends App {
  val empty = Map.empty[SemanticType, List[CleanToken]]
  val title = DataCleaner.process("Con-Tact Creative screwdrivers Covering 18 in. x 75 ft. Rosebud Multipurpose Shelf Liner, 1 Roll")
  val search = DataCleaner.process("duck shelf liner screw driver")
  val titleTerm = CleanTerm(title, empty)

  println(title)
  println(search)

  val correction = new SearchCorrection
  val merger = new TokenMerger

  val correctedSearch = correction.correct(CleanTerm(search, empty), CleanTerm(title, empty))
  val mergedSearch = merger.process(correctedSearch, titleTerm)

  println(correctedSearch)
  println(mergedSearch)
}
