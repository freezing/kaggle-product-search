package com.kaggle.file

import com.kaggle.model.ProductId

/**
  * Created by freezing on 07/03/16.
  */
class TfidfFileCreator(tfidfs: Map[(ProductId, String), Double]) extends FileCreator {

  protected def makeContents: String = {
    tfidfs map { case ((k, v), d) => s"$k,$v,$d" } mkString "\n"
  }
}
