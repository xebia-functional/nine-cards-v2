package com.fortysevendeg.ninecardslauncher.ui.components

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.{ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid.FullDsl._
import macroid.{Tweak, ActivityContext, AppContext}
import ItemType._

class TestMultipleTypesAnimatedWorkSpaces(context: Context)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends AnimatedWorkSpaces[WorkSpaceMultipleHolder, TestMultipleData](context, null, 0) {

  override val horizontalGallery = false

  override def getItemViewTypeCount: Int = 2

  override def getItemViewType(data: TestMultipleData, position: Int): Int = if (data.text.isDefined) textView else imageView

  override def getData: List[TestMultipleData] = List(
    TestMultipleData(None, Some("light_action_bar_icon_collection_game_action")),
    TestMultipleData(Some("1"), None),
    TestMultipleData(Some("2"), None),
    TestMultipleData(Some("4"), None),
    TestMultipleData(None, Some("light_action_bar_icon_collection_game_adventure")),
    TestMultipleData(Some("5"), None),
    TestMultipleData(None, Some("light_action_bar_icon_collection_game_arcade"))
  )

  override def createView(viewType: Int): WorkSpaceMultipleHolder = viewType match {
    case `textView` => new WorkSpaceTextHolder
    case `imageView` => new WorkSpaceImageHolder
  }

  override def populateView(view: Option[WorkSpaceMultipleHolder], data: TestMultipleData, position: Int) = {
    view map {
      case v if v.isInstanceOf[WorkSpaceTextHolder] =>
        data.text map (text => runUi(v.asInstanceOf[WorkSpaceTextHolder].text <~ tvText(text)))
      case v if v.isInstanceOf[WorkSpaceImageHolder] =>
        for {
          image <- data.image
          drawable <- resGetDrawable(image)
        } yield runUi(v.asInstanceOf[WorkSpaceImageHolder].image <~ ivSrc(drawable))
    }
  }

}

object ItemType {
  val textView = 0
  val imageView = 1
}

class WorkSpaceMultipleHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LinearLayout(activityContext.get)

class WorkSpaceTextHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends WorkSpaceMultipleHolder {

  var text = slot[TextView]

  addView(
    getUi(
      w[TextView] <~ wire(text) <~ vMatchParent <~ tvSize(80) <~ tvColor(Color.WHITE) <~ tvText("-") <~ tvGravity(Gravity.CENTER)
    )
  )

}

class WorkSpaceImageHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends WorkSpaceMultipleHolder {

  var image = slot[ImageView]

  addView(
    getUi(
      w[ImageView] <~ wire(image) <~ vMatchParent <~ llLayoutGravity(Gravity.CENTER)
    )
  )

}

case class TestMultipleData(text: Option[String], image: Option[String])

object TestMultipleTypesAnimatedWorkSpacesTweaks {
  type W = TestAnimatedWorkSpaces

  def flgEnabled(e: Boolean): Tweak[W] = Tweak[W](_.enabled = e)

  def flgInfinite(i: Boolean): Tweak[W] = Tweak[W](_.infinite = i)

}