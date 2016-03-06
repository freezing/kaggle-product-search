package com.kaggle.jobs

import java.nio.file.Paths

import com.kaggle.file.ProductTypeProductMergerFileCreator
import com.kaggle.main.Pipeline._
import com.kaggle.service.{AttributeService, CsvReader}

/**
  * Created by freezing on 06/03/16.
  */
object ProductTypeProductMerger extends App {
  val outputPath = args.toSeq sliding 2 collectFirst {
    case Seq("--outputPath", path) => Paths.get(path)
  } getOrElse { throw new Exception("No output path specified") }

  val attributeService = new AttributeService

  val trainData = CsvReader.readTrainData("/train.csv")
  val trainDataWithProductType = trainData map { item =>
    val ptype = attributeService.get(item.rawData.productId) collectFirst { case x if x.name.original.toLowerCase.contains("product type") => x }
    ptype -> item
  } collect { case (Some(k), v) => k.value.toLowerCase -> v } sortBy { _._1 }

  new ProductTypeProductMergerFileCreator(trainDataWithProductType).save(outputPath)
}
