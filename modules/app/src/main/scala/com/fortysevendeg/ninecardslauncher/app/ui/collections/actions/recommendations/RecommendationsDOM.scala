package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations

import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}

trait RecommendationsDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.actions_recycler)

}

trait RecommendationsUiListener {

  def loadRecommendations(): Unit

  def addApp(app: RecommendedApp): Unit

  def installApp(app: RecommendedApp): Unit

}