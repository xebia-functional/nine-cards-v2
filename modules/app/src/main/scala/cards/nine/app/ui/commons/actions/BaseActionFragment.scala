package cards.nine.app.ui.commons.actions

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.FrameLayout
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.di.{Injector, InjectorImpl}
import cards.nine.app.ui.collections.ActionsScreenListener
import cards.nine.app.ui.commons.AppUtils._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.actions.ActionsSnails._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.preferences.commons.Theme
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.process.theme.models._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ProgressBarTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait BaseActionFragment
  extends Fragment
  with TypedFindView
  with ContextSupportProvider
  with UiExtensions
  with Contexts[Fragment] {

  val defaultValue = 0

  implicit lazy val di: Injector = new InjectorImpl

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  implicit lazy val theme: NineCardsTheme =
    di.themeProcess.getTheme(Theme.getThemeFile).resolveNow match {
      case Right(t) => t
      case _ => getDefaultTheme
    }

  private[this] lazy val defaultColor = theme.get(PrimaryColor)

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

  protected lazy val backgroundColor = theme.get(DrawerBackgroundColor)

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
    ((transitionView <~ vBackgroundColor(backgroundColor)) ~
      (rootView <~ vBackgroundColor(backgroundColor)) ~
      (errorContent <~ vBackgroundColor(backgroundColor)) ~
      (content <~ vgAddView(layout))  ~
      (loading <~ pbColor(colorPrimary)) ~
      (transitionView <~ vBackgroundColor(colorPrimary)) ~
      (errorIcon <~ tivColor(colorPrimary)) ~
      (rootContent <~ vInvisible) ~
      (errorContent <~ vGone) ~
      (errorMessage <~ tvColor(theme.get(DrawerTextColor).alpha(0.8f))) ~
      (errorButton <~ vBackgroundTint(colorPrimary))).run
    baseView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
      override def onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int): Unit = {
        v.removeOnLayoutChangeListener(this)
        width = right - left
        height = bottom - top
        reveal.run
      }
    })
    baseView
  }

  def reveal: Ui[_] = {
    val (x, y) = rootView map (view => view.projectionScreenPositionInView(originalPosX, originalPosY)) getOrElse(defaultValue, defaultValue)
    val ratioScaleToolbar = toolbar map (tb => tb.getHeight.toFloat / height.toFloat) getOrElse 0f
    (rootView <~~ revealIn(x, y, width, height, sizeIcon)) ~~
      (transitionView <~~ scaleToToolbar(ratioScaleToolbar)) ~~
      (rootContent <~~ showContent()) ~~
      (if (useFab) fab <~~ showFab() else Ui.nop)
  }

  def unreveal(): Ui[_] = {
    val (x, y) = rootView map (view => view.projectionScreenPositionInView(endPosX, endPosY)) getOrElse(defaultValue, defaultValue)
    onStartFinishAction ~ (rootView <~~ revealOut(x, y, width, height)) ~~ onEndFinishAction
  }

  def showMessageInScreen(message: Int, error: Boolean, action: => Unit): Ui[_] =
    (loading <~ vGone) ~
      (errorIcon <~ ivSrc(if (error) R.drawable.placeholder_error else R.drawable.placeholder_empty)) ~
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