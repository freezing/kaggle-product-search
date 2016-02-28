package com.kaggle.nlp

/**
  * Created by freezing on 28/02/16.
  *
  * Correct spell errors in the tokens.
  * These should fix the following:
  * - batf -> bath; wrong letter
  * - sponge -> spounge; missing letter
  * - lightbulb -> light bulb; space error - in this case it is split in two tokens (order MUST be preserved)
  */
class DataSpellChecker extends Serializable {
  // TODO: Not implemented
  def process(token: Token): SpellCorrectedToken = token

  def process(tokens: List[Token]): List[SpellCorrectedToken] = tokens map process
}
