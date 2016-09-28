package cards.nine.app.ui.collections.actions.recommendations

import cards.nine.process.recommendations.models.RecommendedApp
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