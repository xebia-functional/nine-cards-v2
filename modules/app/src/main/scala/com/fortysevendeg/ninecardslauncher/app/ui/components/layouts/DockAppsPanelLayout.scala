package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent._
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppDockType, ContactDockType}
import com.fortysevendeg.ninecardslauncher.process.device.models.DockApp
import com.fortysevendeg.ninecardslauncher.process.theme.models.{AppDrawerPressedColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class DockAppsPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
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

  var dockApps: Seq[DockApp] = Seq.empty

  var draggingTo: Option[Int] = None

  LayoutInflater.from(context).inflate(R.layout.app_drawer_panel, this)

  def init(apps: Seq[DockApp])
    (implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Ui[Any] = {
    dockApps = apps
    (Option(findView(TR.launcher_page_1)) <~ populate(0)) ~
      (Option(findView(TR.launcher_page_2)) <~ populate(1)) ~
      (Option(findView(TR.launcher_page_3)) <~ populate(2)) ~
      (Option(findView(TR.launcher_page_4)) <~ populate(3))
  }

  def reload(dockApp: DockApp)
    (implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Ui[Any] = {
    dockApps = dockApps map (app => if (app.position == dockApp.position) dockApp else app)
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
        draggingTo flatMap dockApps.lift map { app =>
          presenter.endAddItemToDockApp(app.position)
        } getOrElse {
          presenter.endAddItem()
        }
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
      case view: TintableImageView if view.getPosition.contains(position) => view <~ populate(position)
    }

  private[this] def calculatePosition(x: Float): Option[Int] = {
    val space = x.toInt / (getWidth / numberOfItems)
    space match {
      case `appDrawerPosition` => None
      case s if s < appDrawerPosition => Some(s)
      case s => Some(s - 1)
    }
  }

  private[this] def populate(position: Int)
    (implicit theme: NineCardsTheme, presenter: LauncherPresenter, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Tweak[TintableImageView] =
    tivPressedColor(theme.get(AppDrawerPressedColor)) +
      vSetPosition(position) +
      (dockApps.lift(position) map { app =>
        (app.dockType match {
          case AppDockType => ivSrcByPackageName(app.intent.extractPackageName(), app.name)
          case ContactDockType => ivUriContact(app.imagePath, app.name, circular = true)
          case _ => Tweak.blank
        }) +
          On.click (Ui(presenter.execute(app.intent)))
      } getOrElse Tweak.blank)

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

}
