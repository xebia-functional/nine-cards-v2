package cards.nine.app.ui.collections.actions.recommendations

import cards.nine.models.NotCategorizedPackage
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait RecommendationsDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.actions_recycler)

}

trait RecommendationsUiListener {

  def loadRecommendations(): Unit

  def addApp(app: NotCategorizedPackage): Unit

  def installApp(app: NotCategorizedPackage): Unit

}