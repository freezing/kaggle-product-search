package com.kaggle.ml.decisiontree

/**
  * Created by freezing on 03/03/16.
  */
class DecisionTreeBuilder(data: List[DecisionTreeFeatures]) {
  var nextNodeId = 0
  var children: collection.mutable.MutableList[DecisionTreeNodeBuilder] = collection.mutable.MutableList.empty[DecisionTreeNodeBuilder]

  private def nextId(): Int = {
    nextNodeId += 1
    nextNodeId
  }

  def build(): DecisionTree = {
    data foreach { features => insert(features.vector) }
    DecisionTree((children map { _.build }).toList)
  }

  private def insert(featureVector: List[DecisionTreeFeature], children: collection.mutable.MutableList[DecisionTreeNodeBuilder] = children): Unit = {
    if (featureVector.nonEmpty) {
      // Go along the path for each coordinate in the feature vector
      insert(featureVector.tail, (findCorrespondingNode(featureVector.head, children) match {
        case Some(child) =>
          child.size += 1
          child
        case None =>
          var newChild = new DecisionTreeNodeBuilder(DecisionTreeNodeId(nextId()), featureVector.head)
          children += newChild
          newChild
      }).children)
    }

    def findCorrespondingNode(feature: DecisionTreeFeature, children: collection.mutable.MutableList[DecisionTreeNodeBuilder]): Option[DecisionTreeNodeBuilder] = {
      children collectFirst { case x if Math.abs(x.centroid.value - feature.value) < EPS => x }
    }
  }
}