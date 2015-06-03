package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.content.Context
import android.widget.FrameLayout
import com.fortysevendeg.ninecardslauncher.modules.RegistryImpl
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import com.fortysevendeg.ninecardslauncher.ui.commons.Constants
import com.fortysevendeg.ninecardslauncher.ui.components.AnimatedWorkSpaces
import com.fortysevendeg.ninecardslauncher.ui.launcher.WorkSpaceType._
import macroid.{ActivityContextWrapper, ContextWrapper, Tweak, Ui}

import scala.annotation.tailrec

class LauncherWorkSpaces(context: Context)(implicit activityContext: ActivityContextWrapper)
  extends AnimatedWorkSpaces[LauncherWorkSpaceHolder, LauncherData](context, null, 0)
  with RegistryImpl {

  override val contextProvider: ContextWrapper = activityContext

  override def getItemViewTypeCount: Int = 2

  override def getItemViewType(data: LauncherData, position: Int): Int = if (data.widgets) widgets else collections

  override def createView(viewType: Int): LauncherWorkSpaceHolder = viewType match {
    case `widgets` => new LauncherWorkSpaceWidgetsHolder
    case `collections` => new LauncherWorkSpaceCollectionsHolder(dimen)
  }

  override def populateView(view: Option[LauncherWorkSpaceHolder],  data: LauncherData, viewType: Int, position: Int): Ui[_] =
    view match {
      case Some(v: LauncherWorkSpaceCollectionsHolder) => v.populate(data)
      case _ => Ui.nop
    }
}

object WorkSpaceType {
  val widgets = 0
  val collections = 1
}

class LauncherWorkSpaceHolder(implicit activityContext: ActivityContextWrapper)
  extends FrameLayout(activityContext.application)

case class LauncherData(widgets: Boolean, collections: Seq[Collection] = Seq.empty)

object LauncherWorkSpacesTweaks {
  type W = LauncherWorkSpaces

  // We create a new page every 9 collections
  @tailrec
  private def getCollectionsItems(collections: Seq[Collection], acc: Seq[LauncherData], newLauncherData: LauncherData): Seq[LauncherData] = {
    collections match {
      case Nil if newLauncherData.collections.length > 0 => acc :+ newLauncherData
      case Nil => acc
      case h :: t if newLauncherData.collections.length == Constants.NumSpaces => getCollectionsItems(t, acc :+ newLauncherData, LauncherData(false))
      case h :: t =>
        val g: Seq[Collection] = newLauncherData.collections :+ h
        val n = LauncherData(false, g)
        getCollectionsItems(t, acc, n)
    }
  }

  def lwsData(collections: Seq[Collection], pageSelected: Int) = Tweak[W] {
    workspaces =>
      workspaces.data = LauncherData(true) +: getCollectionsItems(collections, Seq.empty, LauncherData(false))
      workspaces.init(pageSelected)
  }

  def lwsAddPageChangedObserver(observer: (Int => Unit)) = Tweak[W] (_.addPageChangedObservers(observer))

}