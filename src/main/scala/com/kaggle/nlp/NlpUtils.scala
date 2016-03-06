package com.kaggle.nlp

/**
  * Created by freezing on 01/03/16.
  */
object NlpUtils {
  def smallErrorsFailSafe(w: String): List[String] = {
    if (w.length >= 3 && w.length <= 4) {
      NlpUtils.smallErrors(w, 0) union NlpUtils.smallErrors(w, 1)
    }
    else if (w.length >= 5) NlpUtils.smallErrors(w, 0) union NlpUtils.smallErrors(w, 1) //TODO: Should we use 2?
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

  def equal(w: String, s: String): Boolean = {
    val wNormalized = removeDuplicates(w)
    val sNormalized = removeDuplicates(s)

    if (wNormalized == sNormalized) true
    else if (s.length <= 3 || w.length <= 3) w == s
    else {
      // Get all letters (union)
      val wCounts = letterCounts(w)
      val sCounts = letterCounts(s)
      val keys = wCounts.keys.toSet union sCounts.keys.toSet
      val differenceCount = (keys.toList map { k => Math.abs(wCounts(k) - sCounts(k)) }).sum
      val difference = differenceCount.toDouble / Math.max(w.length, s.length)
      val lcsMatchRatio = JavaNlpUtils.lcsMatch(w, s).toDouble / Math.max(w.length, s.length)
      (lcsMatchRatio > 0.5 && difference < 0.35) || difference < 0.3
    }
  }

  def letterCounts(s: String): Map[Char, Int] = s groupBy { x => x } map { case (k, v) => k -> v.length } withDefaultValue 0

  def removeDuplicates(s: String): String = {
    // Mutable
    var takeCurrent = true
    s sliding 2 map { w =>
      val ret = {
        if (takeCurrent) w.head.toString
        else ""
      }
      takeCurrent = w.head != w.last
      ret
    } mkString ""
  }
}
