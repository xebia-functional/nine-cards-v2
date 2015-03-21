package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.{ImageView, TextView, FrameLayout}
import com.fortysevendeg.ninecardslauncher.ui.components.AnimatedWorkSpaces
import macroid.{ActivityContext, AppContext}
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import WorkSpaceType._
import macroid.FullDsl._

class LauncherWorkSpaces(context: Context)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends AnimatedWorkSpaces[LauncherWorkSpaceHolder, LauncherData](context, null, 0) {

  override def getItemViewTypeCount: Int = 2

  override def getItemViewType(data: LauncherData, position: Int): Int = if (data.widgets) widgets else collections

  override def getData: List[LauncherData] = List(
    LauncherData(true, ""),
    LauncherData(false, "")
  )

  override def createView(viewType: Int): LauncherWorkSpaceHolder = viewType match {
    case `widgets` => new LauncherWorkSpaceWidgetsHolder
    case `collections` => new LauncherWorkSpaceCollectionsHolder
  }

  override def populateView(view: Option[LauncherWorkSpaceHolder], data: LauncherData, position: Int) = {
    view foreach {
      case v if v.isInstanceOf[LauncherWorkSpaceWidgetsHolder] =>
      case v if v.isInstanceOf[LauncherWorkSpaceCollectionsHolder] =>
    }
  }

}

object WorkSpaceType {
  val widgets = 0
  val collections = 1
}

class LauncherWorkSpaceHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends FrameLayout(activityContext.get)

class LauncherWorkSpaceWidgetsHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LauncherWorkSpaceHolder {

  var text = slot[TextView]

  addView(
    getUi(
      w[TextView] <~ wire(text) <~ vMatchParent <~ tvSize(80) <~ tvColor(Color.WHITE) <~ tvText("WIDGETS") <~ tvGravity(Gravity.CENTER)
    )
  )

}

class LauncherWorkSpaceCollectionsHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LauncherWorkSpaceHolder {

  var image = slot[ImageView]

  addView(
    getUi(
      w[ImageView] <~ wire(image) <~ vMatchParent
    )
  )

}

case class LauncherData(widgets: Boolean, collections: String)