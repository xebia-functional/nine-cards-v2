package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.ActionsScreenListener
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.ActionsSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiExtensions
import com.fortysevendeg.ninecardslauncher2.TypedFindView
import macroid.FullDsl._
import macroid.{Contexts, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class BaseActionFragment
  extends Fragment
  with TypedFindView
  with ContextSupportProvider
  with UiExtensions
  with Contexts[Fragment] {

  var actionsScreenListener: Option[ActionsScreenListener] = None

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  protected var width: Int = 0

  protected var height: Int = 0

  protected lazy val revealPosX = getInt(Seq(getArguments), BaseActionFragment.posX, 0)

  protected lazy val revealPosY = getInt(Seq(getArguments), BaseActionFragment.posY, 0)

  protected var rootView: Option[View] = None

  def reveal: Ui[_] = rootView <~ revealIn(revealPosX, revealPosY, width, height)

  def unreveal(): Ui[_] = (rootView <~~ revealOut(revealPosX, revealPosY, width, height)) ~~ finishAction

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    activity match {
      case listener: ActionsScreenListener => actionsScreenListener = Some(listener)
      case _ =>
    }
  }

  override def onDetach(): Unit = {
    super.onDetach()
    actionsScreenListener = None
  }

  private[this] def finishAction() = Ui {
    actionsScreenListener foreach (_.finishAction())
  }

}

object BaseActionFragment {

  val posX = "pos_x"
  val posY = "pos_y"

}