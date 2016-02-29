package com.kaggle.main

import java.nio.file.Paths

import com.kaggle.SubmitCsvCreator
import com.kaggle.feature.extraction.SimpleFeatureExtractor
import com.kaggle.ml.MachineLearningCommons
import com.kaggle.nlp.DataCleaner
import com.kaggle.service.SparkFileReader

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
  val outputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--outputPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No output path specified") }

  // 1. Read data
  val trainData = SparkFileReader.readTrainData("/train.csv")
  val testData = SparkFileReader.readTestData("/test.csv")

  // 2. Clean data
  val cleanTrainData = DataCleaner.processTrainDataSpark(trainData)
  val cleanTestData = DataCleaner.processTestDataSpark(testData)

  // 3. Extract Features
  val trainDataFeatures = SimpleFeatureExtractor.processTrainDataSpark(cleanTrainData).cache()
  val testDataFeatures = SimpleFeatureExtractor.processTestDataSpark(cleanTestData)

  // 4. Machine Learning
  MachineLearningCommons.train(trainDataFeatures.toLocalIterator.toList)
  val evaluations = MachineLearningCommons.predict(testDataFeatures.toLocalIterator.toList)

  new SubmitCsvCreator(evaluations).save(outputPath)
}
