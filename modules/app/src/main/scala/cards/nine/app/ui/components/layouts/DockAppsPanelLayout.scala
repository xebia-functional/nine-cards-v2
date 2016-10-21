package cards.nine.app.ui.components.layouts

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.DragEvent._
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.widgets.TintableImageView
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.launcher.LauncherActivity
import cards.nine.app.ui.launcher.jobs.{DragJobs, NavigationJobs}
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService._
import cards.nine.models.DockAppData
import cards.nine.models.types.{AppDockType, ContactDockType}
import cards.nine.process.intents.LauncherExecutorProcessPermissionException
import cards.nine.process.theme.models.{DockPressedColor, NineCardsTheme}
import cats.implicits._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class DockAppsPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  // TODO First implementation in order to remove LauncherPresenter
  val dragJobs: DragJobs = context match {
    case activity: LauncherActivity => activity.dragJobs
    case _ => throw new RuntimeException("DragJobs not found")
  }

  val navigationJobs: NavigationJobs = context match {
    case activity: LauncherActivity => activity.navigationJobs
    case _ => throw new RuntimeException("NavigationJobs not found")
  }


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

  var dockApps: Seq[DockAppData] = Seq.empty

  var draggingTo: Option[Int] = None

  LayoutInflater.from(context).inflate(R.layout.app_drawer_panel, this)

  def init(apps: Seq[DockAppData])
    (implicit theme: NineCardsTheme, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Ui[Any] = {
    dockApps = apps
    (findView(TR.launcher_page_1) <~ vSetPosition(0) <~ populate(getDockApp(0))) ~
      (findView(TR.launcher_page_2) <~ vSetPosition(1) <~ populate(getDockApp(1))) ~
      (findView(TR.launcher_page_3) <~ vSetPosition(2) <~ populate(getDockApp(2))) ~
      (findView(TR.launcher_page_4) <~ vSetPosition(3) <~ populate(getDockApp(3)))
  }

  def reload(dockApp: DockAppData)
    (implicit theme: NineCardsTheme, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Ui[Any] = {
    dockApps = (dockApps filterNot (_.position == dockApp.position)) :+ dockApp
    this <~ updatePosition(dockApp.position)
  }

  def dragAddItemController(action: Int, x: Float, y: Float)(implicit contextWrapper: ActivityContextWrapper): Unit =
    action match {
      case ACTION_DRAG_LOCATION =>
        val newPosition = calculatePosition(x)
        if (newPosition != draggingTo) {
          draggingTo = newPosition
          (this <~ (draggingTo map select getOrElse select(unselectedPosition))).run
        }
      case ACTION_DROP =>
        draggingTo match {
          case Some(position) =>
            dragJobs.endAddItemToDockApp(position).resolveAsyncServiceOr(_ =>
              dragJobs.dragUiActions.endAddItem() *> dragJobs.navigationUiActions.showContactUsError())
          case _ => dragJobs.endAddItem().resolveAsync()
        }
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case ACTION_DRAG_EXITED =>
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case ACTION_DRAG_ENDED =>
        dragJobs.endAddItem().resolveAsync()
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case _ =>
    }

  private[this] def updatePosition(position: Int)
    (implicit theme: NineCardsTheme, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Transformer =
    Transformer {
      case view: TintableImageView if view.getPosition.contains(position) => view <~ populate(getDockApp(position))
    }

  private[this] def calculatePosition(x: Float): Option[Int] = {
    val space = x.toInt / (getWidth / numberOfItems)
    space match {
      case `appDrawerPosition` => None
      case s if s < appDrawerPosition => Option(s)
      case s => Some(s - 1)
    }
  }

  private[this] def populate(dockApp: Option[DockAppData])
    (implicit theme: NineCardsTheme, uiContext: UiContext[_], contextWrapper: ActivityContextWrapper): Tweak[TintableImageView] =
    tivPressedColor(theme.get(DockPressedColor)) +
      (dockApp map { app =>
        (app.dockType match {
          case AppDockType => ivSrcByPackageName(app.intent.extractPackageName(), app.name)
          case ContactDockType => ivUriContactFromLookup(app.intent.extractLookup(), app.name, circular = true)
          case _ => ivSrc(noFoundAppDrawable)
        }) +
          On.click (Ui {
            navigationJobs.execute(app.intent).resolveServiceOr[Throwable]{
              case e: LauncherExecutorProcessPermissionException =>
                navigationJobs.openMomentIntentException(app.intent.extractPhone())
              case _ => navigationJobs.navigationUiActions.showContactUsError()
            }
          })
      } getOrElse ivSrc(noFoundAppDrawable) + On.click(Ui.nop))

  private[this] def select(position: Int)(implicit contextWrapper: ActivityContextWrapper) = Transformer {
    case view: TintableImageView if view.getPosition.contains(position) =>
      view <~ applyAnimation(
        scaleX = Option(selectedScale),
        scaleY = Option(selectedScale),
        alpha = Option(selectedAlpha))
    case view: TintableImageView =>
      view <~ applyAnimation(
        scaleX = Option(defaultScale),
        scaleY = Option(defaultScale),
        alpha = Option(defaultAlpha))
  }

  private[this] def getDockApp(position: Int) = dockApps.find(_.position == position)

}
