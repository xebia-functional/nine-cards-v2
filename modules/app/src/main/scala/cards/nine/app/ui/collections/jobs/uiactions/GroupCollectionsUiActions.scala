package cards.nine.app.ui.collections.jobs.uiactions

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.os.{Bundle, Handler}
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, View}
import android.widget.{ImageView, LinearLayout, TextView}
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.CollectionsPagerAdapter
import cards.nine.app.ui.collections.actions.apps.AppsFragment
import cards.nine.app.ui.collections.actions.recommendations.RecommendationsFragment
import cards.nine.app.ui.collections.snails.CollectionsSnails._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.drawables.tweaks.PathMorphDrawableTweaks._
import cards.nine.app.ui.components.drawables.{CollectionSelectorDrawable, IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.layouts.tweaks.FabItemMenuTweaks._
import cards.nine.app.ui.components.layouts.tweaks.SlidingTabLayoutTweaks._
import cards.nine.app.ui.components.layouts.{FabItemMenu, SlidingTabLayout}
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.NineCardsCategory
import cards.nine.models.types.theme.{CardLayoutBackgroundColor, CollectionDetailTextTabDefaultColor, CollectionDetailTextTabSelectedColor}
import cards.nine.models.{Card, Collection, NineCardsTheme}
import com.fortysevendeg.macroid.extras.FloatingActionButtonTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

class GroupCollectionsUiActions(val dom: GroupCollectionsDOM, listener: GroupCollectionsUiListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends ActionsBehaviours
  with ImplicitsUiExceptions {

  lazy val systemBarsTint = new SystemBarsTint

  lazy val selectorDrawable = CollectionSelectorDrawable()

  implicit def theme: NineCardsTheme = statuses.theme

  // Ui Actions

  def initialize(): TaskService[Unit] =
    ((dom.root <~ vBackgroundColor(statuses.theme.get(CardLayoutBackgroundColor))) ~
      (dom.tabs <~ tabsStyle) ~
      (dom.titleContent <~ vGone) ~
      (dom.titleName <~ titleNameStyle) ~
      (dom.selector <~ vGone <~ selectorStyle(selectorDrawable)) ~
      initFabButton ~
      loadMenuItems(getItemsForFabMenu)).toService

  def showCollections(collections: Seq[Collection], position: Int): TaskService[Unit] =
    (collections lift position match {
      case Some(collection) =>
        val adapter = CollectionsPagerAdapter(fragmentManagerContext.manager, collections, position)
        selectorDrawable.setNumberOfItems(collections.length)
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
          (dom.titleName <~ tvText(collection.name)) ~
          (dom.titleIcon <~ ivSrc(collection.getIconDetail)) ~
          (dom.tabs <~ vVisible <~~ enterViews)
      case _ => Ui.nop
    }).toService

  def showContactUsError: TaskService[Unit] = showError().toService

  def back(): TaskService[Unit] = (if (dom.isMenuOpened) {
    swapFabMenu()
  } else if (isActionShowed) {
    unrevealActionFragment
  } else if (listener.isEditingMode) {
    Ui(listener.closeEditingMode())
  } else {
    exitTransition
  }).toService

  def destroy(): TaskService[Unit] = Ui {
    dom.getAdapter foreach(_.clear())
  }.toService

  def resetAction: TaskService[Unit] =
    ((dom.fragmentContent <~ colorContentDialog(paint = false) <~ vClickable(false)) ~
      updateBarsInFabMenuHide()).toService

  def destroyAction: TaskService[Unit] = Ui(removeActionFragment).toService

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
  }.toService

  def editCard(collectionId: Int, cardId: Int, cardName: String): TaskService[Unit] =
    Ui (listener.showEditCollectionDialog(cardName, (maybeNewName) => {
      listener.saveEditedCard(collectionId, cardId, maybeNewName)
    })).toService

  def removeCards(cards: Seq[Card]): TaskService[Unit] = Ui {
    for {
      adapter <- dom.getAdapter
      currentPosition <- adapter.getCurrentFragmentPosition
    } yield {
      adapter.removeCardFromCollection(currentPosition, cards)
    }
  }.toService

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
  }.toService

  def reloadItemCollection(itemsSelected: Int, position: Int): TaskService[Unit] =
    (Ui(dom.invalidateOptionMenu) ~
      (dom.toolbarTitle <~ tvText(resGetString(R.string.itemsSelected, itemsSelected.toString))) ~
      Ui(dom.notifyItemChangedCollectionAdapter(position))).toService

  def showNoPhoneCallPermissionError(): TaskService[Unit] = showMessage(R.string.noPhoneCallPermissionMessage).toService

  def addCards(cards: Seq[Card]): TaskService[Unit] = Ui {
    for {
      adapter <- dom.getAdapter
      currentPosition <- adapter.getCurrentFragmentPosition
    } yield {
      adapter.addCardsToCollection(currentPosition, cards)
    }
  }.toService

  def openReorderModeUi(current: ScrollType, canScroll: Boolean): TaskService[Unit] = hideFabButton.toService

  def startEditing(items: Int): TaskService[Unit] =
    (Ui(dom.invalidateOptionMenu) ~
      (dom.toolbarTitle <~ tvText(resGetString(R.string.itemsSelected, items.toString))) ~
      (dom.getScrollType match {
        case Some(ScrollDown) =>
          dom.iconContent <~ applyAnimation(alpha = Some(0))
        case Some(ScrollUp) =>
          (dom.titleContent <~ applyAnimation(alpha = Some(0))) ~
            (dom.selector <~ applyAnimation(alpha = Some(0)))
        case _ => Ui.nop
      }) ~
      Ui(dom.notifyDataSetChangedCollectionAdapter())).toService

  def closeEditingModeUi(): TaskService[Unit] =
    ((dom.toolbarTitle <~ tvText("")) ~
      (dom.getScrollType match {
        case Some(ScrollDown) =>
          dom.iconContent <~ (vVisible + vScaleX(1) + vScaleY(1) + vAlpha(0f) ++ applyAnimation(alpha = Some(1)))
        case Some(ScrollUp) =>
          (dom.titleContent <~ applyAnimation(alpha = Some(1))) ~
            (dom.selector <~ applyAnimation(alpha = Some(1)))
        case _ => Ui.nop
      }) ~
      Ui(dom.notifyDataSetChangedCollectionAdapter()) ~
      Ui(dom.invalidateOptionMenu)).toService

  def showMenuButton(autoHide: Boolean = true, openMenu: Boolean = false, indexColor: Int): TaskService[Unit] = {
    val color = theme.getIndexColor(indexColor)
    (showFabButton(color, autoHide) ~
      (if (openMenu) swapFabMenu(forceOpen = true) else Ui.nop)).toService
  }

  def hideMenuButton(): TaskService[Unit] = hideFabButton.toService

  def close(): TaskService[Unit] = exitTransition.toService

  // FabButtonBehaviour

  def updateBarsInFabMenuHide(): Ui[Any] =
    dom.getCurrentCollection map (c => systemBarsTint.updateStatusColor(theme.getIndexColor(c.themedColorIndex))) getOrElse Ui.nop

  var runnableHideFabButton: Option[RunnableWrapper] = None

  val handler = new Handler()

  val timeDelayFabButton = 3000

  def initFabButton: Ui[_] =
    (dom.fabMenuContent <~ On.click(swapFabMenu()) <~ vClickable(false)) ~
      (dom.fabButton <~ fabButtonMenuStyle <~ On.click(swapFabMenu()))

  def loadMenuItems(items: Seq[FabItemMenu]): Ui[_] =
    dom.fabMenu <~ Tweak[LinearLayout] { view =>
      val param = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.END)
      items foreach (view.addView(_, 0, param))
    }

  def swapFabMenu(doUpdateBars: Boolean = true, forceOpen: Boolean = false): Ui[Any] = {
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

  def colorContentDialog(paint: Boolean) =
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
        (i <~ animFabMenuItem) ~
          (i.icon <~ animFabMenuIconItem) ~
          (i.title <~ animFabMenuTitleItem)
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
    ((dom.titleContent <~ applyAnimation(alpha = Some(0))) ~
      (dom.selector <~ applyAnimation(alpha = Some(0))) ~
      (dom.tabs <~ exitViews) ~
      (dom.iconContent <~ exitViews)) ~
      (dom.viewPager <~~ exitViews) ~~
      Ui(activity.finish())
  }

  private[this] def getItemsForFabMenu = Seq(
    (w[FabItemMenu] <~ fabButtonApplicationsStyle <~ FuncOn.click {
      view: View =>
        val collection = dom.getCurrentCollection
        val category = collection flatMap (_.appsCategory)
        val map = category map (cat => Map(AppsFragment.categoryKey -> cat)) getOrElse Map.empty
        val packages = (collection map (_.cards flatMap (_.packageName))).toSeq.flatten
        val args = createBundle(view, map, packages)
        startDialog() ~ listener.showAppsDialog(args)
    }).get,
    (w[FabItemMenu] <~ fabButtonRecommendationsStyle <~ FuncOn.click {
      view: View =>
        val collection = dom.getCurrentCollection
        val packages = collection map (_.cards flatMap (_.packageName)) getOrElse Seq.empty
        val category = collection flatMap (_.appsCategory)
        val map = category map (cat => Map(RecommendationsFragment.categoryKey -> cat)) getOrElse Map.empty
        if (category.isEmpty && packages.isEmpty) {
          showError(R.string.recommendationError)
        } else {
          val args = createBundle(view, map)
          startDialog() ~ listener.showRecommendationsDialog(args)
        }
    }).get,
    (w[FabItemMenu] <~ fabButtonContactsStyle <~ FuncOn.click {
      view: View => {
        val args = createBundle(view)
        startDialog() ~ listener.showContactsDialog(args)
      }
    }).get,
    (w[FabItemMenu] <~ fabButtonShortcutsStyle <~ FuncOn.click {
      view: View => {
        val args = createBundle(view)
        startDialog() ~ listener.showShortcutsDialog(args)
      }
    }).get
  )

  private[this] def showError(error: Int = R.string.contactUsError): Ui[Any] = dom.root <~ vSnackbarShort(error)

  private[this] def showMessage(message: Int): Ui[Any] = uiShortToast2(message)

  private[this] def updateToolbarColor(color: Int): Ui[Any] =
    (dom.toolbar <~ vBackgroundColor(color)) ~
      systemBarsTint.updateStatusColor(color)

  private[this] def updateCollection(collection: Collection, position: Int, pageMovement: PageMovement): Ui[Any] =
    dom.getAdapter map { adapter =>
      val resIcon = collection.getIconDetail
      val distance = resGetDimensionPixelSize(R.dimen.padding_large)
      val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
      ((pageMovement, adapter.statuses.scrollType) match {
        case (Start | Idle, _) =>
          (dom.icon <~ ivSrc(resIcon)) ~
            (dom.titleName <~ tvText(collection.name)) ~
            (dom.titleIcon <~ ivSrc(resIcon))
        case (Left, ScrollDown) =>
          (dom.icon <~ animationIcon(fromLeft = true, resIcon)) ~
            (dom.titleName <~ tvText(collection.name)) ~
            (dom.titleIcon <~ ivSrc(resIcon))
        case (Left, ScrollUp) =>
          (dom.icon <~ ivSrc(resIcon)) ~
            (dom.titleContent <~~
              applyAnimation(
                duration = Option(duration),
                x = Option(distance),
                alpha = Option(0))) ~~
            (dom.titleContent <~ vTranslationX(-distance)) ~~
            (dom.titleName <~ tvText(collection.name)) ~~
            (dom.titleIcon <~ ivSrc(resIcon)) ~~
            (dom.titleContent <~~
              applyAnimation(
                duration = Option(duration),
                x = Option(0),
                alpha = Option(1)))
        case (Right | Jump, ScrollDown) =>
          (dom.icon <~ animationIcon(fromLeft = false, resIcon)) ~
            (dom.titleName <~ tvText(collection.name)) ~
            (dom.titleIcon <~ ivSrc(resIcon))
        case (Right | Jump, ScrollUp) =>
          (dom.icon <~ ivSrc(resIcon)) ~
            (dom.titleContent <~~
              applyAnimation(
                duration = Option(duration),
                x = Option(-distance),
                alpha = Option(0))) ~~
            (dom.titleContent <~ vTranslationX(distance)) ~~
            (dom.titleName <~ tvText(collection.name)) ~~
            (dom.titleIcon <~ ivSrc(resIcon)) ~~
            (dom.titleContent <~~
              applyAnimation(
                duration = Option(duration),
                x = Option(0),
                alpha = Option(1)))
        case _ => Ui.nop
      }) ~
        Ui(selectorDrawable.setSelected(position)) ~
        adapter.notifyChanged(position) ~
        (if (collection.cards.isEmpty) {
          val color = theme.getIndexColor(collection.themedColorIndex)
          showFabButton(color = color, autoHide = false)
        } else {
          hideFabButton
        })
    } getOrElse Ui.nop

  private[this] def createBundle(view: View, map: Map[String, NineCardsCategory] = Map.empty, packages: Seq[String] = Seq.empty): Bundle = {
    val sizeIconFabMenuItem = resGetDimensionPixelSize(R.dimen.size_fab_menu_item)
    val sizeFabButton = dom.fabButton.getWidth
    val (startX: Int, startY: Int) = Option(view.findViewById(R.id.fab_icon)) map (_.calculateAnchorViewPosition) getOrElse(0, 0)
    val (endX: Int, endY: Int) = dom.fabButton.calculateAnchorViewPosition
    val args = new Bundle()
    args.putInt(BaseActionFragment.sizeIcon, sizeIconFabMenuItem)
    args.putInt(BaseActionFragment.startRevealPosX, startX + (sizeIconFabMenuItem / 2))
    args.putInt(BaseActionFragment.startRevealPosY, startY + (sizeIconFabMenuItem / 2))
    args.putInt(BaseActionFragment.endRevealPosX, endX + (sizeFabButton / 2))
    args.putInt(BaseActionFragment.endRevealPosY, endY + (sizeFabButton / 2))
    args.putStringArray(BaseActionFragment.packages, packages.toArray)
    map foreach (item => {
      val (categoryKey, category) = item
      args.putString(categoryKey, category.name)
    })
    dom.getCurrentCollection foreach (c =>
      args.putInt(BaseActionFragment.colorPrimary, theme.getIndexColor(c.themedColorIndex)))
    args
  }

  private[this] def startDialog(): Ui[Any] = {
    swapFabMenu(doUpdateBars = false) ~
      (dom.fragmentContent <~ colorContentDialog(paint = true) <~ vClickable(true))
  }

  // Styles

  private[this] def tabsStyle: Tweak[SlidingTabLayout] =
    stlDefaultTextColor(theme.get(CollectionDetailTextTabDefaultColor)) +
      stlSelectedTextColor(theme.get(CollectionDetailTextTabSelectedColor)) +
      vInvisible

  private[this]def titleNameStyle: Tweak[TextView] =
    tvColor(theme.get(CollectionDetailTextTabSelectedColor))

  private[this] def selectorStyle(drawable: Drawable): Tweak[ImageView] =
    ivSrc(drawable)

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