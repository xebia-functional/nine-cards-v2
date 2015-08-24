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
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils._

import scala.concurrent.ExecutionContext.Implicits.global

class BaseActionFragment
  extends Fragment
  with TypedFindView
  with ContextSupportProvider
  with UiExtensions
  with Contexts[Fragment] {

  val defaultPosition = 0

  var actionsScreenListener: Option[ActionsScreenListener] = None

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  protected var width: Int = 0

  protected var height: Int = 0

  protected lazy val originalPosX = getInt(Seq(getArguments), BaseActionFragment.posX, defaultPosition)

  protected lazy val originalPosY = getInt(Seq(getArguments), BaseActionFragment.posY, defaultPosition)

  protected var rootView: Option[View] = None

  def reveal: Ui[_] = {
    val projection = rootView map (projectionScreenPositionInView(_, originalPosX, originalPosY)) getOrElse(defaultPosition, defaultPosition)
    rootView <~ revealIn(projection._1, projection._2, width, height)
  }

  def unreveal(): Ui[_] = onStartFinishAction ~ (rootView <~~ revealOut(width, height)) ~~ onEndFinishAction

  def createBaseView(view: View): View = {
    rootView = Option(view)
    view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      override def onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int): Unit = {
        v.removeOnLayoutChangeListener(this)
        width = right - left
        height = bottom - top
        runUi(reveal)
      }
    })
    view
  }

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

  private[this] def onStartFinishAction() = Ui {
    actionsScreenListener foreach (_.onStartFinishAction())
  }

  private[this] def onEndFinishAction() = Ui {
    actionsScreenListener foreach (_.onEndFinishAction())
  }

}

object BaseActionFragment {

  val posX = "pos_x"
  val posY = "pos_y"

}