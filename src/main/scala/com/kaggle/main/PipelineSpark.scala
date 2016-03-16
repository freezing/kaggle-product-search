package com.kaggle.main

import com.github.fommil.netlib.BLAS
import com.kaggle.model.Relevance
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import com.kaggle.nlp.{DataLexer}
import com.kaggle.service.{SparkFileReader}
import org.apache.spark.mllib.feature.{Word2Vec}
import org.apache.spark.storage.StorageLevel

/**
  * Created by freezing on 2/25/16.
  *
  * 1. Read train and test
  * 2. Clean data
  * 3. Extract features
  * 4. Machine LearningR
  * 5. Save results
  */
object PipelineSpark extends App with Serializable {
  def cosineSimilarity(v1: Array[Float], v2: Array[Float]): Double = {
    val n = v1.length
    val norm1 = blas.snrm2(n, v1, 1)
    val norm2 = blas.snrm2(n, v2, 1)
    if (norm1 == 0 || norm2 == 0) return 0.0
    blas.sdot(n, v1, 1, v2,1) / norm1 / norm2
  }
//  val outputPath = args.toSeq sliding 2 collectFirst {
//    case Seq("--outputPath", path) => Paths.get(path)
//  } getOrElse { throw new Exception("No output path specified") }

  // 1. Read data
  val trainData = SparkFileReader.readTrainData("/train.csv").cache()
  val testData = SparkFileReader.readTestData("/test.csv").cache()

  val lexer = new DataLexer

  val trainWords = trainData flatMap { td => Iterable(lexer.tokenize(td.rawData.title) map { _.value }, lexer.tokenize(td.rawData.searchTerm.value) map { _.value })}
  val testWords = testData flatMap { td => Iterable(lexer.tokenize(td.title) map {_.value}, lexer.tokenize(td.searchTerm.value) map {_.value})}
  val descriptions = SparkFileReader.readTextFile("/product_descriptions.csv") map { line => lexer.tokenize(line) map {_.value} }
  val attributes = SparkFileReader.readTextFile("/attributes.csv") map { line => lexer.tokenize(line) map {_.value} }

  val words = (trainWords union testWords union descriptions union attributes repartition 8).persist(StorageLevel.MEMORY_AND_DISK)

  val wordVec = new Word2Vec
  val model = wordVec.fit(words)
  words.unpersist()

  val blas = BLAS.getInstance()
  val train = trainData map { t =>
    val title = getVector(t.rawData.title)
    val searchTerm = getVector(t.rawData.searchTerm.value)
    val similarity = cosineSimilarity(title, searchTerm)
    val v = Vectors.dense(similarity)
    LabeledPoint(t.relevance.value, v)
  }

  train.cache()

  val test = testData map { t =>
    val similarity = {
      try {
        val title = model.transform(t.title).toArray map {
          _.toFloat
        }
        val searchTerm = model.transform(t.searchTerm.value).toArray map {
          _.toFloat
        }
        cosineSimilarity(title, searchTerm)
      } catch {
        case e: Exception => 0.0
      }
    }
    Vectors.dense(similarity)
  } repartition 4

  val ml = LinearRegressionWithSGD.train(train, 100)
  val predictions = ml.predict(test) zip testData.map { _.id.value } map { case (k, v) => s"$v,${clamp(k, 1, 3)}" }
  predictions.repartition(1).saveAsTextFile("/home/freezing/sparkKaggle")

  def clamp(k: Double, min: Double, max: Double): Double = Math.min(max, Math.max(min, k))

  def getVector(s: String): Array[Float] = {
    val t = lexer.tokenize(s) map { _.value } map model.transform
    val zeroes = (0 to t.head.size).toArray map { x => 0.0 }
    (t map { _.toArray }).foldLeft(zeroes)((v1: Array[Double], v2: Array[Double]) => v1.zip(v2) map { case (a, b) => a + b }) map { x =>
      (x / t.size).toFloat
    }
  }
//
//  val input = Iterable("goggles", "glasses", "lenses", "cabinet", "drawer", "wood", "timber", "clutch", "impact")
//  input foreach { w =>
//    println(s"Synonyms for word: $w")
//    println("_______________________")
//    model.findSynonyms(w, 10) foreach println
//    println("_______________________")
//    println()
//    println()
//  }



//
//  // 2. Clean data
//  val cleanTrainData = DataCleaner.processTrainDataSpark(trainData)
//  val cleanTestData = DataCleaner.processTestDataSpark(testData)
//
//  // 3. Extract Features
//  val trainDataFeatures = SimpleFeatureExtractor.processTrainDataSpark(cleanTrainData).cache()
//  val testDataFeatures = SimpleFeatureExtractor.processTestDataSpark(cleanTestData)
//
//  // 4. Machine Learning
//  // TODO: Change to spark
//  MachineLearningCommons.train(trainDataFeatures.toLocalIterator.toList)
//  val evaluations = MachineLearningCommons.predict(testDataFeatures.toLocalIterator.toList)
//
//  new SubmitCsvCreator(evaluations).save(outputPath)
}
