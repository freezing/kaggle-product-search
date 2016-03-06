package com.kaggle.nlp

import com.kaggle.model.CleanTerm

/**
  * Created by freezing on 06/03/16.
  */
class TokenMerger {
  // TODO: Better implenentation would look for any two pairs and merge them,
  // but that would require figuring out the position later (maybe)
  // and it's harder to implement
  def process(toMerge: CleanTerm, against: CleanTerm): CleanTerm = {
    //toMerge sliding 2
    toMerge
  }
}
