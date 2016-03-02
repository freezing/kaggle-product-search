package com.kaggle.nlp

/**
  * Created by freezing on 02/03/16.
  */
object SpellCorrectionTest extends App {
  val lexer = new DataLexer
  val spellChecker = new DataSpellChecker

  printClean("airs conditiner")
  printClean("bathroom ssink")

  def printClean(s: String): Unit = println(s"Clean($s) = " + (spellChecker.process(lexer.tokenize(s)) map { _.value } mkString " "))
}
