package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.app.ActionBarActivity
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewPagerTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.collections.Snails._
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

  self: ActionBarActivity with TypedFindView with Contexts[ActionBarActivity] =>

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
    (root <~ rootStyle) ~ (tabs <~ tabsStyle)

  def fetchCollections(collections: Seq[Collection])
    (implicit manager: FragmentManagerContext[Fragment, FragmentManager], theme: NineCardsTheme) = {
    val adapter = CollectionsPagerAdapter(manager.get, collections)
    (viewPager <~ vpAdapter(adapter)) ~
      Ui(adapter.activateFragment(0)) ~
      (tabs <~
        stlViewPager(viewPager) <~
        stlOnPageChangeListener(new OnPageChangeCollectionsListener(collections, updateToolbarColor, updateCollection))) ~
      (viewPager map (vp => setIconCollection(collections(vp.getCurrentItem))) getOrElse Ui.nop)
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

  private[this] def updateCollection(collection: Collection, position: Int, fromLeft: Boolean): Ui[_] = getAdapter map {
    adapter =>
      (icon <~ changeIcon(iconCollectionDetail(collection.icon), fromLeft)) ~ adapter.notifyChanged(position)
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

