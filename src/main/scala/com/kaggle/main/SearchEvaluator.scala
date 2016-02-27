package com.kaggle.main

import java.nio.file.Paths

import com.kaggle.SubmitCsvCreator
import com.kaggle.pipeline.ProductRelevanceEvaluator
import com.kaggle.service.{TestItemService, TrainItemService}

/**
  * Created by freezing on 2/25/16.
  */
object SearchEvaluator extends App with Serializable {
  val evaluations = new ProductRelevanceEvaluator(TrainItemService.trainItems, TestItemService.testItems).evaluate()

  val path = Paths.get("/Users/freezing/Desktop/submit.csv")
  new SubmitCsvCreator(evaluations).save(path)
}
