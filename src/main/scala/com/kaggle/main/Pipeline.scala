package com.kaggle.main

import com.kaggle.feature.extraction.SimpleFeatureExtractor
import com.kaggle.nlp.DataCleaner
import com.kaggle.service.SparkFileReader

/**
  * Created by freezing on 2/25/16.
  *
  * 1. Read train and test
  * 2. Clean data
  * 3. Extract features
  * 4. Machine Learning
  * 5. Save results
  */
object Pipeline extends App with Serializable {
  // 1. Read data
  val trainData = SparkFileReader.readTrainData("/train.csv")
  val testData = SparkFileReader.readTestData("/test.csv")

  // 2. Clean data
  val cleanTrainData = DataCleaner.processTrainData(trainData)
  val cleanTestData = DataCleaner.processTestData(testData)

  // 3. Extract Features
  val trainDataFeatures = SimpleFeatureExtractor.processTrainData(cleanTrainData)
  val testDataFeatures = SimpleFeatureExtractor.processTestData(cleanTestData)

//  val path = Paths.get("/Users/freezing/Desktop/submit.csv")
//  new SubmitCsvCreator(evaluations).save(path)
}
