package com.kaggle.nlp

/**
  * Created by freezing on 01/03/16.
  */
object NlpUtils {
  def smallErrors(w: String): List[String] = smallErrors(w, 1) union smallErrors(w, 2)

  // TODO: Add memoization
  def smallErrors(w: String, d: Int): List[String] = {
    if (d > w.length) throw new IllegalArgumentException(s"Distance $d is greater than word length: ${w.length}")
    d match {
      case 0 => List(w)
      case 1 => smallErrors1(w)
      case k => smallErrors1(w) flatMap { s => smallErrors(s, d - 1) }
    }
  }

  def smallErrors1(w: String): List[String] = (0 until w.length map { idx => w.substring(0, idx) + w.substring(idx + 1) }).toList
}
