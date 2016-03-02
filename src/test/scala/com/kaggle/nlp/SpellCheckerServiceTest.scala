package com.kaggle.nlp

import com.kaggle.service.SpellCheckerService

/**
  * Created by freezing on 02/03/16.
  */
object SpellCheckerServiceTest extends App {
  val service = new SpellCheckerService
  println(service.getMatches("conditner"))
}
