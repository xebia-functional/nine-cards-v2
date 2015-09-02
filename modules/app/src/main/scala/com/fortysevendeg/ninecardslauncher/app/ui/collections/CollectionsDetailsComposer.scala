package com.fortysevendeg.ninecardslauncher.app.ui.collections

import java.util

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.SharedElementCallback
import android.os.{Bundle, Build}
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.app.AppCompatActivity
import android.transition.{Transition, Fade, TransitionSet, TransitionInflater}
import android.view.{ViewGroup, Gravity, View}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.Snails._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.apps.AppsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.FabButtonBehaviour
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.components.{FabItemMenu, IconTypes, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.utils.SystemBarTintManager
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.PositionsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._

import scala.collection.JavaConversions._

import CollectionsDetailsActivity._

import scala.concurrent.ExecutionContext.Implicits.global

trait CollectionsDetailsComposer
  extends Styles
  with FabButtonBehaviour {

  self: AppCompatActivity with TypedFindView with Contexts[AppCompatActivity] =>

  val nameActionFragment = "action-fragment"

  val resistanceDisplacement = .35f

  val resistanceScale = .15f

  lazy val iconIndicatorDrawable = new PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.default_stroke),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val elevation = resGetDimensionPixelSize(R.dimen.elevation_toolbar)

  lazy val systemBarTintManager = new SystemBarTintManager(this)

  lazy val toolbar = Option(findView(TR.collections_toolbar))

  lazy val root = Option(findView(TR.collections_root))

  lazy val fragmentContent = Option(findView(TR.collections_fragment_content))

  lazy val viewPager = Option(findView(TR.collections_view_pager))

  lazy val tabs = Option(findView(TR.collections_tabs))

  lazy val iconContent = Option(findView(TR.collections_icon_content))

  lazy val icon = Option(findView(TR.collections_icon))

  def initUi(indexColor: Int, iconCollection: String)(implicit theme: NineCardsTheme) =
    (tabs <~ tabsStyle <~ vInvisible) ~
      initFabButton ~
      loadMenuItems(getItemsForFabMenu) ~
      (viewPager <~ vInvisible) ~
      updateToolbarColor(resGetColor(getIndexColor(indexColor))) ~
      (icon <~ ivSrc(iconCollectionDetail(iconCollection)))

  def drawCollections(collections: Seq[Collection], position: Int)
    (implicit manager: FragmentManagerContext[Fragment, FragmentManager], theme: NineCardsTheme) = {
    val adapter = CollectionsPagerAdapter(manager.get, collections)
    (root <~ rootStyle) ~
      (viewPager <~ vpAdapter(adapter)) ~
      Ui(adapter.activateFragment(position)) ~
      (tabs <~
        stlViewPager(viewPager) <~
        stlOnPageChangeListener(
          new OnPageChangeCollectionsListener(collections, updateToolbarColor, updateCollection))) ~
      uiHandler(viewPager <~ Tweak[ViewPager](_.setCurrentItem(position, false))) ~
      (tabs <~ vVisible <~~ enterViews) ~
      (viewPager <~ vVisible <~~ enterViews)
  }

  def pullCloseScrollY(scroll: Int, scrollType: Int, close: Boolean): Ui[_] = {
    val displacement = scroll * resistanceDisplacement
    val distanceToValidClose = resGetDimension(R.dimen.distance_to_valid_close)
    val scale = 1f + ((scroll / distanceToValidClose) * resistanceScale)
    (tabs <~ (scrollType match {
      case ScrollType.down => vTranslationY(displacement)
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
      (toolbar <~ vTranslationY(-move * 2) <~ uiElevation(newElevation)) ~
      (iconContent <~ uiElevation(newElevation) <~ vScaleX(scale) <~ vScaleY(scale) <~ vAlpha(1 - ratio))
  }

  def notifyScroll(sType: Int): Ui[_] = (for {
    vp <- viewPager
    adapter <- getAdapter
  } yield {
      adapter.setScrollType(sType)
      adapter.notifyChanged(vp.getCurrentItem)
    }) getOrElse Ui.nop

  private[this] def getItemsForFabMenu(implicit theme: NineCardsTheme) = Seq(
    getUi(w[FabItemMenu] <~ fabButtonApplicationsStyle <~ FuncOn.click {
      (view: View) =>
        showAction(view)
    }),
    getUi(w[FabItemMenu] <~ fabButtonRecommendationsStyle <~ On.click {
      uiShortToast("Recommendations")
    }),
    getUi(w[FabItemMenu] <~ fabButtonContactsStyle <~ On.click {
      uiShortToast("Contacts")
    }),
    getUi(w[FabItemMenu] <~ fabButtonShortcutsStyle <~ On.click {
      uiShortToast("Shortcuts")
    })
  )

  private[this] def uiElevation(elevation: Float) = Lollipop.ifSupportedThen {
    vElevation(elevation)
  }.getOrElse(Tweak.blank)

  def getAdapter: Option[CollectionsPagerAdapter] = viewPager flatMap (ad => Option(ad.getAdapter)) flatMap {
    case adapter: CollectionsPagerAdapter => Some(adapter)
    case _ => None
  }

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

    getWindow.setSharedElementReturnTransition(new TransitionSet())
    getWindow.setReturnTransition(new Fade())

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
      Ui {
        Lollipop ifSupportedThen {
          getWindow.setStatusBarColor(color)
        } getOrElse {
          systemBarTintManager.setStatusBarTintColor(color)
        }
      }

  private[this] def showAction(view: View): Ui[_] = {
    val sizeIconFabMenuItem = resGetDimensionPixelSize(R.dimen.size_fab_menu_item)
    val (x: Int, y: Int) = calculateAnchorViewPosition(view.findViewById(R.id.fab_icon))
    val args = new Bundle()
    args.putInt(BaseActionFragment.posX, x + (sizeIconFabMenuItem / 2))
    args.putInt(BaseActionFragment.posY, y + (sizeIconFabMenuItem / 2))
    swapFabButton ~
      (fragmentContent <~ fadeBackground(in = true) <~ fragmentContentStyle(true)) ~
      addFragment(f[AppsFragment].pass(args), Option(R.id.collections_fragment_content), Option(nameActionFragment))
  }

  def turnOffFragmentContent: Ui[_] = fragmentContent <~ fadeBackground(in = false) <~ fragmentContentStyle(false)

  def removeActionFragment(): Unit = findFragmentByTag(nameActionFragment) map removeFragment

  def isActionShowed: Boolean = findFragmentByTag(nameActionFragment).isDefined

  def unrevealActionFragment(): Ui[_] =
    findFragmentByTag[BaseActionFragment](nameActionFragment) map (_.unreveal()) getOrElse Ui.nop

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
  updateToolbarColor: (Int) => Ui[_],
  updateCollection: (Collection, Int, PageMovement) => Ui[_])
  (implicit context: ContextWrapper, theme: NineCardsTheme)
  extends OnPageChangeListener {

  var lastPosition = -1

  var currentPosition = -1

  var currentMovement: PageMovement = Loading

  private[this] def getColor(col: Collection): Int = resGetColor(getIndexColor(col.themedColorIndex))

  private[this] def jump(from: Collection, to: Collection) = {
    val valueAnimator = ValueAnimator.ofInt(0, 100)
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      override def onAnimationUpdate(value: ValueAnimator) {
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
        nextCollection map {
          next =>
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
