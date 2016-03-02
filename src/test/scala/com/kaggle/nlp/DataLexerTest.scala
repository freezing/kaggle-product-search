package com.kaggle.nlp

/**
  * Created by freezing on 02/03/16.
  */
object DataLexerTest extends App {
  val lexer = new DataLexer

  println(lexer.tokenize("123 nikola stojiljkovic 123lkj12l54kj123l12j3l123klj"))
}
