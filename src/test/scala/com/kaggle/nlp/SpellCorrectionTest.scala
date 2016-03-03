package com.kaggle.nlp

/**
  * Created by freezing on 02/03/16.
  */
object SpellCorrectionTest extends App {
  val lexer = new DataLexer
  val spellChecker = new DataSpellChecker
  printClean("airs conditiner")
  printClean("bathroom sybks")
  printClean("bathroom ssink")
  printClean("bathroom sunk")
  printClean("bathroom sink")
  printClean("toigets bruseh")
  printClean("toigets bruseh for blue chairs")
  printClean("toigets bruseh for kitchen")
  printClean("toigets bruseh that are nice")
  printClean("blue doors")

  printClean("Moen bathroom faucets")
  printClean("banbury")
  printClean("outdoor LED light bulb")
  printClean("ceiling fan canopie for flst ceiling")
  printClean("one handle moen bracket replacement")

  printClean("HDX 48 in. W x 72 in. H x 18 in. D Decorative Wire Chrome Finish Commercial Shelving Unit")
  printClean("hdx wire shelving")
  printClean("r19 insulation")
  printClean("PLATFORM FOR WASHERS")
  printClean("bazz lighting")
  printClean("BAZZ 10 ft. Multi-Color Self-Adhesive Cuttable Rope Lighting with Remote Control")

  printClean("closetmade")
  printClean("quikrete color")
  printClean("DEWALT VACCUM")
  printClean("hindges")
  printClean("chaise")
  printClean("ceadar")
  printClean("cedar plank")
  printClean("cedart board")
  printClean("milwakee M12")
  printClean("milwaukee m12")
  printClean("jeld wen aurora a5001")
  printClean("werner ladder")

  printClean("libht bulp")

  def printClean(s: String): Unit = println(s"Clean($s) = " + (spellChecker.process(lexer.tokenize(s)) map { _.value } mkString " "))
}
