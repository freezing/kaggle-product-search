package com.kaggle.nlp

/**
  * Created by freezing on 02/03/16.
  */
object DataLexerTest extends App {
  val lexer = new DataLexer

  println(lexer.tokenize("123 nikola stojiljko,v)ic 123lkj12NIKS322NIKOLA123AdD54kj12)3l12j3lasdddd-ddddd123klj"))
}
