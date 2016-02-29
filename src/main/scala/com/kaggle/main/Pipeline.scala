package com.kaggle.main

import java.nio.file.Paths

import com.kaggle.SubmitCsvCreator
import com.kaggle.feature.extraction.SimpleFeatureExtractor
import com.kaggle.ml.MachineLearningCommons
import com.kaggle.nlp.DataCleaner
import com.kaggle.service.CsvReader

/**
  * Created by freezing on 2/25/16.
  *
  * 1. Read train and test
  * 2. Clean data
  * 3. Extract features
  * 4. Machine LearningR
  * 5. Save results
  */
object Pipeline extends App with Serializable {
  val outputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--outputPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No output path specified") }

  // 1. Read data
  val trainData = CsvReader.readTrainData("/train.csv")
  val testData = CsvReader.readTestData("/test.csv")

  // 2. Clean data
  val cleanTrainData = DataCleaner.processTrainData(trainData)
  val cleanTestData = DataCleaner.processTestData(testData)

  // 3. Extract Features
  val trainDataFeatures = SimpleFeatureExtractor.processTrainData(cleanTrainData)
  val testDataFeatures = SimpleFeatureExtractor.processTestData(cleanTestData)

  // 4. Machine Learning

  val evaluations = MachineLearningCommons.predict(testDataFeatures)

  new SubmitCsvCreator(evaluations).save(outputPath)
}
