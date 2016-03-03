package com.kaggle.ml

/**
  * Created by freezing on 03/03/16.
  */
package object decisiontree {
  val EPS = 1e-3

  case class DecisionTreeNodeId(id: Int) extends AnyVal
  case class DecisionTreeFeature(value: Double) extends AnyVal
  case class DecisionTreeFeatures(vector: List[DecisionTreeFeature])
  case class DecisionTreeNode(id: DecisionTreeNodeId, centroid: DecisionTreeFeature, children: List[DecisionTreeNode]) {
    def isLeaf: Boolean = children.isEmpty
    def findDecisionLeaf(features: List[DecisionTreeFeature]): DecisionTreeNodeId = {
      if (isLeaf) id
      else closestChild(features.head).findDecisionLeaf(features.tail)
    }

    private def closestChild(feature: DecisionTreeFeature): DecisionTreeNode = {
      // Mutable
      var best = Double.PositiveInfinity
      var bestNode: DecisionTreeNode = null
      children foreach { child =>
        val tmp = Math.abs(child.centroid.value - feature.value)
        if (tmp < best) {
          best = tmp
          bestNode = child
        }
      }
      bestNode
    }
  }

  case class DecisionTree(children: List[DecisionTreeNode]) {
    // Id and Feature are not part of the root node because it technically doesn't exist. This is more like forest structure.
    val root = DecisionTreeNode(DecisionTreeNodeId(-1), DecisionTreeFeature(-1), children)
    def findLeafId(features: DecisionTreeFeatures): DecisionTreeNodeId = root.findDecisionLeaf(features.vector)
  }
}
