package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.graphics.Point
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.Snails._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ColorsUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ImageResourceNamed._
import com.fortysevendeg.ninecardslauncher.app.ui.components.SlidingTabLayoutTweaks._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.utils.SystemBarTintManager
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

trait CollectionsDetailsComposer
  extends Styles {

  self: AppCompatActivity with TypedFindView with Contexts[AppCompatActivity] =>

  lazy val spaceMove = resGetDimensionPixelSize(R.dimen.space_moving_collection_details)

  lazy val elevation = resGetDimensionPixelSize(R.dimen.elevation_toolbar)

  lazy val systemBarTintManager = new SystemBarTintManager(this)

  lazy val toolbar = Option(findView(TR.collections_toolbar))

  lazy val root = Option(findView(TR.collections_root))

  lazy val viewPager = Option(findView(TR.collections_view_pager))

  lazy val tabs = Option(findView(TR.collections_tabs))

  lazy val iconContent = Option(findView(TR.collections_icon_content))

  lazy val icon = Option(findView(TR.collections_icon))

  def initUi(implicit theme: NineCardsTheme) =
    (tabs <~ tabsStyle <~ vInvisible) ~
      (viewPager <~ vInvisible)

  def drawCollections(collections: Seq[Collection], position: Int)
    (implicit manager: FragmentManagerContext[Fragment, FragmentManager], theme: NineCardsTheme) = {
    val adapter = CollectionsPagerAdapter(manager.get, collections)
    (viewPager <~ vpAdapter(adapter)) ~
      Ui(adapter.activateFragment(position)) ~
      (tabs <~
        stlViewPager(viewPager) <~
        stlOnPageChangeListener(new OnPageChangeCollectionsListener(collections, updateToolbarColor, updateCollection))) ~
      (viewPager map (vp => setIconCollection(collections(vp.getCurrentItem))) getOrElse Ui.nop) ~
      uiHandler(viewPager <~ vpCurrentItem(position))
  }

  def showViews(position: Int)(implicit theme: NineCardsTheme) = {
    val point = new Point
    getWindowManager.getDefaultDisplay.getSize(point)
    val x = point.x / 2
    val y = (resGetDimensionPixelSize(R.dimen.size_icon_collection_detail) / 2) + resGetDimensionPixelSize(R.dimen.padding_default)
    (root <~ rootStyle) ~
      (tabs <~ vVisible) ~
      (viewPager <~ vVisible) ~
      (getAdapter map {
        adapter =>
          updateToolbarColor(resGetColor(getIndexColor(adapter.collections(position).themedColorIndex)))
      } getOrElse Ui.nop) ~
      (toolbar <~ ripple(x, y))
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

  private[this] def uiElevation(elevation: Float) = Lollipop.ifSupportedThen {
    vElevation(elevation)
  }.getOrElse(Tweak.blank)

  def getAdapter: Option[CollectionsPagerAdapter] = {
    viewPager flatMap (ad => Option(ad.getAdapter)) flatMap {
      case adapter: CollectionsPagerAdapter => Some(adapter)
      case _ => None
    }
  }

  private[this] def setIconCollection(collection: Collection): Ui[_] = icon <~ ivSrc(iconCollectionDetail(collection.icon))

  private[this] def updateCollection(collection: Collection, position: Int, pageMovement: PageMovement): Ui[_] = getAdapter map {
    adapter =>
      (pageMovement match {
        case Start | Idle => icon <~ ivSrc(iconCollectionDetail(collection.icon))
        case Left => icon <~ changeIcon(iconCollectionDetail(collection.icon), fromLeft = true)
        case Right | Jump => icon <~ changeIcon(iconCollectionDetail(collection.icon), fromLeft = false)
        case _ => Ui.nop
      }) ~ adapter.notifyChanged(position)
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
  updateCollection: (Collection, Int, PageMovement) => Ui[_]
  )(implicit context: ContextWrapper, theme: NineCardsTheme)
  extends OnPageChangeListener {

  var lastPosition = -1

  var currentPosition = -1

  var currentMovement: PageMovement = Loading

  private[this] def getColor(col: Collection): Int = resGetColor(getIndexColor(col.themedColorIndex))

  override def onPageScrollStateChanged(state: Int): Unit = {
    state match {
      case ViewPager.SCROLL_STATE_IDLE => currentMovement = Idle
      case _ =>
    }
  }

  override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit = {

    currentMovement match {
      case Loading => // Nothing
      case Start => // First time, we change automatically the movement
        currentMovement = if (currentPosition > 0) Jump else Idle
      case Jump => // Nothing. The animation was triggered in onPageSelected
        Log.d("9cards", s"positionOffset: $positionOffset -- lastPosition: $lastPosition -- currentMovement: ${currentMovement.toString} -- currentPosition: $currentPosition")
      case _ =>
        Log.d("9cards", s"positionOffset: $positionOffset -- position: $position -- currentMovement: ${currentMovement.toString} -- currentPosition: $currentPosition")
        val selectedCollection: Collection = collections(position)
        val nextCollection: Option[Collection] = collections.lift(position + 1)
        nextCollection map {
          next =>
            val color = interpolateColors(positionOffset, getColor(selectedCollection), getColor(next))
            runUi(updateToolbarColor(color))
        }
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
    Log.d("9cards", s"===>>>>> onPageSelected: $position -- lastSelected: $currentPosition -- pageMovement: ${pageMovement.toString} <<<<<<======")
    lastPosition = currentPosition
    currentPosition = position
    currentMovement = pageMovement
    runUi(updateCollection(collections(position), position, pageMovement))
  }

}
