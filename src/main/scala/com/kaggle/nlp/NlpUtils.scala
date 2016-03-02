package com.kaggle.nlp

/**
  * Created by freezing on 01/03/16.
  */
object NlpUtils {
  def smallErrorsFailSafe(w: String): List[String] = {
//    if (w.length >= 5 && w.length <= 5) NlpUtils.smallErrors(w, 1)
    if (w.length >= 5) NlpUtils.smallErrors(w, 0) union NlpUtils.smallErrors(w, 1) //TODO: FIgure out if want to use 2
    else List(w)
  }

  def smallErrors(w: String): List[String] = smallErrors(w, 1) union smallErrors(w, 2)

  // TODO: Add memoization
  def smallErrors(w: String, d: Int): List[String] = {
    if (d > w.length) throw new IllegalArgumentException(s"Distance $d is greater than word length: ${w.length}")
    d match {
      case 0 => List(w)
      case 1 => smallErrors1(w)
      case k =>
        val se1 = smallErrors1(w)
        se1 union (se1 flatMap { s => smallErrors(s, k - 1) })
    }
  }

  def smallErrors1(w: String): List[String] = (0 until w.length map { idx => w.substring(0, idx) + w.substring(idx + 1) }).toList

  def isNumber(s: String): Boolean = s forall Character.isDigit
}
