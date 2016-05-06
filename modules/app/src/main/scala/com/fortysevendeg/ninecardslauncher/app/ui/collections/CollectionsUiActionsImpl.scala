package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.snails.CollectionsSnails
import CollectionsSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ContactsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations.RecommendationsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts.ShortcutFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.styles.Styles
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FabButtonBehaviour, SnailsCommons, SystemBarsTint}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FabItemMenu
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CollectionDetailBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

trait CollectionsUiActionsImpl
  extends CollectionsUiActions
  with Styles
  with ActionsBehaviours
  with FabButtonBehaviour {

  self: SystemBarsTint with TypedFindView with Contexts[AppCompatActivity] =>

  implicit val presenter: CollectionsPagerPresenter

  implicit lazy val theme: NineCardsTheme = presenter.getTheme

  val resistanceDisplacement = .2f

  val resistanceScale = .05f

  lazy val iconIndicatorDrawable = new PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val elevation = resGetDimensionPixelSize(R.dimen.elevation_collection_default)

  lazy val elevationUp = resGetDimensionPixelSize(R.dimen.elevation_collection_up)

  lazy val maxHeightToolbar = resGetDimensionPixelSize(R.dimen.height_toolbar_collection_details)

  lazy val toolbar = Option(findView(TR.collections_toolbar))

  lazy val root = Option(findView(TR.collections_root))

  lazy val viewPager = Option(findView(TR.collections_view_pager))

  lazy val tabs = Option(findView(TR.collections_tabs))

  lazy val iconContent = Option(findView(TR.collections_icon_content))

  lazy val icon = Option(findView(TR.collections_icon))

  def updateBarsInFabMenuShow(): Ui[_] = updateStatusToBlack

  def updateBarsInFabMenuHide(): Ui[_] =
    getCurrentCollection map (c => updateStatusColor(resGetColor(getIndexColor(c.themedColorIndex)))) getOrElse Ui.nop

  override def initialize(indexColor: Int, iconCollection: String, isStateChanged: Boolean): Ui[Any] =
    (tabs <~ tabsStyle) ~
      initFabButton ~
      loadMenuItems(getItemsForFabMenu) ~
      updateToolbarColor(resGetColor(getIndexColor(indexColor))) ~
      (icon <~ ivSrc(iconCollectionDetail(iconCollection))) ~
      Ui(initSystemStatusBarTint) ~
      (if (isStateChanged) Ui.nop else toolbar <~ enterToolbar)

  override def back(): Ui[Any] = if (isMenuOpened) {
    swapFabMenu()
  } else if (isActionShowed) {
    unrevealActionFragment
  } else {
    exitTransition
  }

  override def destroy(): Ui[Any] = Ui {
    getAdapter foreach(_.clear())
  }

  override def showCollections(collections: Seq[Collection], position: Int): Ui[Any] =
    activityContextWrapper.getOriginal match {
      case fragmentActivity: FragmentActivity =>
        val adapter = CollectionsPagerAdapter(fragmentActivity.getSupportFragmentManager, collections, position)
        (root <~ SnailsCommons.fadeBackground(theme.get(CollectionDetailBackgroundColor))) ~
          (viewPager <~ vpAdapter(adapter)) ~
          Ui(adapter.activateFragment(position)) ~
          (tabs <~
            stlViewPager(viewPager) <~
            stlOnPageChangeListener(
              new OnPageChangeCollectionsListener(position, updateToolbarColor, updateCollection))) ~
          uiHandler(viewPager <~ Tweak[ViewPager](_.setCurrentItem(position, false))) ~
          uiHandlerDelayed(Ui { getActivePresenter foreach (_.bindAnimatedAdapter()) }, 100) ~
          (tabs <~ vVisible <~~ enterViews) ~
          elevationsDefault
      case _ => Ui.nop
    }

  override def reloadCards(cards: Seq[Card], reloadFragments: Boolean): Ui[Any] = Ui {
    for {
      adapter <- getAdapter
      presenter <- getActivePresenter
      currentPosition <- adapter.getCurrentFragmentPosition
    } yield {
      adapter.updateCardFromCollection(currentPosition, cards)
      if (reloadFragments) presenter.reloadCards(cards)
    }
  }

  override def addCards(cards: Seq[Card]): Ui[Any] = Ui {
    for {
      adapter <- getAdapter
      presenter <- getActivePresenter
      currentPosition <- adapter.getCurrentFragmentPosition
    } yield {
      adapter.addCardsToCollection(currentPosition, cards)
      presenter.addCards(cards)
    }
  }

  override def removeCards(card: Card): Ui[Any] = Ui {
    for {
      adapter <- getAdapter
      presenter <- getActivePresenter
      currentPosition <- adapter.getCurrentFragmentPosition
    } yield {
      adapter.removeCardFromCollection(currentPosition, card)
      presenter.removeCard(card)
    }
  }

  override def showContactUsError: Ui[Any] = showError()

  override def showMessageNotImplemented: Ui[Any]= showError(R.string.todo)

  override def getCurrentCollection: Option[Collection] = getAdapter flatMap { adapter =>
    adapter.getCurrentFragmentPosition flatMap adapter.collections.lift
  }

  override def pullCloseScrollY(scroll: Int, scrollType: ScrollType, close: Boolean): Ui[_] = {
    val displacement = scroll * resistanceDisplacement
    val distanceToValidClose = resGetDimension(R.dimen.distance_to_valid_action)
    val scale = 1f + ((scroll / distanceToValidClose) * resistanceScale)
    (tabs <~ (scrollType match {
      case ScrollDown => vTranslationY(displacement)
      case _ => Tweak.blank
    })) ~
      (iconContent <~ vScaleX(scale) <~ vScaleY(scale) <~ vTranslationY(displacement)) ~
      Ui {
        val newIcon = if (close) IconTypes.CLOSE else IconTypes.BACK
        if (iconIndicatorDrawable.currentTypeIcon != newIcon && !iconIndicatorDrawable.isRunning) {
          iconIndicatorDrawable.setToTypeIcon(newIcon)
          iconIndicatorDrawable.start()
        }
      }
  }

  override def translationScrollY(scroll: Int): Ui[_] = {
    val move = math.min(scroll, spaceMove)
    val ratio: Float = move.toFloat / spaceMove.toFloat
    val isTop = ratio >= 1
    val scale = 1 - (ratio / 2)
    (tabs <~ vTranslationY(-move)) ~
      (toolbar <~ tbReduceLayout(move * 2)) ~
      (iconContent <~ vScaleX(scale) <~ vScaleY(scale) <~ vAlpha(1 - ratio)) ~
      (if (isTop) elevationsUp else elevationsDefault)
  }

  override def openReorderModeUi(current: ScrollType, canScroll: Boolean): Ui[_] =
    hideFabButton ~
      ((toolbar <~~
        applyAnimation(onUpdate = (ratio) => current match {
          case ScrollDown =>
            (tabs <~ vTranslationY(-ratio * spaceMove)) ~
              (toolbar <~ tbReduceLayout(calculateReduce(ratio, spaceMove, reversed = false)))
          case _ => Ui.nop
        })) ~
        elevationsUp ~
        (iconContent <~ vAlpha(0))).ifUi(canScroll)

  override def notifyScroll(sType: ScrollType): Ui[_] = (for {
    vp <- viewPager
    adapter <- getAdapter
  } yield {
      adapter.setScrollType(sType)
      adapter.notifyChanged(vp.getCurrentItem)
    }) getOrElse Ui.nop

  override def exitTransition: Ui[Any] = {
    val activity = activityContextWrapper.getOriginal
    ((toolbar <~ exitToolbar) ~
      (tabs <~ exitViews) ~
      (iconContent <~ exitViews)) ~
      (viewPager <~~ exitViews) ~~
      Ui(activity.finish())
  }

  override def showMenuButton(autoHide: Boolean = true, collection: Collection): Ui[Any] = {
    val color = getIndexColor(collection.themedColorIndex)
    showFabButton(color, autoHide)
  }

  override def hideMenuButton: Ui[Any] = hideFabButton

  override def resetAction: Ui[Any] = turnOffFragmentContent

  override def destroyAction: Ui[Any] = Ui(removeActionFragment)

  private[this] def showError(error: Int = R.string.contactUsError): Ui[_] = root <~ vSnackbarShort(error)

  private[this] def elevationsDefault: Ui[_] =
    (viewPager <~ vElevation(elevation)) ~
      (tabs <~ vElevation(elevation)) ~
      (toolbar <~ vElevation(elevation)) ~
      (iconContent <~ vElevation(elevation))

  private[this] def elevationsUp: Ui[_] =
    (viewPager <~ vElevation(elevation)) ~
      (tabs <~ vElevation(elevationUp)) ~
      (toolbar <~ vElevation(elevationUp)) ~
      (iconContent <~ vElevation(elevationUp))

  private[this] def calculateReduce(ratio: Float, spaceMove: Int, reversed: Boolean) = {
    val newRatio = if (reversed) 1f - ratio else ratio
    (newRatio * (spaceMove * 2)).toInt
  }

  private[this] def getItemsForFabMenu = Seq(
    (w[FabItemMenu] <~ fabButtonApplicationsStyle <~ FuncOn.click {
      view: View =>
        val category = getCurrentCollection flatMap (_.appsCategory)
        val map = category map (cat => Map(AppsFragment.categoryKey -> cat)) getOrElse Map.empty
        showAction(f[AppsFragment], view, map)
    }).get,
    (w[FabItemMenu] <~ fabButtonRecommendationsStyle <~ FuncOn.click {
      view: View =>
        val collection = getCurrentCollection
        val packages = collection map (_.cards flatMap (_.packageName)) getOrElse Seq.empty
        val category = collection flatMap (_.appsCategory)
        val map = category map (cat => Map(RecommendationsFragment.categoryKey -> cat)) getOrElse Map.empty
        if (category.isEmpty && packages.isEmpty) {
          showError(R.string.recommendationError)
        } else {
          showAction(f[RecommendationsFragment], view, map, packages)
        }
    }).get,
    (w[FabItemMenu] <~ fabButtonContactsStyle <~ FuncOn.click {
      view: View => showAction(f[ContactsFragment], view)
    }).get,
    (w[FabItemMenu] <~ fabButtonShortcutsStyle <~ FuncOn.click {
      view: View => showAction(f[ShortcutFragment], view)
    }).get
  )

  private[this] def tbReduceLayout(reduce: Int) = Tweak[Toolbar] { view =>
    view.getLayoutParams.height = maxHeightToolbar - reduce
    view.requestLayout()
  }

  private[this] def getAdapter: Option[CollectionsPagerAdapter] = viewPager flatMap (ad => Option(ad.getAdapter)) flatMap {
    case adapter: CollectionsPagerAdapter => Some(adapter)
    case _ => None
  }

  def getCurrentPosition: Option[Int] = getAdapter flatMap ( _.getCurrentFragmentPosition )

  private[this] def getCollection(position: Int): Option[Collection] = getAdapter flatMap (_.collections.lift(position))

  private[this] def getActivePresenter: Option[CollectionPresenter] = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
  } yield fragment.presenter

  override def turnOffFragmentContent: Ui[_] =
    (fragmentContent <~
      colorContentDialog(paint = false) <~
      vClickable(false)) ~ updateBarsInFabMenuHide

  private[this] def updateCollection(collection: Collection, position: Int, pageMovement: PageMovement): Ui[_] = getAdapter map {
    adapter =>
      (pageMovement match {
        case Start | Idle => icon <~ ivSrc(iconCollectionDetail(collection.icon))
        case Left => icon <~ changeIcon(iconCollectionDetail(collection.icon), fromLeft = true)
        case Right | Jump => icon <~ changeIcon(iconCollectionDetail(collection.icon), fromLeft = false)
        case _ => Ui.nop
      }) ~ adapter.notifyChanged(position) ~ (if (collection.cards.isEmpty) {
        val color = getIndexColor(collection.themedColorIndex)
        showFabButton(color = color, autoHide = false)
      } else {
        hideFabButton
      })
  } getOrElse Ui.nop

  private[this] def updateToolbarColor(color: Int): Ui[_] =
    (toolbar <~ vBackgroundColor(color)) ~
      updateStatusColor(color)

  private[this] def showAction[F <: BaseActionFragment]
  (fragmentBuilder: FragmentBuilder[F], view: View, map: Map[String, NineCardCategory] = Map.empty, packages: Seq[String] = Seq.empty): Ui[_] = {
    val sizeIconFabMenuItem = resGetDimensionPixelSize(R.dimen.size_fab_menu_item)
    val sizeFabButton = fabButton map (_.getWidth) getOrElse 0
    val (startX: Int, startY: Int) = Option(view.findViewById(R.id.fab_icon)) map calculateAnchorViewPosition getOrElse(0, 0)
    val (endX: Int, endY: Int) = fabButton map calculateAnchorViewPosition getOrElse(0, 0)
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
    getCurrentCollection foreach (c =>
      args.putInt(BaseActionFragment.colorPrimary, resGetColor(getIndexColor(c.themedColorIndex))))
    swapFabMenu(doUpdateBars = false) ~
      (fragmentContent <~ colorContentDialog(paint = true) <~ vClickable(true)) ~
      addFragment(fragmentBuilder.pass(args), Option(R.id.action_fragment_content), Option(nameActionFragment))
  }


  class OnPageChangeCollectionsListener(
   position: Int,
   updateToolbarColor: (Int) => Ui[_],
   updateCollection: (Collection, Int, PageMovement) => Ui[_])
    extends OnPageChangeListener {

    var lastPosition = -1

    var currentPosition = if (position == 0) position else -1

    var currentMovement: PageMovement = if (position == 0) Left else Loading

    private[this] def getColor(col: Collection): Int = resGetColor(getIndexColor(col.themedColorIndex))

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
            current <- getCollection(position)
            next <- getCollection(position + 1)
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
            last <- getCollection(lastPosition)
            current <- getCollection(currentPosition)
          } yield jump(last, current)
        case _ =>
      }
      getCollection(position) foreach { collection =>
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

