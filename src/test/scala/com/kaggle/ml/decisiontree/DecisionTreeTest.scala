package com.kaggle.ml.decisiontree

/**
  * Created by freezing on 03/03/16.
  */
object DecisionTreeTest {
  def main(args: Array[String]) {
    val data = collection.mutable.MutableList(
      DecisionTreeFeatures(List(0.0, 1.0, 1.0) map DecisionTreeFeature))
    data += DecisionTreeFeatures(List(0.0, 1.0, 2.0) map DecisionTreeFeature)
    data += DecisionTreeFeatures(List(1.0, 2.0, 2.0) map DecisionTreeFeature)
    data += DecisionTreeFeatures(List(1.0, 2.0, 3.0) map DecisionTreeFeature)
    data += DecisionTreeFeatures(List(2.0, 2.0, 3.0) map DecisionTreeFeature)

    val tree = new DecisionTreeBuilder(data.toList).build()
    println(tree.findLeafId(DecisionTreeFeatures(List(0.0, 1.0, 1.0) map DecisionTreeFeature))) // Expect 3
    println(tree.findLeafId(DecisionTreeFeatures(List(0.0, 1.0, 2.0) map DecisionTreeFeature))) // Expect 4
    println(tree.findLeafId(DecisionTreeFeatures(List(0.0, 1.0, 1.7) map DecisionTreeFeature))) // Expect 4
    println(tree.findLeafId(DecisionTreeFeatures(List(1.0, 2.0, 2.0) map DecisionTreeFeature))) // Expect 7
    println(tree.findLeafId(DecisionTreeFeatures(List(1.0, 2.0, 3.0) map DecisionTreeFeature))) // Expect 8
    println(tree.findLeafId(DecisionTreeFeatures(List(2.0, 2.0, 3.0) map DecisionTreeFeature))) // Expect 11
    println(tree.findLeafId(DecisionTreeFeatures(List(2.0, 1.0, 1.7) map DecisionTreeFeature))) // Expect 11
  }
}
