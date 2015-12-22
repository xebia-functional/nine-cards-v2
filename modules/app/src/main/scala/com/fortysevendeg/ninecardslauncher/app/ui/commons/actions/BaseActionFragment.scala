package com.fortysevendeg.ninecardslauncher.app.ui.commons.actions

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.ui.collections.ActionsScreenListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiExtensions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.ActionsSnails._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Contexts, Ui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait BaseActionFragment
  extends Fragment
  with TypedFindView
  with ContextSupportProvider
  with UiExtensions
  with Contexts[Fragment] {

  val defaultValue = 0

  private[this] lazy val defaultColor = fragmentContextWrapper.application.getResources.getColor(R.color.primary)

  var actionsScreenListener: Option[ActionsScreenListener] = None

  override protected def findViewById(id: Int): View = rootView map (_.findViewById(id)) orNull

  protected var width: Int = 0

  protected var height: Int = 0

  protected lazy val sizeIcon = getInt(Seq(getArguments), BaseActionFragment.sizeIcon, defaultValue)

  protected lazy val originalPosX = getInt(Seq(getArguments), BaseActionFragment.startRevealPosX, defaultValue)

  protected lazy val originalPosY = getInt(Seq(getArguments), BaseActionFragment.startRevealPosY, defaultValue)

  protected lazy val endPosX = getInt(Seq(getArguments), BaseActionFragment.endRevealPosX, defaultValue)

  protected lazy val endPosY = getInt(Seq(getArguments), BaseActionFragment.endRevealPosY, defaultValue)

  protected lazy val colorPrimary = getInt(Seq(getArguments), BaseActionFragment.colorPrimary, defaultColor)

  protected lazy val toolbar = Option(findView(TR.actions_toolbar))

  protected lazy val loading = Option(findView(TR.action_loading))

  protected lazy val transitionView = Option(findView(TR.actions_transition))

  protected lazy val content = Option(findView(TR.action_content_layout))

  protected lazy val rootContent = Option(findView(TR.action_content_root))

  protected lazy val fab = Option(findView(TR.action_content_fab))

  protected lazy val errorContent = Option(findView(TR.actions_content_error_layout))

  protected lazy val errorMessage = Option(findView(TR.actions_content_error_message))

  protected lazy val errorIcon = Option(findView(TR.actions_content_error_icon))

  protected lazy val errorButton = Option(findView(TR.actions_content_error_button))

  protected var rootView: Option[FrameLayout] = None

  def getLayoutId: Int

  def useFab: Boolean = false

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val baseView = LayoutInflater.from(getActivity).inflate(R.layout.base_action_fragment, container, false).asInstanceOf[FrameLayout]
    val layout = LayoutInflater.from(getActivity).inflate(getLayoutId, javaNull)
    rootView = Option(baseView)
    runUi(
      (content <~ vgAddView(layout))  ~
        (loading <~ pbColor(colorPrimary)) ~
        (transitionView <~ vBackgroundColor(colorPrimary)) ~
        (rootContent <~ vInvisible) ~
        (errorContent <~ vGone) ~
        (errorButton <~ vBackgroundTint(colorPrimary)))
    baseView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      override def onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int): Unit = {
        v.removeOnLayoutChangeListener(this)
        width = right - left
        height = bottom - top
        runUi(reveal)
      }
    })
    baseView
  }

  def reveal: Ui[_] = {
    val projection = rootView map (projectionScreenPositionInView(_, originalPosX, originalPosY)) getOrElse(defaultValue, defaultValue)
    val ratioScaleToolbar = toolbar map (tb => tb.getHeight.toFloat / height.toFloat) getOrElse 0f
    (rootView <~~ revealIn(projection._1, projection._2, width, height, sizeIcon)) ~~
      (transitionView <~~ scaleToToolbar(ratioScaleToolbar)) ~~
      (rootContent <~~ showContent()) ~~
      (if (useFab) fab <~~ showFab() else Ui.nop)
  }

  def unreveal(): Ui[_] = {
    val projection = rootView map (projectionScreenPositionInView(_, endPosX, endPosY)) getOrElse(defaultValue, defaultValue)
    onStartFinishAction ~ (rootView <~~ revealOut(projection._1, projection._2, width, height)) ~~ onEndFinishAction
  }

  def showError(message: Int, action: => Unit): Ui[_] =
    (loading <~ vGone) ~
      (errorMessage <~ text(message)) ~
      (errorButton <~ On.click {
        action
        hideError
      }) ~
      (errorContent <~ vVisible)

  def hideError: Ui[_] = errorContent <~ vGone

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
  val sizeIcon = "size_icon"
  val startRevealPosX = "start_reveal_pos_x"
  val startRevealPosY = "start_reveal_pos_y"
  val endRevealPosX = "end_reveal_pos_x"
  val endRevealPosY = "end_reveal_pos_y"
  val packages = "packages"
  val colorPrimary = "color_primary"
}