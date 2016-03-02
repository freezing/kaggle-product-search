package com.kaggle.main

import java.nio.file.Paths

import com.kaggle.debug.{TrainDebugCsvCreator, DebugCsvCreator}
import com.kaggle.feature.TestFeature
import com.kaggle.file.SubmitCsvCreator
import com.kaggle.model.{TestItem, Relevance, Id, Evaluation}
import com.kaggle.feature.extraction.SimpleFeatureExtractor
import com.kaggle.ml.MachineLearning
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

  val debugTrainOutputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--debugTrainOutputPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No debug train output path specified") }

  val debugTestOutputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--debugTestOutputPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No debug test output path specified") }

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
  // TODO: Refactor so that featureSize is figured out in LinearRegression
  val machineLearning = new MachineLearning
  machineLearning.train(trainDataFeatures)
  val evaluations = machineLearning.predict(testDataFeatures)

  // Output debug train data
  val trainEvaluations = machineLearning.predict(trainDataFeatures map { feature => TestFeature(feature.feature, feature.id) })
  new TrainDebugCsvCreator(trainEvaluations, trainDataFeatures, trainData).save(debugTrainOutputPath)

  new DebugCsvCreator(evaluations, testDataFeatures, testData).save(debugTestOutputPath)

  println(s"RMS = ${machineLearning.RMS(trainDataFeatures)}")

  new SubmitCsvCreator(evaluations).save(outputPath)
}
