package cards.nine.app.ui.launcher.actions.editmoment

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.commons.javaNull
import cards.nine.models.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher2.R

class EditMomentFragment
  extends BaseActionFragment
  with EditMomentActionsImpl
  with AppNineCardsIntentConversions { self =>

  lazy val momentType = Option(getString(Seq(getArguments), EditMomentFragment.momentKey, javaNull))

  override lazy val editPresenter = new EditMomentPresenter(self)

  override def useFab: Boolean = true

  override def getLayoutId: Int = R.layout.edit_moment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    momentType match {
      case Some(moment) => editPresenter.initialize(NineCardsMoment(moment))
      case _ => editPresenter.momentNoFound()
    }
  }

}

object EditMomentFragment {

  val momentKey = "moment"

}

