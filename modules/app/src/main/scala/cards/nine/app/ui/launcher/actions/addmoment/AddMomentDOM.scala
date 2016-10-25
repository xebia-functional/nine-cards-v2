package cards.nine.app.ui.launcher.actions.addmoment

import cards.nine.models.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}

trait AddMomentDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.actions_recycler)

}

trait AddMomentListener {

  def loadMoments(): Unit

  def addMoment(moment: NineCardsMoment): Unit

}
