package com.kaggle

/**
  * Created by freezing on 28/02/16.
  */
package object nlp {
  // Word Types
  sealed trait TokenType
  sealed trait WordType extends TokenType

  case object NUMBER extends TokenType
  case object NOUN extends TokenType

  // Semantic Types
  sealed trait SemanticType
  case object UNKNOWN_SEMANTIC_TYPE extends SemanticType

  case object MATERIAL extends SemanticType

  case object INCH extends SemanticType
  case object FOOT extends SemanticType
  case object GALLON extends SemanticType
  case object CC extends SemanticType
  case object POUND extends SemanticType
  case object VOLT extends SemanticType
  case object AMP extends SemanticType
  case object CU_FT extends SemanticType
  case object SQ_FT extends SemanticType
  case object OZ extends SemanticType
  case object UNKNOWN_DIMENSION extends SemanticType

  case class Token(value: String) extends AnyVal
  case class StemmedToken(original: OriginalValue, stemmed: StemmedValue)
  case class ClassifiedToken(originalValue: OriginalValue, stemmedValue: StemmedValue, tokenType: TokenType)
  case class CleanToken(originalValue: OriginalValue, stemmedValue: StemmedValue, tokenType: TokenType, semanticType: SemanticType)

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
  implicit val termSemanticExtraction = new TermSemanticExtraction
}
