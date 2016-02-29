package com.kaggle.nlp

/**
  * Created by freezing on 29/02/16.
  */
package object attribute {
  sealed trait CleanAttributeName
  case object UNKNOWN extends CleanAttributeName
  case object BRAND extends CleanAttributeName
  case object MATERIAL extends CleanAttributeName
}
