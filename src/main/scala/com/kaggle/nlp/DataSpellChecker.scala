package com.kaggle.nlp

import com.kaggle.service.SpellCheckerService

/**
  * Created by freezing on 28/02/16.
  *
  * Correct spell errors in the tokens.
  * These should fix the following:
  * - batf -> bath; wrong letter
  * - sponge -> spounge; missing letter
  * - lightbulb -> light bulb; space error - in this case it is split in two tokens (order MUST be preserved)
  * - 1x3 -> 1 x 3
  */
class DataSpellChecker(implicit val spellCheckerService: SpellCheckerService) extends Serializable {
  // TODO: Not implemented
  def process(token: Token): SpellCorrectedToken = {
    val w = token.value.toLowerCase
    if (w.length > 1) {
      // Check if token exists with 0 distance
      val w0 = spellCheckerService.getMatches(w)
      if (w0.contains(w)) Token(w)
      else {
        // TODO: Figure out what is the best match if there are multiple choices
        // For now just choose first one
        (NlpUtils.smallErrorsFailSafe(w) sortBy {
          _.length
        }).reverse collectFirst {
          case s if spellCheckerService.getMatches(s).nonEmpty => spellCheckerService.getMatches(s)
        } match {
          case Some(matches) =>
            if (matches.nonEmpty) Token((matches.toList sortBy { x => Math.abs(x.length - w.length) }).head)
            else Token(w)
          case None => Token(w)
        }
      }
    } else {
      token
    }
  }

  def process(tokens: List[Token]): List[SpellCorrectedToken] = tokens map process
}
