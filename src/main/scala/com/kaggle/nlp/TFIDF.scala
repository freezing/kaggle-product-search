package com.kaggle.nlp

/**
  * Created by freezing on 07/03/16.
  */
object TFIDF {
  case class TFIDFDocument(words: List[String])

  class TFIDF(documents: List[TFIDFDocument]) {
    private val cacheIdf: collection.mutable.HashMap[String, Double] = collection.mutable.HashMap.empty[String, Double]

    private def tf(word: String, document: TFIDFDocument): Double = (document.words count { x => x == word }).toDouble / document.words.size

    private def idf(word: String): Double = {
      if (!cacheIdf.contains(word)) {
        cacheIdf.put(word, Math.log(documents.size.toDouble / (1 + documentsContaining(word))))
      }
      cacheIdf(word)
    }

    private def documentsContaining(word: String): Int = documents count { d => d.words.contains(word) }

    def tfidf(word: String, document: TFIDFDocument): Double = tf(word, document) * idf(word)
  }

  def parseWords(line: String) = ("""[a-zA-Z]+""".r.findAllIn(line) filter { _.length > 0 } map { _.toLowerCase }).toList

  def parseDocument(lines: List[String]): TFIDFDocument = TFIDFDocument(lines flatMap { line => parseWords(line) })
}
