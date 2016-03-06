package com.kaggle.nlp

/**
  * Created by freezing on 06/03/16.
  */
object StringEqualsTest extends App {

  println(NlpUtils.equal("wheelbarow", "wheelbarrow"))
  println(NlpUtils.equal("lumionaire", "luminarie"))
  println(NlpUtils.equal("paint", "psint"))
  println(NlpUtils.equal("epilator", "epilateur"))
  println(NlpUtils.equal("milwaukee", "milwakiee"))
  println(NlpUtils.equal("marrazi", "marazzi"))
  println(NlpUtils.equal("", ""))

  println(NlpUtils.removeDuplicates("marrassssagekkeee"))
}
