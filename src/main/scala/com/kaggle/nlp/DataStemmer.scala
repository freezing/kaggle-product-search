package com.kaggle.nlp

import java.util.logging.Logger

import com.kaggle.service.CsvReader

/**
  * Created by freezing on 28/02/16.
  * Stem and normalize the tokens.
  */
class DataStemmer extends Serializable {
  val logger = Logger.getLogger(getClass.getName)

  lazy val stems: Map[String, String] = {
    logger.info("Loading stems...")
    val loaded = (CsvReader.readTextFile("/stems.csv") map { x =>
      val cols = x.split(",")
      cols.head -> cols.last
    }).toMap withDefault((k: String) => k)
    logger.info("Loading stems has finished.")
    loaded
  }

  private def stemPlural(s: String): String = {
    if (s.endsWith("s")) s.substring(0, s.length - 1)
    else s
  }

  // TODO: Not implemented
  def process(token: Token): StemmedToken = {
    var s = stems(stemPlural(token.value.toLowerCase))

    s = s.replace("'","in.")
    s = s.replace("inches","in.")
    s = s.replace("inch","in.")

    s = s.replace("''","ft")
    s = s.replace("feet","ft")
    s = s.replace("foot","ft")

    s = s.replace("pounds","lb")
    s = s.replace("pound","lb")
    s = s.replace("lb","lb")

    s = s.replace("x","xby")
    s = s.replace("*","xby")
    s = s.replace("by","xby")

//    s = s.replace("x0"," xby 0")
//    s = s.replace("x1"," xby 1")
//    s = s.replace("x2"," xby 2")
//    s = s.replace("x3"," xby 3")
//    s = s.replace("x4"," xby 4")
//    s = s.replace("x5"," xby 5")
//    s = s.replace("x6"," xby 6")
//    s = s.replace("x7"," xby 7")
//    s = s.replace("x8"," xby 8")
//    s = s.replace("x9"," xby 9")
//    s = s.replace("0x","0 xby ")
//    s = s.replace("1x","1 xby ")
//    s = s.replace("2x","2 xby ")
//    s = s.replace("3x","3 xby ")
//    s = s.replace("4x","4 xby ")
//    s = s.replace("5x","5 xby ")
//    s = s.replace("6x","6 xby ")
//    s = s.replace("7x","7 xby ")
//    s = s.replace("8x","8 xby ")
//    s = s.replace("9x","9 xby ")

//    s = s.replace(" sq ft","sq.ft. ")
//    s = s.replace("sq ft","sq.ft. ")
//    s = s.replace("sqft","sq.ft. ")
//    s = s.replace(" sqft ","sq.ft. ")
//    s = s.replace("sq. ft","sq.ft. ")
//    s = s.replace("sq ft.","sq.ft. ")
//    s = s.replace("sq feet","sq.ft. ")
//    s = s.replace("square feet","sq.ft. ")
//
//    s = s.replace(" gallons ","gal. ")
//    s = s.replace(" gallon ","gal. ")
//    s = s.replace("gallons","gal.")
//    s = s.replace("gallon","gal.")
//    s = s.replace(" gal ","gal. ")
//    s = s.replace(" gal","gal.")
//
//    s = s.replace("ounces","oz.")
//    s = s.replace("ounce","oz.")
//    s = s.replace(" oz.","oz. ")
//    s = s.replace(" oz ","oz. ")
//
//    s = s.replace("centimeters","cm.")
//    s = s.replace(" cm.","cm.")
//    s = s.replace(" cm ","cm. ")
//
//    s = s.replace("milimeters","mm.")
//    s = s.replace(" mm.","mm.")
//    s = s.replace(" mm ","mm. ")
//
//    s = s.replace("°","deg. ")
//    s = s.replace("degrees","deg. ")
//    s = s.replace("degree","deg. ")
//
//    s = s.replace("volts","volt. ")
//    s = s.replace("volt","volt. ")
//
//    s = s.replace("watts","watt. ")
//    s = s.replace("watt","watt. ")
//
//    s = s.replace("ampere","amp. ")
//    s = s.replace("amps","amp. ")
//    s = s.replace(" amp ","amp. ")

    s = s.replace("whirpool","whirlpool")
    s = s.replace("whirlpoolga", "whirlpool")
    s = s.replace("whirlpoolstainless","whirlpool stainless")

    s = s.replace(",", "")
    StemmedToken(token.value, s)
  }

  def process(tokens: List[Token]): List[StemmedToken] = tokens map process
}
