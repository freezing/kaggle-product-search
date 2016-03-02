package com.kaggle.service

/**
  * Created by freezing on 02/03/16.
  */
object LanguageModelServiceTest extends App {
  val languageModelService = new LanguageModelService
  println(languageModelService.logProbability("is", "container"))
  println(languageModelService.logProbability("air", "conditioner"))

  println(languageModelService.logProbability("is"))
  println(languageModelService.logProbability("air"))
}
