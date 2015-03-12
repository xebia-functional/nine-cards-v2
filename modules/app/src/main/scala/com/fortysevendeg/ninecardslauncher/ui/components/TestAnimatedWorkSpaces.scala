package com.fortysevendeg.ninecardslauncher.ui.components

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.{LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid.FullDsl._
import macroid.{Tweak, ActivityContext, AppContext}

class TestAnimatedWorkSpaces(context: Context)(implicit appContext: AppContext, activityContext: ActivityContext)
  extends AnimatedWorkSpaces[WorkSpaceHolder, String](context, null, 0) {

  override def getData(): List[String] = List("1", "2", "3", "4", "5", "6")

  override def createView(): WorkSpaceHolder = new WorkSpaceHolder

  override def populateView(view: Option[WorkSpaceHolder], data: String, position: Int) = {
    view map {
      v =>
        runUi(v.text <~ tvText(data))
    }
  }

}

class WorkSpaceHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LinearLayout(activityContext.get) {

  var text = slot[TextView]

  addView(
    getUi(
      w[TextView] <~ wire(text) <~ vMatchParent <~ tvSize(80) <~ tvColor(Color.WHITE) <~ tvText("-") <~ tvGravity(Gravity.CENTER)
    )
  )

}

object TestAnimatedWorkSpacesTweaks {
  type W = TestAnimatedWorkSpaces

  def flgEnabled(e: Boolean): Tweak[W] = Tweak[W](_.enabled = e)

  def flgHorizontalGallery(h: Boolean): Tweak[W] = Tweak[W](_.horizontalGallery = h)

  def flgInfinite(i: Boolean): Tweak[W] = Tweak[W](_.infinite = i)

}