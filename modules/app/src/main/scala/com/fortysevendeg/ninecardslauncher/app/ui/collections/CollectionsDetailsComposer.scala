package com.fortysevendeg.ninecardslauncher.app.ui.collections

import java.util

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.SharedElementCallback
import android.os.{Build, Bundle}
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.{Transition, TransitionInflater}
import android.view.{Gravity, View, ViewGroup}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.Snails._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.contacts.ContactsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.recommendations.RecommendationsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts.ShortcutFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{ActionsBehaviours, BaseActionFragment}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SnailsCommons, FabButtonBehaviour, SystemBarsTint}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.FabItemMenu
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CollectionDetailBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

trait CollectionsDetailsComposer
  extends Styles
  with ActionsBehaviours
  with FabButtonBehaviour {

  self: AppCompatActivity with SystemBarsTint with TypedFindView with Contexts[AppCompatActivity] =>

  val resistanceDisplacement = .2f

  val resistanceScale = .05f

  lazy val iconIndicatorDrawable = new PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val elevation = resGetDimensionPixelSize(R.dimen.elevation_toolbar)

  lazy val maxHeightToolbar = resGetDimensionPixelSize(R.dimen.height_toolbar_collection_details)

  lazy val toolbar = Option(findView(TR.collections_toolbar))

  lazy val root = Option(findView(TR.collections_root))

  lazy val viewPager = Option(findView(TR.collections_view_pager))

  lazy val tabs = Option(findView(TR.collections_tabs))

  lazy val iconContent = Option(findView(TR.collections_icon_content))

  lazy val icon = Option(findView(TR.collections_icon))

  def updateBarsInFabMenuShow: Ui[_] = updateStatusToBlack

  def updateBarsInFabMenuHide: Ui[_] =
    getCurrentCollection map (c => updateStatusColor(resGetColor(getIndexColor(c.themedColorIndex)))) getOrElse Ui.nop

  def initUi(indexColor: Int, iconCollection: String)(implicit theme: NineCardsTheme) =
    (tabs <~ tabsStyle <~ vInvisible) ~
      initFabButton ~
      loadMenuItems(getItemsForFabMenu) ~
      updateToolbarColor(resGetColor(getIndexColor(indexColor))) ~
      (icon <~ ivSrc(iconCollectionDetail(iconCollection)))

  def showError(error: Int = R.string.contactUsError): Ui[_] = root <~ uiSnackbarShort(error)

  def drawCollections(collections: Seq[Collection], position: Int)
    (implicit manager: FragmentManagerContext[Fragment, FragmentManager], theme: NineCardsTheme) = {
    val adapter = CollectionsPagerAdapter(manager.get, collections, position)
    (root <~ SnailsCommons.fadeBackground(theme.get(CollectionDetailBackgroundColor))) ~
      (viewPager <~ vpAdapter(adapter)) ~
      Ui(adapter.activateFragment(position)) ~
      (tabs <~
        stlViewPager(viewPager) <~
        stlOnPageChangeListener(
          new OnPageChangeCollectionsListener(collections, position, updateToolbarColor, updateCollection))) ~
      uiHandler(viewPager <~ Tweak[ViewPager](_.setCurrentItem(position, false))) ~
      uiHandlerDelayed(Ui { getActiveFragment foreach (_.bindAnimatedAdapter) }, 100) ~
      (tabs <~ vVisible <~~ enterViews)
  }

  def pullCloseScrollY(scroll: Int, scrollType: ScrollType, close: Boolean): Ui[_] = {
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

  def translationScrollY(scroll: Int): Ui[_] = {
    val move = math.min(scroll, spaceMove)
    val ratio: Float = move.toFloat / spaceMove.toFloat
    val newElevation = elevation + (if (ratio >= 1) 1 else 0)
    val scale = 1 - (ratio / 2)
    (tabs <~ vTranslationY(-move) <~ uiElevation(newElevation)) ~
      (toolbar <~ tbReduceLayout(move * 2) <~ uiElevation(newElevation)) ~
      (iconContent <~ uiElevation(newElevation) <~ vScaleX(scale) <~ vScaleY(scale) <~ vAlpha(1 - ratio))
  }

  def openReorderModeUi(current: ScrollType): Ui[_] =
    (tabs <~~
      applyAnimation(y = Some(-spaceMove), alpha = Some(0f)) <~
      uiElevation(elevation + 1) <~
      vInvisible) ~
      (toolbar <~~
        applyAnimation(onUpdate = (ratio) => current match {
          case ScrollDown => toolbar <~ tbReduceLayout(calculateReduce(ratio, spaceMove, reversed = false))
          case _ => Ui.nop
        }) <~
        uiElevation(elevation + 1)) ~
      (iconContent <~ vInvisible)

  def closeReorderModeUi: Ui[_] =
    (tabs <~
      vVisible <~~
      applyAnimation(y = Some(0f), alpha = Some(1f)) <~
      uiElevation(elevation)) ~
      (toolbar <~
        applyAnimation(onUpdate = (ratio) => toolbar <~ tbReduceLayout(calculateReduce(ratio, spaceMove, reversed = true))) <~
        uiElevation(elevation)) ~
      (iconContent <~ vVisible <~ vScaleX(1) <~ vScaleY(1) <~ vAlpha(1))

  def notifyScroll(sType: ScrollType): Ui[_] = (for {
    vp <- viewPager
    adapter <- getAdapter
  } yield {
      adapter.setScrollType(sType)
      adapter.notifyChanged(vp.getCurrentItem)
    }) getOrElse Ui.nop

  private[this] def calculateReduce(ratio: Float, spaceMove: Int, reversed: Boolean) = {
    val newRatio = if (reversed) 1f - ratio else ratio
    (newRatio * (spaceMove * 2)).toInt
  }

  private[this] def getItemsForFabMenu(implicit theme: NineCardsTheme) = Seq(
    getUi(w[FabItemMenu] <~ fabButtonApplicationsStyle <~ FuncOn.click {
      view: View =>
        val category = getCurrentCollection flatMap (_.appsCategory)
        val map = category map (cat => Map(AppsFragment.categoryKey -> cat)) getOrElse Map.empty
        showAction(f[AppsFragment], view, map)
    }),
    getUi(w[FabItemMenu] <~ fabButtonRecommendationsStyle <~ FuncOn.click {
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
    }),
    getUi(w[FabItemMenu] <~ fabButtonContactsStyle <~ FuncOn.click {
      view: View => showAction(f[ContactsFragment], view)
    }),
    getUi(w[FabItemMenu] <~ fabButtonShortcutsStyle <~ FuncOn.click {
      view: View => showAction(f[ShortcutFragment], view)
    })
  )

  private[this] def tbReduceLayout(reduce: Int) = Tweak[Toolbar] { view =>
    view.getLayoutParams.height = maxHeightToolbar - reduce
    view.requestLayout()
  }

  private[this] def uiElevation(elevation: Float) = Lollipop.ifSupportedThen {
    vElevation(elevation)
  }.getOrElse(Tweak.blank)

  def getAdapter: Option[CollectionsPagerAdapter] = viewPager flatMap (ad => Option(ad.getAdapter)) flatMap {
    case adapter: CollectionsPagerAdapter => Some(adapter)
    case _ => None
  }

  def getCurrentPosition: Option[Int] = getAdapter flatMap ( _.getCurrentFragmentPosition )

  def getCurrentCollection: Option[Collection] = getAdapter flatMap { adapter =>
    adapter.getCurrentFragmentPosition flatMap adapter.collections.lift
  }

  def getCollection(position: Int): Option[Collection] = getAdapter flatMap (_.collections.lift(position))

  def getActiveFragment(): Option[CollectionFragment] = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
  } yield fragment

  def turnOffFragmentContent(implicit activityContextWrapper: ActivityContextWrapper): Ui[_] =
    (fragmentContent <~
      colorContentDialog(paint = false) <~
      vClickable(false)) ~ updateBarsInFabMenuHide

  protected def addCardsToCurrentFragment(c: Seq[Card]) = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
    currentPosition <- adapter.getCurrentFragmentPosition
  } yield {
      adapter.addCardsToCollection(currentPosition, c)
      fragment.addCards(c)
    }

  protected def removeCardFromCurrentFragment(c: Card) = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
    currentPosition <- adapter.getCurrentFragmentPosition
  } yield {
      adapter.removeCardFromCollection(currentPosition, c)
      fragment.removeCard(c)
    }

  protected def reloadCardsToCurrentFragment(cards: Seq[Card], reloadFragment: Boolean) = for {
    adapter <- getAdapter
    fragment <- adapter.getActiveFragment
    currentPosition <- adapter.getCurrentFragmentPosition
  } yield {
    adapter.updateCardFromCollection(currentPosition, cards)
    if (reloadFragment) fragment.reloadCards(cards)
  }

  def backByPriority(implicit theme: NineCardsTheme): Ui[_] = if (fabMenuOpened) {
    swapFabButton()
  } else if (isActionShowed) {
    unrevealActionFragment
  } else {
    exitTransition
  }

  def exitTransition(implicit theme: NineCardsTheme) =
    ((toolbar <~ exitViews()) ~
      (tabs <~ exitViews()) ~
      (iconContent <~ exitViews()) ~
      (root <~ vBackgroundColorResource(android.R.color.transparent))) ~
      (viewPager <~~ exitViews(up = false)) ~~
      Ui(finish())

  def configureEnterTransition(
    position: Int,
    end: (() => Unit)) = Lollipop.ifSupportedThen {
    configureEnterTransitionLollipop(position, end)
  } getOrElse end()

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private[this] def configureEnterTransitionLollipop(
    position: Int,
    end: (() => Unit)) = {

    val enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.shared_element_enter_collection_detail)
    getWindow.setSharedElementEnterTransition(enterTransition)

    iconContent foreach (_.setTransitionName(getContentTransitionName(position)))

    setEnterSharedElementCallback(new SharedElementCallback {

      var snapshot: Option[View] = None

      override def onSharedElementStart(
        sharedElementNames: util.List[String],
        sharedElements: util.List[View],
        sharedElementSnapshots: util.List[View]): Unit = {
        addSnapshot(sharedElementNames, sharedElements, sharedElementSnapshots, relayoutContainer = false)
        snapshot foreach (_.setVisibility(View.VISIBLE))
        findViewById(R.id.collections_toolbar).setVisibility(View.INVISIBLE)
      }

      override def onSharedElementEnd(
        sharedElementNames: util.List[String],
        sharedElements: util.List[View],
        sharedElementSnapshots: util.List[View]): Unit = {
        addSnapshot(sharedElementNames, sharedElements, sharedElementSnapshots, relayoutContainer = true)
        snapshot foreach (_.setVisibility(View.INVISIBLE))
        findViewById(R.id.collections_toolbar).setVisibility(View.VISIBLE)
      }

      override def onMapSharedElements(
        names: util.List[String],
        sharedElements: util.Map[String, View]): Unit =
        findViewById(R.id.collections_toolbar).setVisibility(View.INVISIBLE)

      private[this] def addSnapshot(
        sharedElementNames: util.List[String],
        sharedElements: util.List[View],
        sharedElementSnapshots: util.List[View],
        relayoutContainer: Boolean) = {
        if (snapshot.isEmpty) {
          val transitionName = getContentTransitionName(position)
          sharedElementNames.zipWithIndex foreach {
            case (name, index) if name.equals(transitionName) =>
              val element = sharedElements.get(index).asInstanceOf[FrameLayout]
              val snapshotView = sharedElementSnapshots.get(index)
              val width = snapshotView.getWidth
              val height = snapshotView.getHeight
              val layoutParams = new FrameLayout.LayoutParams(width, height)
              layoutParams.gravity = Gravity.CENTER
              val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
              val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
              snapshotView.measure(widthSpec, heightSpec)
              snapshotView.layout(0, 0, width, height)
              snapshotView.setTransitionName(snapshotName)
              if (relayoutContainer) {
                val container = findViewById(R.id.collections_root).asInstanceOf[ViewGroup]
                val left = (container.getWidth - width) / 2
                val top = (container.getHeight - height) / 2
                element.measure(widthSpec, heightSpec)
                element.layout(left, top, left + width, top + height)
              }
              snapshot = Option(snapshotView)
              element.addView(snapshotView, layoutParams)
            case _ =>
          }
        }
      }

    })

    getWindow.getSharedElementEnterTransition.addListener(new Transition.TransitionListener {
      override def onTransitionStart(transition: Transition): Unit = {}

      override def onTransitionCancel(transition: Transition): Unit = {}

      override def onTransitionEnd(transition: Transition): Unit = end()

      override def onTransitionPause(transition: Transition): Unit = {}

      override def onTransitionResume(transition: Transition): Unit = {}
    })
  }

  private[this] def updateCollection(collection: Collection, position: Int, pageMovement: PageMovement): Ui[_] = getAdapter map {
    adapter =>
      (pageMovement match {
        case Start | Idle => icon <~ ivSrc(iconCollectionDetail(collection.icon))
        case Left => icon <~ changeIcon(iconCollectionDetail(collection.icon), fromLeft = true)
        case Right | Jump => icon <~ changeIcon(iconCollectionDetail(collection.icon), fromLeft = false)
        case _ => Ui.nop
      }) ~ adapter.notifyChanged(position) ~ hideFabButton
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
    swapFabButton(doUpdateBars = false) ~
      (fragmentContent <~ colorContentDialog(paint = true) <~ vClickable(true)) ~
      addFragment(fragmentBuilder.pass(args), Option(R.id.action_fragment_content), Option(nameActionFragment))
  }

}

sealed trait PageMovement

case object Loading extends PageMovement

case object Left extends PageMovement

case object Right extends PageMovement

case object Start extends PageMovement

case object Idle extends PageMovement

case object Jump extends PageMovement

class OnPageChangeCollectionsListener(
  collections: Seq[Collection],
  position: Int,
  updateToolbarColor: (Int) => Ui[_],
  updateCollection: (Collection, Int, PageMovement) => Ui[_])
  (implicit context: ContextWrapper, theme: NineCardsTheme)
  extends OnPageChangeListener {

  var lastPosition = -1

  var currentPosition = if (position == 0) position else -1

  var currentMovement: PageMovement = if (position == 0) Left else Loading

  private[this] def getColor(col: Collection): Int = resGetColor(getIndexColor(col.themedColorIndex))

  private[this] def jump(from: Collection, to: Collection) = {
    val valueAnimator = ValueAnimator.ofInt(0, 100)
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      override def onAnimationUpdate(value: ValueAnimator): Unit = {
        val color = interpolateColors(value.getAnimatedFraction, getColor(from), getColor(to))
        runUi(updateToolbarColor(color))
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
        val selectedCollection: Collection = collections(position)
        val nextCollection: Option[Collection] = collections.lift(position + 1)
        nextCollection map { next =>
          val color = interpolateColors(positionOffset, getColor(selectedCollection), getColor(next))
          runUi(updateToolbarColor(color))
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
      case Jump => jump(collections(lastPosition), collections(currentPosition))
      case _ =>
    }
    runUi(updateCollection(collections(position), position, pageMovement))
  }

}
