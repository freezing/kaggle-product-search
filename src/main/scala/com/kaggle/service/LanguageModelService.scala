package com.kaggle.service

import java.util.logging.Logger

import com.kaggle.nlp.Bigram

import scala.collection.mutable

/**
  * Created by freezing on 02/03/16.
  */
class LanguageModelService {
  val logger = Logger.getLogger(getClass.getName)

  lazy val wordCounts = {
    // Use mutable map to read in memory counts
    val m = new collection.mutable.HashMap[String, Int]
    CsvReader.readTextFile("/ngrams1.csv") map { line =>
      val cols = line.split(",")
      val w = cols.head
      val count = cols.last.toInt

      if (m.contains(w)) throw new IllegalStateException(s"Key $w already exists!")
      m.put(w, count)
    }
    m.toMap
  }

  lazy val bigramCounts = {
    // Use mutable map to read in memory counts
    val m = new mutable.HashMap[Bigram, Int]
    CsvReader.readTextFile("/ngrams2.csv") map { line =>
      val cols = line.split(",")
      val bigram = Bigram(cols.head, cols(1))
      val count = cols.last.toInt

      if (m.contains(bigram)) throw new IllegalStateException(s"Key $bigram already exists!")
      m.put(bigram, count)
    }
    m.toMap
  }

  lazy val totalWordCounts = (wordCounts map { case (k, v) => v }).sum
  lazy val totalBigramCounts = (bigramCounts map { case (k, v) => v }).sum

  // Use logarithms so we wouldn't have to worry about floating point precission and not use BigDecimal
  lazy val wordLogProbabilities = wordCounts map { case (k, v) => (k, Math.log(v) - Math.log(totalWordCounts)) }
  lazy val bigramLogProbabilities = bigramCounts map { case (k, v) => (k, Math.log(v) - Math.log(totalBigramCounts)) }

  def logProbability(w: String) = wordLogProbabilities(w)
  def logProbability(w1: String, w2: String) = bigramLogProbabilities(Bigram(w1, w2))
}
