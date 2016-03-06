package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 05/03/16.
  */
// TODO: Handle "dd" -> DIMENSION DIMENSION such as "cu ft"
class TermSemanticExtraction {
  val Regex = """(nd(xnd)*)|(n(xn)*)""".r

  val semanticTypes = Map(
    "in" -> INCH,
    "amp" -> AMP,
    "btu" -> BTU,
    "oz" -> OZ,
    "lb" -> POUND,
    "volt" -> VOLT,
    "cc" -> CC,
    "gal" -> GALLON,
    "ft" -> FOOT,
    // TODO: Fix, not working with this kind of logic if it is made of two tokens
    "cuft" -> CU_FT,
    "sqft" -> SQ_FT
  ) withDefaultValue UNKNOWN_DIMENSION

  def process(tokens: List[CleanToken]): CleanTerm = {
    // Map tokens to string:
    // For each number yield 'n'
    // For each NOUN 'x' yield 'x'
    // For each NOUN yield 'w'
    val s = tokens map { token =>
      token.tokenType match {
        case NUMBER => 'n'
        case NOUN => token.stemmedValue match {
          case "x" | "by" => 'x'
          case "in" | "btu" | "a" | "amp" |  "ft" | "lb" | "amp"  | "volt" | "v" | "gal" | "oz" | "cu" | "cc" | "mm" | "year" => 'd'
          case _ => 'w'
        }
      }
    } mkString ""

    val matches = Regex.findAllMatchIn(s).toList

    val attributes = matches flatMap { m =>
      makeAttributes(tokens slice (m.start, m.end))
    } groupBy { _._1 } map { case (k, v) => k -> (v map { case (_, vv) => vv }) }

    val attributeIndexes = (matches flatMap { m => m.start until m.end }).toSet
    val newTokens = tokens.zipWithIndex collect { case (token, idx) if !attributeIndexes.contains(idx) => token }

    CleanTerm(newTokens, attributes)
  }

  private def makeAttributes(tokens: List[CleanToken]): List[(SemanticType, CleanToken)] = {
    val ret = (tokens filter { _.stemmedValue != "x" } sliding 2 collect { case Seq(t1, t2) if t1.tokenType == NUMBER =>
      semanticTypes(t2.stemmedValue) -> t1
    }).toList
    // TODO: Fix hack (this is for N x N case)
    if (tokens.last.tokenType == NUMBER) ret union List(UNKNOWN_DIMENSION -> tokens.last)
    else ret
  }
}
