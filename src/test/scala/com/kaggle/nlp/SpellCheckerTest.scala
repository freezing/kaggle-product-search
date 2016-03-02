package com.kaggle.nlp

/**
  * Created by freezing on 02/03/16.
  */
// tODO: Have unit tests
object SpellCheckerTest extends App {
  val spellChecker = new DataSpellChecker
  println(spellChecker.process(Token("conditiner")))
  println(spellChecker.process(Token("air")))
  println(spellChecker.process(Token("airo")))
  println(spellChecker.process(Token("lifht")))
  println(spellChecker.process(Token("ssink")))
  println(spellChecker.process(Token("sunk")))
}
