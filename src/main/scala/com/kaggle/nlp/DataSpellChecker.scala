package com.kaggle.nlp

import com.kaggle.service.{ErrorModelService, LanguageModelService, SpellCheckerService}

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
// TODO: Implement P(w | c) - probability that correction c stands on its own - Language model
class DataSpellChecker(implicit val spellCheckerService: SpellCheckerService, languageModelService: LanguageModelService, errorModelService: ErrorModelService) extends Serializable {
  val MAX_CANDIDATES = 10

  val bestCandidateFinder = new BestCandidateFinder(languageModelService, errorModelService)

  /**
    * @param token token to be processed
    * @return List of tokens that are potential candidates.
    */
  def process(token: Token): List[Token] = {
    val w = token.value.toLowerCase
    val smallErrors = NlpUtils.smallErrorsFailSafe(w)
    val allCandidates = (smallErrors flatMap spellCheckerService.getMatches).distinct
    (allCandidates sortBy { x => languageModelService.logProbability(x) + errorModelService.logProbability(w, x) } takeRight MAX_CANDIDATES map Token union List(token)).distinct
  }

  import scala.collection.JavaConverters._
  def process(tokens: List[Token]): List[SpellCorrectedToken] = {
    val candidates = (tokens map { x => (process(x) map {
      _.value
    }).asJava
    }).asJava

    val originals = (tokens map { _.value.toLowerCase }).asJava
    bestCandidateFinder.findBest(candidates, originals).asScala.toList map Token
  }
}
