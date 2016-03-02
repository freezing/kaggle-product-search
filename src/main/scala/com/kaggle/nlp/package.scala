package com.kaggle

/**
  * Created by freezing on 28/02/16.
  */
package object nlp {
  // Word Types
  sealed trait WordType
  case object NOUN extends WordType

  // Semantic Types
  sealed trait SemanticType
  case object MATERIAL extends SemanticType

  case class Token(value: String) extends AnyVal
  case class StemmedToken(original: OriginalValue, stemmed: StemmedValue)
  case class ClassifiedToken(originalValue: OriginalValue, stemmedValue: StemmedValue, wordType: WordType)
  case class CleanToken(originalValue: OriginalValue, stemmedValue: StemmedValue, wordType: WordType, semanticType: SemanticType)

  case class Sentence(words: List[String])
  case class Bigram(w1: String, w2: String)

  type OriginalValue = String
  type StemmedValue = String
  type SpellCorrectedToken = Token

  // Create DataCleaner
  implicit val lexer = new DataLexer
  implicit val spellChecker = new DataSpellChecker
  implicit val stemmer = new DataStemmer
  implicit val tokenClassification = new DataTokenClassification
  implicit val semanticExtraction = new DataSemanticExtraction
}
