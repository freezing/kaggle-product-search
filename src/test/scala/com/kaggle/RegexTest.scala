package com.kaggle

/**
  * Created by freezing on 05/03/16.
  */
object RegexTest extends App {
  val Regex = """(nd(xnd)*)""".r
  Regex.findAllMatchIn("ndndxndxndxnd  ndxndndxndndndnd") foreach { m =>
    println((m.start, m.end))
    println(m)
    println
  }
}
