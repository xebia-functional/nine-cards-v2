package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.view.ViewPager
import android.view.View
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
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
      val collections = workspaces.data flatMap (_.collections.filterNot(_ == collection))
      val maybeWorkspaceCollection = workspaces.data find (_.collections contains collection)
      val maybePage = maybeWorkspaceCollection map { workspace =>
        workspaces.data.indexOf(workspace)
      }
      workspaces.data = LauncherData(MomentWorkSpace) +: getCollectionsItems(collections, Seq.empty, LauncherData(CollectionsWorkSpace))
      val page = maybePage map { page =>
        if (workspaces.data.isDefinedAt(page)) page else workspaces.data.length - 1
      } getOrElse defaultPage
      workspaces.selectPosition(page)
      workspaces.reset()

  }

  def lwsSelect(position: Int) = Tweak[W](_.selectPosition(position))

}

object AnimatedWorkSpacesTweaks {

  type W = AnimatedWorkSpaces[_, _]

  def awsListener(listener: AnimatedWorkSpacesListener) = Tweak[W] { view =>
    view.listener.startScroll = listener.startScroll
    view.listener.endScroll = listener.endScroll
    view.listener.onLongClick = listener.onLongClick
  }

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

object SearchBoxesAnimatedViewTweak {

  def sbavReset(implicit contextWrapper: ContextWrapper) = Tweak[SearchBoxesAnimatedView] { view =>
    runUi(view.forceAppsView ~ view.reset)
  }

  def sbavChangeListener(listener: SearchBoxAnimatedListener) = Tweak[SearchBoxesAnimatedView] { view =>
    view.listener = Some(listener)
  }

}

object PullToCloseViewTweaks {

  def pcvListener(pullToCloseListener: PullToCloseListener) = Tweak[PullToCloseView] {
    view =>
      view.listeners.startPulling = pullToCloseListener.startPulling
      view.listeners.endPulling = pullToCloseListener.endPulling
      view.listeners.scroll = pullToCloseListener.scroll
      view.listeners.close = pullToCloseListener.close
  }

}

object FastScrollerLayoutTweak {
  // We should launch this tweak when the adapter has been added
  def fslLinkRecycler = Tweak[FastScrollerLayout](_.linkRecycler())

  def fslColor(color: Int) = Tweak[FastScrollerLayout](_.setColor(color))

  def fslInvisible = Tweak[FastScrollerLayout]{ view =>
    runUi(view.fastScroller map (fs => fs.hide) getOrElse Ui.nop)
  }

  def fslVisible = Tweak[FastScrollerLayout]{ view =>
    runUi(view.fastScroller map (fs => fs.show) getOrElse Ui.nop)
  }

  def fslReset = Tweak[FastScrollerLayout](_.reset)

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

  def dtbChangeText(res: Int) = Tweak[W] { view =>
    runUi(view.changeText(res))
  }

  def dtbNavigationOnClickListener(click: (View) => Ui[_]) = Tweak[W]{ view =>
    runUi(view.navigationClickListener(click))
  }

}