package cards.nine.app.ui.collections.jobs.uiactions

import android.animation.ValueAnimator
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.Gravity
import android.view.ViewGroup.LayoutParams._
import android.widget.LinearLayout
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.CollectionsPagerAdapter
import cards.nine.app.ui.collections.snails.CollectionsSnails._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.dialogs.wizard.{CollectionsWizardInline, WizardInlinePreferences}
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.drawables.tweaks.PathMorphDrawableTweaks._
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.layouts.tweaks.FabItemMenuTweaks._
import cards.nine.app.ui.components.layouts.tweaks.SlidingTabLayoutTweaks._
import cards.nine.app.ui.components.layouts.{FabItemMenu, SlidingTabLayout}
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.theme.{CollectionDetailTextTabDefaultColor, CollectionDetailTextTabSelectedColor}
import cards.nine.models.{Card, Collection, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import macroid.extras.FloatingActionButtonTweaks._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.UIActionsExtras._
import macroid.extras.ViewPagerTweaks._
import macroid.extras.ViewTweaks._

import scala.concurrent.ExecutionContext.Implicits.global

class GroupCollectionsUiActions(val dom: GroupCollectionsDOM, listener: GroupCollectionsUiListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends ImplicitsUiExceptions {

  implicit lazy val systemBarsTint = new SystemBarsTint

  lazy val wizardInlinePreferences = new WizardInlinePreferences()

  implicit def theme: NineCardsTheme = statuses.theme

  // Ui Actions

  def initialize(): TaskService[Unit] =
    ((dom.tabs <~ tabsStyle) ~
      initFabButton ~
      loadMenuItems(getItemsForFabMenu)).toService()

  def showCollections(collections: Seq[Collection], position: Int): TaskService[Unit] =
    (collections lift position match {
      case Some(collection) =>
        val adapter = CollectionsPagerAdapter(fragmentManagerContext.manager, collections, position)
        (dom.viewPager <~ vpAdapter(adapter)) ~
          Ui(adapter.activateFragment(position)) ~
          (dom.tabs <~
            stlViewPager(dom.viewPager) <~
            stlOnPageChangeListener(
              new OnPageChangeCollectionsListener(position, updateToolbarColor, updateCollection))) ~
          uiHandler(dom.viewPager <~ vpCurrentItem(position, smoothScroll = false)) ~
          uiHandlerDelayed(Ui {
            listener.bindAnimatedAdapter()
          }, delayMilis = 100) ~
          (dom.tabs <~ vVisible <~~ enterViews)
      case _ => Ui.nop
    }).toService()

  def openCollectionsWizardInline(): TaskService[Unit] =
    (if (wizardInlinePreferences.shouldBeShowed(CollectionsWizardInline)) {
      dom.root <~ vLauncherWizardSnackbar(CollectionsWizardInline, forceNavigationBarHeight = false)
    } else {
      Ui.nop
    }).toService()

  def showContactUsError(): TaskService[Unit] = showError().toService()

  def back(): TaskService[Unit] = (if (dom.isMenuOpened) {
    swapFabMenu()
  } else if (listener.isEditingMode) {
    Ui(listener.closeEditingMode())
  } else {
    exitTransition
  }).toService()

  def destroy(): TaskService[Unit] = Ui {
    dom.getAdapter foreach(_.clear())
  }.toService()

  def getCurrentCollection: TaskService[Option[Collection]] = TaskService {
    CatchAll[UiException](dom.getCurrentCollection)
  }

  def getCollection(position: Int): TaskService[Option[Collection]] = TaskService {
    CatchAll[UiException](dom.getCollection(position))
  }

  def reloadCards(cards: Seq[Card]): TaskService[Unit] = Ui {
    for {
      adapter <- dom.getAdapter
      currentPosition <- adapter.getCurrentFragmentPosition
    } yield {
      adapter.updateCardFromCollection(currentPosition, cards)
    }
  }.toService()

  def editCard(collectionId: Int, cardId: Int, cardName: String): TaskService[Unit] =
    Ui (listener.showEditCollectionDialog(cardName, (maybeNewName) => {
      listener.saveEditedCard(collectionId, cardId, maybeNewName)
    })).toService()

  def removeCards(cards: Seq[Card]): TaskService[Unit] = Ui {
    for {
      adapter <- dom.getAdapter
      currentPosition <- adapter.getCurrentFragmentPosition
    } yield {
      adapter.removeCardFromCollection(currentPosition, cards)
    }
  }.toService()

  def addCardsToCollection(collectionPosition: Int, cards: Seq[Card]): TaskService[Unit] = Ui {
    for {
      adapter <- dom.getAdapter
    } yield {
      adapter.addCardsToCollection(collectionPosition, cards)
      adapter.getFragmentByPosition(collectionPosition).foreach { fragment =>
        fragment.getAdapter foreach (_.addCards(cards))
        listener.showDataInPosition(collectionPosition)
      }
    }
  }.toService()

  def reloadItemCollection(itemsSelected: Int, position: Int): TaskService[Unit] =
    (Ui(dom.invalidateOptionMenu) ~
      (dom.toolbarTitle <~ tvText(resGetString(R.string.itemsSelected, itemsSelected.toString))) ~
      Ui(dom.notifyItemChangedCollectionAdapter(position))).toService()

  def showNoPhoneCallPermissionError(): TaskService[Unit] = showMessage(R.string.noPhoneCallPermissionMessage).toService()

  def addCards(cards: Seq[Card]): TaskService[Unit] = Ui {
    for {
      adapter <- dom.getAdapter
      currentPosition <- adapter.getCurrentFragmentPosition
    } yield {
      adapter.addCardsToCollection(currentPosition, cards)
    }
  }.toService()

  def openReorderModeUi(): TaskService[Unit] = hideFabButton.toService()

  def startEditing(items: Int): TaskService[Unit] =
    (Ui(dom.invalidateOptionMenu) ~
      (dom.toolbarTitle <~ tvText(resGetString(R.string.itemsSelected, items.toString))) ~
      (dom.iconContent <~ applyAnimation(alpha = Some(0))) ~
      Ui(dom.notifyDataSetChangedCollectionAdapter())).toService()

  def closeEditingModeUi(): TaskService[Unit] =
    ((dom.toolbarTitle <~ tvText("")) ~
      (dom.iconContent <~ (vVisible + vScaleX(1) + vScaleY(1) + vAlpha(0f) ++ applyAnimation(alpha = Some(1)))) ~
      Ui(dom.notifyDataSetChangedCollectionAdapter()) ~
      Ui(dom.invalidateOptionMenu)).toService()

  def showMenu(autoHide: Boolean = true, openMenu: Boolean = false, indexColor: Int): TaskService[Unit] = {
    val color = theme.getIndexColor(indexColor)
    (showFabButton(color, autoHide) ~
      (if (openMenu) swapFabMenu(forceOpen = true) else Ui.nop)).toService()
  }

  def hideMenu(): TaskService[Unit] =
    (if (dom.isFabButtonVisible) swapFabMenu() else Ui.nop).toService()

  def hideMenuButton(): TaskService[Unit] = hideFabButton.toService()

  def close(): TaskService[Unit] = exitTransition.toService()

  // FabButtonBehaviour

  private[this] def updateBarsInFabMenuHide(): Ui[Any] =
    dom.getCurrentCollection map (c => systemBarsTint.updateStatusColor(theme.getIndexColor(c.themedColorIndex))) getOrElse Ui.nop

  private[this] var runnableHideFabButton: Option[RunnableWrapper] = None

  private[this] lazy val handler = new Handler()

  private[this] val timeDelayFabButton = 3000

  private[this] def initFabButton: Ui[_] =
    (dom.fabMenuContent <~ On.click(swapFabMenu()) <~ vClickable(false)) ~
      (dom.fabButton <~ fabButtonMenuStyle <~ On.click(swapFabMenu()))

  private[this] def loadMenuItems(items: Seq[FabItemMenu]): Ui[_] =
    dom.fabMenu <~ Tweak[LinearLayout] { view =>
      val param = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.END)
      items foreach (view.addView(_, 0, param))
    }

  private[this] def swapFabMenu(doUpdateBars: Boolean = true, forceOpen: Boolean = false): Ui[Any] = {
    val open = if (forceOpen) false else dom.isMenuOpened
    val autoHide = dom.isAutoHide
    val ui = (dom.fabButton <~
      vAddField(dom.opened, !open) <~
      pmdAnimIcon(if (open) IconTypes.ADD else IconTypes.CLOSE)) ~
      (dom.fabMenuContent <~
        animFabButton(open) <~
        colorContentDialog(!open) <~
        vClickable(!open)) ~
      (if (open && autoHide) postDelayedHideFabButton else removeDelayedHideFabButton())
    ui ~ (if (doUpdateBars) updateBars(open) else Ui.nop)
  }

  private[this] def colorContentDialog(paint: Boolean) =
    vBackgroundColorResource(if (paint) R.color.background_dialog else android.R.color.transparent)

  private[this] def showFabButton(color: Int = 0, autoHide: Boolean = true): Ui[_] =
    if (dom.isFabButtonVisible && autoHide) {
      resetDelayedHide
    } else {
      val colorDark = color.dark()
      (if (autoHide) postDelayedHideFabButton else removeDelayedHideFabButton()) ~
        (dom.fabButton <~ (if (color != 0) fbaColor(color, colorDark) else Tweak.blank) <~ showFabMenu <~ vAddField(dom.autoHideKey, autoHide)) ~
        (if (color != 0) dom.fabMenu <~ changeItemsColor(color) else Ui.nop)
    }

  def hideFabButton: Ui[_] = removeDelayedHideFabButton() ~ (dom.fabButton <~ hideFabMenu)

  def changeItemsColor(color: Int) = Transformer {
    case item: FabItemMenu => item <~ fimBackgroundColor(color)
  }

  private[this] def updateBars(opened: Boolean): Ui[_] = if (opened) {
    updateBarsInFabMenuHide()
  } else {
    systemBarsTint.updateStatusToBlack()
  }

  private[this] def animFabButton(open: Boolean) = Transformer {
    case i: FabItemMenu if i.isType(dom.fabButtonItem) =>
      if (open) {
        i <~ vGone
      } else {
        val position = i.getPosition
        (i <~ animFabMenuItem(position)) ~
          (i.icon <~ animFabMenuIconItem(position)) ~
          (i.title <~ animFabMenuTitleItem(position))
      }
  }

  private[this] def resetDelayedHide = removeDelayedHideFabButton() ~ postDelayedHideFabButton

  private[this] def postDelayedHideFabButton = Ui {
    val runnable = new RunnableWrapper()
    handler.postDelayed(runnable, timeDelayFabButton)
    runnableHideFabButton = Option(runnable)
  }

  private[this] def removeDelayedHideFabButton() = Ui {
    runnableHideFabButton foreach handler.removeCallbacks
  }

  class RunnableWrapper extends Runnable {
    override def run(): Unit = (dom.fabButton <~ hideFabMenu).run
  }

  // Private utilities

  private[this] def exitTransition: Ui[Any] = {
    val activity = activityContextWrapper.getOriginal
    ((dom.tabs <~ exitViews) ~
      (dom.iconContent <~ exitViews)) ~
      (dom.viewPager <~~ exitViews) ~~
      Ui(activity.finish())
  }

  private[this] def getItemsForFabMenu = Seq(
    (w[FabItemMenu] <~ fabButtonApplicationsStyle <~ On.click {
      Ui(listener.showAppsDialog())
    }).get,
    (w[FabItemMenu] <~ fabButtonRecommendationsStyle <~ On.click {
      Ui(listener.showRecommendationsDialog())
    }).get,
    (w[FabItemMenu] <~ fabButtonContactsStyle <~ On.click {
      Ui(listener.showContactsDialog())
    }).get,
    (w[FabItemMenu] <~ fabButtonShortcutsStyle <~ On.click {
      Ui(listener.showShortcutsDialog())
    }).get
  )

  private[this] def showError(error: Int = R.string.contactUsError): Ui[Any] = dom.root <~ vSnackbarShort(error)

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast(message)

  private[this] def updateToolbarColor(color: Int): Ui[Any] =
    (dom.toolbar <~ vBackgroundColor(color)) ~
      systemBarsTint.updateStatusColor(color)

  private[this] def updateCollection(collection: Collection, position: Int, pageMovement: PageMovement): Ui[Any] =
    dom.getAdapter map { adapter =>
      val resIcon = collection.getIconDetail
      (pageMovement match {
        case Start | Idle =>
          dom.icon <~ ivSrc(resIcon)
        case Left =>
          dom.icon <~ animationIcon(fromLeft = true, resIcon)
        case Right | Jump =>
          dom.icon <~ animationIcon(fromLeft = false, resIcon)
        case _ => Ui.nop
      }) ~
        adapter.notifyChanged(position) ~
        (if (collection.cards.isEmpty) {
          val color = theme.getIndexColor(collection.themedColorIndex)
          showFabButton(color = color, autoHide = false)
        } else {
          hideFabButton
        })
    } getOrElse Ui.nop

  // Styles

  private[this] def tabsStyle: Tweak[SlidingTabLayout] =
    stlDefaultTextColor(theme.get(CollectionDetailTextTabDefaultColor)) +
      stlSelectedTextColor(theme.get(CollectionDetailTextTabSelectedColor)) +
      vInvisible

  private[this] def fabButtonApplicationsStyle: Tweak[FabItemMenu] =
    fabButtonStyle(R.string.applications, R.drawable.fab_menu_icon_applications, 1)

  private[this] def fabButtonRecommendationsStyle: Tweak[FabItemMenu] =
    fabButtonStyle(R.string.recommendations, R.drawable.fab_menu_icon_recommendations, 2)

  private[this] def fabButtonContactsStyle: Tweak[FabItemMenu] =
    fabButtonStyle(R.string.contacts, R.drawable.fab_menu_icon_contact, 3)

  private[this] def fabButtonShortcutsStyle: Tweak[FabItemMenu] =
    fabButtonStyle(R.string.shortcuts, R.drawable.fab_menu_icon_shorcut, 4)

  private[this] def fabButtonMenuStyle: Tweak[FloatingActionButton] = {
    val iconFabButton = PathMorphDrawable(
      defaultIcon = IconTypes.ADD,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default))
    ivSrc(iconFabButton) +
      vAddField(dom.opened, false) +
      vGone
  }

  private[this] def fabButtonStyle(title: Int, icon: Int, position: Int): Tweak[FabItemMenu] =
    vWrapContent +
      fimPopulate(resGetColor(R.color.collection_detail_fab_button_item), icon, title) +
      vGone +
      vSetType(dom.fabButtonItem) +
      vSetPosition(position)

  class OnPageChangeCollectionsListener(
    position: Int,
    updateToolbarColor: (Int) => Ui[Any],
    updateCollection: (Collection, Int, PageMovement) => Ui[Any])
    extends OnPageChangeListener {

    var lastPosition = -1

    var currentPosition = if (position == 0) position else -1

    var currentMovement: PageMovement = if (position == 0) Left else Loading

    private[this] def getColor(col: Collection): Int = theme.getIndexColor(col.themedColorIndex)

    private[this] def jump(from: Collection, to: Collection) = {
      val valueAnimator = ValueAnimator.ofInt(0, 100)
      valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        override def onAnimationUpdate(value: ValueAnimator): Unit = {
          val color = (getColor(from), getColor(to)).interpolateColors(value.getAnimatedFraction)
          updateToolbarColor(color).run
        }
      })
      valueAnimator.start()
    }

    override def onPageScrollStateChanged(state: Int): Unit = state match {
      case ViewPager.SCROLL_STATE_IDLE => currentMovement = Idle
      case ViewPager.SCROLL_STATE_DRAGGING => listener.closeEditingMode()
      case _ =>
    }

    override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit =
      currentMovement match {
        case Loading => // Nothing
        case Start => // First time, we change automatically the movement
          currentMovement = if (currentPosition > 0) Jump else Idle
        case Jump => // Nothing. The animation was triggered in onPageSelected
        case _ => // Scrolling to left or right
          for {
            current <- dom.getCollection(position)
            next <- dom.getCollection(position + 1)
          } yield {
            val color = (getColor(current), getColor(next)).interpolateColors(positionOffset)
            updateToolbarColor(color).run
          }
      }

    override def onPageSelected(position: Int): Unit = {
      val pageMovement: PageMovement = (position, currentPosition) match {
        case (p, cp) if cp == -1 => Start
        case (p, cp) if p > cp && p - cp > 1 => Jump
        case (p, cp) if p < cp && cp - p > 1 => Jump
        case (p, cp) if p < cp => Left
        case _ => Right
      }
      lastPosition = currentPosition
      currentPosition = position
      currentMovement = pageMovement
      pageMovement match {
        case Jump =>
          for {
            last <- dom.getCollection(lastPosition)
            current <- dom.getCollection(currentPosition)
          } yield jump(last, current)
        case _ =>
      }
      dom.getCollection(position) foreach { collection =>
        updateCollection(collection, position, pageMovement).run
      }
    }

  }

}

sealed trait PageMovement

case object Loading extends PageMovement

case object Left extends PageMovement

case object Right extends PageMovement

case object Start extends PageMovement

case object Idle extends PageMovement

case object Jump extends PageMovement