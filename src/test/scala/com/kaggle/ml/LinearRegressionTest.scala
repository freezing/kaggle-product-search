package com.kaggle.ml

import scala.util.Random

/**
  * Created by freezing on 29/02/16.
  */
object LinearRegressionTest {
  def f(a: Int, b: Int, c: Int): Double = 10 * a + 13 * b - 24 * c + 100

  // TODO: Add scala unit test dependency in sbt and run this as test
  def main(args: Array[String]) {
    val lr = new LinearRegression(3, 1000, 0.1, 0.1, true, 0.1)

    val rnd = new Random

    val data = (0 to 40000 map { x=>
      val a = rnd.nextInt(10000)
      val b = rnd.nextInt(10000)
      val c = rnd.nextInt(10000)
      val res = f(a, b, c)
      //      println(s"Train($a, $b) = $res")
      LabeledFeature(LinearRegressionFeature(List(a, b, c)), res)
    }).toList

    lr.train(data)
    lr.thetas foreach println

    val RMS = Math.sqrt((0 to 1000 map { x =>
      val a = rnd.nextInt(20000)
      val b = rnd.nextInt(20000)
      val c = rnd.nextInt(20000)
      val y = lr.predict(LinearRegressionFeature(List(a, b, c)))
      val error = y - f(a, b, c)
      error * error
      //          println(s"$a ~ $b = ${lr.predict(Feature(List(a, b, c)))}    expected: ${f(a, b, c)}")
    }).sum / 1000.0)

    println("RMS = " + RMS)
  }
}
