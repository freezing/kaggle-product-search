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

  sealed trait DIMENSION extends SemanticType
  case object INCH extends DIMENSION
  case object FOOT extends DIMENSION
  case object GALLON extends DIMENSION
  case object CC extends DIMENSION
  case object POUND extends DIMENSION
  case object VOLT extends DIMENSION
  case object AMP extends DIMENSION
  case object CU_FT extends DIMENSION
  case object SQ_FT extends DIMENSION
  case object OZ extends DIMENSION
  case object UNKNOWN_DIMENSION extends DIMENSION

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
  implicit val searchCorrection = new SearchCorrection
}
