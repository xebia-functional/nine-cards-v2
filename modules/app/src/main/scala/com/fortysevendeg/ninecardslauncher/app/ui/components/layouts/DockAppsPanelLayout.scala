package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.DragEvent._
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ViewOps._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppDockType, ContactDockType}
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp
import com.fortysevendeg.ninecardslauncher.process.theme.models.{DockPressedColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class DockAppsPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  val unselectedPosition = -1

  val selectedScale = 1.1f

  val defaultScale = 1f

  val selectedAlpha =.4f

  val defaultAlpha = 1f

  val numberOfItems = 5

  val appDrawerPosition = 2

  lazy val noFoundAppDrawable = PathMorphDrawable(
    defaultIcon = IconTypes.ADD,
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    defaultColor = Color.WHITE.alpha(selectedAlpha),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  var dockApps: Seq[DockApp] = Seq.empty

  var draggingTo: Option[Int] = None

  LayoutInflater.from(context).inflate(R.layout.app_drawer_panel, this)

  def init(apps: Seq[DockApp])
    (implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Ui[Any] = {
    dockApps = apps
    (findView(TR.launcher_page_1) <~ vSetPosition(0) <~ populate(getDockApp(0))) ~
      (findView(TR.launcher_page_2) <~ vSetPosition(1) <~ populate(getDockApp(1))) ~
      (findView(TR.launcher_page_3) <~ vSetPosition(2) <~ populate(getDockApp(2))) ~
      (findView(TR.launcher_page_4) <~ vSetPosition(3) <~ populate(getDockApp(3)))
  }

  def reload(dockApp: DockApp)
    (implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Ui[Any] = {
    dockApps = (dockApps filterNot (_.position == dockApp.position)) :+ dockApp
    this <~ updatePosition(dockApp.position)
  }

  def dragAddItemController(action: Int, x: Float, y: Float)(implicit presenter: LauncherPresenter, contextWrapper: ActivityContextWrapper): Unit =
    action match {
      case ACTION_DRAG_LOCATION =>
        val newPosition = calculatePosition(x)
        if (newPosition != draggingTo) {
          draggingTo = newPosition
          (this <~ (draggingTo map select getOrElse select(unselectedPosition))).run
        }
      case ACTION_DROP =>
        draggingTo foreach presenter.endAddItemToDockApp
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case ACTION_DRAG_EXITED =>
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case ACTION_DRAG_ENDED =>
        presenter.endAddItem()
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case _ =>
    }

  private[this] def updatePosition(position: Int)
    (implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Transformer =
    Transformer {
      case view: TintableImageView if view.getPosition.contains(position) => view <~ populate(getDockApp(position))
    }

  private[this] def calculatePosition(x: Float): Option[Int] = {
    val space = x.toInt / (getWidth / numberOfItems)
    space match {
      case `appDrawerPosition` => None
      case s if s < appDrawerPosition => Some(s)
      case s => Some(s - 1)
    }
  }

  private[this] def populate(dockApp: Option[DockApp])
    (implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Tweak[TintableImageView] =
    tivPressedColor(theme.get(DockPressedColor)) +
      (dockApp map { app =>
        (app.dockType match {
          case AppDockType => ivSrcByPackageName(app.intent.extractPackageName(), app.name)
          case ContactDockType => ivUriContact(app.imagePath, app.name, circular = true)
          case _ => ivSrc(noFoundAppDrawable)
        }) +
          On.click (Ui(presenter.execute(app.intent)))
      } getOrElse ivSrc(noFoundAppDrawable) + On.click(Ui.nop))

  private[this] def select(position: Int)(implicit contextWrapper: ActivityContextWrapper) = Transformer {
    case view: TintableImageView if view.getPosition.contains(position) =>
      view <~ applyAnimation(
        scaleX = Some(selectedScale),
        scaleY = Some(selectedScale),
        alpha = Some(selectedAlpha))
    case view: TintableImageView =>
      view <~ applyAnimation(
        scaleX = Some(defaultScale),
        scaleY = Some(defaultScale),
        alpha = Some(defaultAlpha))
  }

  private[this] def getDockApp(position: Int) = dockApps.find(_.position == position)

}
