package com.kaggle.ml.decisiontree

/**
  * Created by freezing on 03/03/16.
  */
class DecisionTreeNodeBuilder(val id: DecisionTreeNodeId, val centroid: DecisionTreeFeature) {
  // Initialize to anything
  var size = 1
  var children: collection.mutable.MutableList[DecisionTreeNodeBuilder] = collection.mutable.MutableList.empty[DecisionTreeNodeBuilder]
  def build: DecisionTreeNode = DecisionTreeNode(id, centroid, (children map { child => child.build }).toList)
}