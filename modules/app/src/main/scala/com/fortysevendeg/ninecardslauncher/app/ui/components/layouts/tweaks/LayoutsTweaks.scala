package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.ContentView
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.models.TermCounter
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak, Ui}

import scala.annotation.tailrec

object LauncherWorkSpacesTweaks {
  type W = LauncherWorkSpaces

  val defaultPage = 1

  // We create a new page every 9 collections
  @tailrec
  private def getCollectionsItems(collections: Seq[Collection], acc: Seq[LauncherData], newLauncherData: LauncherData): Seq[LauncherData] = {
    collections match {
      case Nil if newLauncherData.collections.nonEmpty => acc :+ newLauncherData
      case Nil => acc
      case h :: t if newLauncherData.collections.length == numSpaces => getCollectionsItems(t, acc :+ newLauncherData, LauncherData(CollectionsWorkSpace, Seq(h)))
      case h :: t =>
        val g: Seq[Collection] = newLauncherData.collections :+ h
        val n = LauncherData(CollectionsWorkSpace, g)
        getCollectionsItems(t, acc, n)
    }
  }

  def lwsData(collections: Seq[Collection], pageSelected: Int) = Tweak[W] {
    workspaces =>
      workspaces.data = LauncherData(MomentWorkSpace) +: getCollectionsItems(collections, Seq.empty, LauncherData(CollectionsWorkSpace))
      workspaces.init(pageSelected)
  }

  def lwsClean = Tweak[W] (_.clean())

  def lwsAddCollection(collection: Collection) = Tweak[W] {
    workspaces =>
      workspaces.data.lastOption foreach { data =>
        val lastWorkspaceHasSpace = data.collections.size < numSpaces
        if (lastWorkspaceHasSpace) {
          workspaces.data = workspaces.data map { d =>
            if (d == data) {
              d.copy(collections = d.collections :+ collection)
            } else {
              d
            }
          }
        } else {
          workspaces.data = workspaces.data :+ LauncherData(CollectionsWorkSpace, Seq(collection))
        }
        workspaces.selectPosition(workspaces.data.size - 1)
        workspaces.reset()
      }
  }

  def lwsRemoveCollection(collection: Collection) = Tweak[W] {
    workspaces =>
      // We remove a collection in sequence and fix positions
      val collections = (workspaces.data flatMap (_.collections.filterNot(_ == collection))).zipWithIndex map {
        case (col, index) => col.copy(position = index)
      }
      val maybeWorkspaceCollection = workspaces.data find (_.collections contains collection)
      val maybePage = maybeWorkspaceCollection map workspaces.data.indexOf
      workspaces.data = LauncherData(MomentWorkSpace) +: getCollectionsItems(collections, Seq.empty, LauncherData(CollectionsWorkSpace))
      val page = maybePage map { page =>
        if (workspaces.data.isDefinedAt(page)) page else workspaces.data.length - 1
      } getOrElse defaultPage
      workspaces.selectPosition(page)
      workspaces.reset()


  }
  def lwsListener(listener: LauncherWorkSpacesListener) = Tweak[W] (_.workSpacesListener = listener)

  def lwsSelect(position: Int) = Tweak[W](_.selectPosition(position))

  def lwsCloseMenu = Tweak[W] { view =>
    runUi(view.closeMenu())
  }

}

object AnimatedWorkSpacesTweaks {

  type W = AnimatedWorkSpaces[_, _]

  def awsListener(listener: AnimatedWorkSpacesListener) = Tweak[W] (_.listener = listener)

  def awsAddPageChangedObserver(observer: (Int => Unit)) = Tweak[W](_.addPageChangedObservers(observer))

}

object FabItemMenuTweaks {
  type W = FabItemMenu

  def fimBackgroundColor(color: Int) = Tweak[W](_.icon foreach {
    ic =>
      Lollipop ifSupportedThen {
        ic.setBackgroundColor(color)
      } getOrElse {
        val d = new ShapeDrawable(new OvalShape)
        d.getPaint.setColor(color)
        ic.setBackground(d)
      }
  })

  def fimSrc(res: Int) = Tweak[W](_.icon foreach (_.setImageResource(res)))

  def fimTitle(text: Int) = Tweak[W](_.title foreach (_.setText(text)))

  def fimTitle(text: String) = Tweak[W](_.title foreach (_.setText(text)))

}

object WorkSpaceItemMenuTweaks {
  type W = WorkSpaceItemMenu

  def wimBackgroundColor(color: Int) = Tweak[W](_.icon foreach {
    ic =>
      Lollipop ifSupportedThen {
        ic.setBackgroundColor(color)
      } getOrElse {
        val d = new ShapeDrawable(new OvalShape)
        d.getPaint.setColor(color)
        ic.setBackground(d)
      }
  })

  def wimSrc(resourceId: Int) = Tweak[W](_.icon foreach (_.setImageResource(resourceId)))

  def wimTitle(text: Int) = Tweak[W](_.title foreach (_.setText(text)))

  def wimTitle(text: String) = Tweak[W](_.title foreach (_.setText(text)))

}

object SearchBoxesAnimatedViewTweak {

  def sbvUpdateContentView(contentView: ContentView) = Tweak[SearchBoxView] (view => runUi(view.updateContentView(contentView)))

  def sbvChangeListener(listener: SearchBoxAnimatedListener) = Tweak[SearchBoxView] (_.listener = Some(listener))

  def sbvUpdateHeaderIcon(resourceId: Int) = Tweak[SearchBoxView](view => runUi(view.updateHeaderIcon(resourceId)))

  def sbvOnChangeText(onChangeText: (String) => Unit) = Tweak[SearchBoxView] (_.addTextChangedListener(onChangeText))

  def sbvClean = Tweak[SearchBoxView] (view => runUi(view.clean))
}

object PullToTabsViewTweaks {

  def ptvAddTabsAndActivate(items: Seq[TabInfo], index: Int) = Tweak[PullToTabsView](_.addTabs(items, Some(index)))

  def ptvAddTabs(items: Seq[TabInfo]) = Tweak[PullToTabsView](_.addTabs(items))

  def ptvLinkTabs(tabs: Option[LinearLayout], start: Ui[_], end: Ui[_]) = Tweak[PullToTabsView] { view =>
    runUi(view.linkTabsView(tabs, start, end))
  }

  def ptvClearTabs() = Tweak[PullToTabsView](_.clear())

  def ptvActivate(item: Int) = Tweak[PullToTabsView](_.activateItem(item))

  def ptvListener(pullToTabsListener: PullToTabsListener) =
    Tweak[PullToTabsView] (_.tabsListener = pullToTabsListener)

}

object PullToCloseViewTweaks {

  def pcvListener(pullToCloseListener: PullToCloseListener) =
    Tweak[PullToCloseView] (_.closeListeners = pullToCloseListener)

}


object PullToDownViewTweaks {

  def pdvPullingListener(pullToDownListener: PullingListener) =
    Tweak[PullToDownView] (_.pullingListeners = pullToDownListener)

  def pdvHorizontalListener(horizontalMovementListener: HorizontalMovementListener) =
    Tweak[PullToDownView] (_.horizontalListener = horizontalMovementListener)

  def pdvEnable(enabled: Boolean) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(enabled = enabled)
    }

  def pdvHorizontalEnable(enabled: Boolean) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(scrollHorizontalEnabled = enabled)
    }

  def pdvResistance(resistance: Float) =
    Tweak[PullToDownView] { view =>
      view.pullToDownStatuses = view.pullToDownStatuses.copy(resistance = resistance)
    }

}

object FastScrollerLayoutTweak {
  // We should launch this tweak when the adapter has been added
  def fslLinkRecycler(recyclerView: RecyclerView) = Tweak[FastScrollerLayout](_.linkRecycler(recyclerView))

  def fslColor(color: Int) = Tweak[FastScrollerLayout](_.setColor(color))

  def fslEnabledScroller(enabled: Boolean) = Tweak[FastScrollerLayout](_.setEnabledScroller(enabled))

  def fslReset = Tweak[FastScrollerLayout](_.reset)

  def fslCounters(counters: Seq[TermCounter]) = Tweak[FastScrollerLayout](_.setCounters(counters))

  def fslSignalType(signalType: FastScrollerSignalType) = Tweak[FastScrollerLayout](_.setSignalType(signalType))

}

object SlidingTabLayoutTweaks {
  type W = SlidingTabLayout

  def stlViewPager(viewPager: Option[ViewPager]): Tweak[W] = Tweak[W](viewPager foreach _.setViewPager)

  def stlDefaultTextColor(color: Int): Tweak[W] = Tweak[W](_.setDefaultTextColor(color))

  def stlSelectedTextColor(color: Int): Tweak[W] = Tweak[W](_.setSelectedTextColor(color))

  def stlTabStripColor(color: Int): Tweak[W] = Tweak[W](_.setTabStripColor(color))

  def stlOnPageChangeListener(listener: ViewPager.OnPageChangeListener): Tweak[W] = Tweak[W](_.setOnPageChangeListener(listener))
}

object DialogToolbarTweaks {

  type W = DialogToolbar

  def dtbInit(color: Int)(implicit contextWrapper: ContextWrapper) = Tweak[W] { view =>
    runUi(view.init(color))
  }

  def dtbExtended(implicit contextWrapper: ContextWrapper) = Tweak[W] { view =>
    runUi(view.changeToolbarHeight(resGetDimensionPixelSize(R.dimen.height_extended_toolbar_dialog)))
  }

  def dtbAddExtendedView(viewToAdd: View)(implicit contextWrapper: ContextWrapper) = Tweak[W] { view =>
    runUi(view.addExtendedView(viewToAdd))
  }

  def dtbChangeText(resourceId: Int) = Tweak[W] { view =>
    runUi(view.changeText(resourceId))
  }

  def dtbNavigationOnClickListener(click: (View) => Ui[_]) = Tweak[W]{ view =>
    runUi(view.navigationClickListener(click))
  }

}

object SwipeAnimatedDrawerViewTweaks {

  type W = SwipeAnimatedDrawerView

  def sadvInitAnimation(contentView: ContentView, widthContainer: Int) = Tweak[W] { view =>
    runUi(view.initAnimation(contentView, widthContainer))
  }

  def sadvMoveAnimation(contentView: ContentView, widthContainer: Int, displacement: Float) = Tweak[W] { view =>
    runUi(view.moveAnimation(contentView, widthContainer, displacement))
  }

  def sadvEndAnimation(duration: Int)(implicit contextWrapper: ContextWrapper) = Tweak[W] { view =>
    runUi(view.endAnimation(duration))
  }

}