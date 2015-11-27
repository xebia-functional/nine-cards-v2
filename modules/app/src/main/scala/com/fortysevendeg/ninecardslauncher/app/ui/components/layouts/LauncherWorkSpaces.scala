package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.WorkSpaceType._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.{LauncherWorkSpaceCollectionsHolder, LauncherWorkSpaceWidgetsHolder}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import macroid.{ActivityContextWrapper, Ui}

class LauncherWorkSpaces(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit activityContext: ActivityContextWrapper)
  extends AnimatedWorkSpaces[LauncherWorkSpaceHolder, LauncherData](context, attr, defStyleAttr) {

  def this(context: Context)(implicit activityContext: ActivityContextWrapper) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet)(implicit activityContext: ActivityContextWrapper) = this(context, attr, 0)

  override def getItemViewTypeCount: Int = 2

  override def getItemViewType(data: LauncherData, position: Int): Int = if (data.widgets) widgets else collections

  def isWidgetScreen = data(currentItem).widgets

  def isWidgetScreen(page: Int) = data(page).widgets

  def isCollectionScreen = !isWidgetScreen

  def isCollectionScreen(page: Int) = !isWidgetScreen(page)

  def goToWizardScreen(toRight: Boolean): Boolean = data.lift(if (toRight) {
    currentItem - 1
  } else {
    currentItem + 1
  }) exists (_.widgets)

  override def createView(viewType: Int): LauncherWorkSpaceHolder = viewType match {
    case `widgets` => new LauncherWorkSpaceWidgetsHolder
    case `collections` => new LauncherWorkSpaceCollectionsHolder(states.dimen)
  }

  override def populateView(view: Option[LauncherWorkSpaceHolder], data: LauncherData, viewType: Int, position: Int): Ui[_] =
    view match {
      case Some(v: LauncherWorkSpaceCollectionsHolder) => v.populate(data.collections)
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

