package cards.nine.app.ui.components.layouts

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.components.layouts.tweaks.WorkSpaceButtonTweaks._
import cards.nine.app.ui.components.models.LauncherMoment
import cards.nine.app.ui.launcher.LauncherActivity
import cards.nine.app.ui.launcher.jobs.NavigationJobs
import cards.nine.commons.javaNull
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.types.theme.DrawerBackgroundColor
import cards.nine.models.{Card, Collection, NineCardsTheme}
import cards.nine.process.intents.LauncherExecutorProcessPermissionException
import macroid.extras.DeviceVersion.Lollipop
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class AppsMomentLayout(context: Context, attrs: AttributeSet, defStyle: Int)
    extends LinearLayout(context, attrs, defStyle)
    with Contexts[View]
    with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  // TODO First implementation in order to remove LauncherPresenter
  val navigationJobs: NavigationJobs = context match {
    case activity: LauncherActivity => activity.navigationJobs
    case _                          => throw new RuntimeException("NavigationJobs not found")
  }

  LayoutInflater.from(context).inflate(R.layout.apps_moment_layout, this)

  lazy val iconContent = findView(TR.moment_bar_icon_content)

  lazy val icon = findView(TR.moment_bar_icon)

  lazy val appsContent = findView(TR.moment_bar_apps)

  (Lollipop.ifSupportedThen(iconContent <~ vElevation(
    resGetDimensionPixelSize(R.dimen.elevation_default))) getOrElse Ui.nop).run

  def populate(moment: LauncherMoment)(
      implicit context: ActivityContextWrapper,
      theme: NineCardsTheme): Ui[Any] = moment.collection match {
    case Some(collection: Collection) =>
      val resIcon = collection.getIconDetail
      val color   = theme.getIndexColor(collection.themedColorIndex)
      (this <~
        vBackgroundColor(theme.get(DrawerBackgroundColor))) ~
        (iconContent <~
          vBackgroundColor(color) <~
          On.click {
            Ui(navigationJobs.navigationUiActions.goToMomentWorkspace().resolveAsync())
          }) ~
        (icon <~
          ivSrc(resIcon)) ~
        (appsContent <~
          vgRemoveAllViews <~
          vgAddViews(collection.cards map (createIconCard(_, moment.momentType))))
    case _ =>
      val blank: Drawable = javaNull
      (this <~ vBlankBackground) ~
        (iconContent <~ vBlankBackground) ~
        (appsContent <~ vgRemoveAllViews) ~
        (icon <~ ivSrc(blank))
  }

  def setPaddingTopAndBottom(paddingTop: Int, paddingBottom: Int) =
    (iconContent <~ vPadding(paddingTop = paddingTop)) ~
      (appsContent <~ vPadding(paddingBottom = paddingBottom))

  private[this] def createIconCard(card: Card, moment: Option[NineCardsMoment])(
      implicit contextWrapper: ActivityContextWrapper,
      theme: NineCardsTheme): WorkSpaceButton =
    (w[WorkSpaceButton] <~
      vMatchWidth <~
      wbInit(WorkSpaceAppMomentButton) <~
      wbPopulateCard(card) <~
      On.click {
        Ui(navigationJobs.openMomentIntent(card, moment).resolveAsyncServiceOr[Throwable] {
          case e: LauncherExecutorProcessPermissionException =>
            navigationJobs.openMomentIntentException(card.intent.extractPhone())
          case _ => navigationJobs.navigationUiActions.showContactUsError()
        })
      }).get

}
