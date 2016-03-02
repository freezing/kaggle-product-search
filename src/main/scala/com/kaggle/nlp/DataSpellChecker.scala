package com.kaggle.nlp

import com.kaggle.service.{LanguageModelService, SpellCheckerService}

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
class DataSpellChecker(implicit val spellCheckerService: SpellCheckerService, languageModelService: LanguageModelService) extends Serializable {
  val MAX_CANDIDATES = 30

  val bestCandidateFinder = new BestCandidateFinder(languageModelService)

  /**
    * @param token token to be processed
    * @return List of tokens that are potential candidates.
    */
  def process(token: Token): List[Token] = {
    val w = token.value.toLowerCase
    val smallErrors = NlpUtils.smallErrors(w)
    val allCandidates = (smallErrors flatMap spellCheckerService.getMatches).distinct
    (allCandidates sortBy languageModelService.logProbability) takeRight MAX_CANDIDATES map Token union List(token)
  }

  import scala.collection.JavaConverters._
  def process(tokens: List[Token]): List[SpellCorrectedToken] =
    bestCandidateFinder.findBest((tokens map { x => (process(x) map { _.value }).asJava }).asJava ).asScala.toList map Token

//  // TODO: Not implemented
//  def _process(token: Token): SpellCorrectedToken = {
//    val w = token.value.toLowerCase
//    if (w.length > 1) {
//      // Check if token exists with 0 distance
//      val w0 = spellCheckerService.getMatches(w)
//      if (w0.contains(w)) Token(w)
//      else {
//        // TODO: Figure out what is the best match if there are multiple choices
//        // For now just choose first one
//        (NlpUtils.smallErrorsFailSafe(w) sortBy {
//          _.length
//        }).reverse collectFirst {
//          case s if spellCheckerService.getMatches(s).nonEmpty => spellCheckerService.getMatches(s)
//        } match {
//          case Some(matches) =>
//            if (matches.nonEmpty) Token((matches.toList sortBy { x => Math.abs(x.length - w.length) }).head)
//            else Token(w)
//          case None => Token(w)
//        }
//      }
//    } else {
//      token
//    }
//  }
}
