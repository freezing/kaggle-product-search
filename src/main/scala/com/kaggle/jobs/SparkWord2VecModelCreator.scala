package com.kaggle.jobs

import com.kaggle.nlp.DataLexer
import com.kaggle.service.SparkFileReader
import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.storage.StorageLevel

/**
  * Created by freezing on 16/03/16.
  */
object SparkWord2VecModelCreator extends App {
  val trainData = SparkFileReader.readTrainData("/train.csv").cache()
  val testData = SparkFileReader.readTestData("/test.csv").cache()

  val lexer = new DataLexer

  val trainWords = trainData flatMap { td => Iterable(lexer.tokenize(td.rawData.title) map { _.value }, lexer.tokenize(td.rawData.searchTerm.value) map { _.value })}
  val testWords = testData flatMap { td => Iterable(lexer.tokenize(td.title) map {_.value}, lexer.tokenize(td.searchTerm.value) map {_.value})}
  val descriptions = SparkFileReader.readTextFile("/product_descriptions.csv") map { line => lexer.tokenize(line) map {_.value} }
  val attributes = SparkFileReader.readTextFile("/attributes.csv") map { line => lexer.tokenize(line) map {_.value} }

  val words = (trainWords union testWords union descriptions union attributes repartition 8).persist(StorageLevel.MEMORY_ONLY)

  val wordVec = new Word2Vec
  val model = wordVec.fit(words)

  import com.kaggle.sc
  val data = model.getVectors map { case (k, v) => s"$k:${string(v)}" }
  sc.parallelize(data.toSeq, 1).saveAsTextFile("/home/freezing/kaggle/word2vec_model")

  private def string(v: Array[Float]): String = v mkString ","
}
