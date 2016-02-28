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

  type OriginalValue = String
  type StemmedValue = String
  type SpellCorrectedToken = Token
}
