package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.AnimatedWorkSpaces
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Tweak, Ui}

class StepsWorkspaces(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit activityContext: ActivityContextWrapper)
  extends AnimatedWorkSpaces[StepWorkSpaceWidgetsHolder, StepData](context, attr, defStyleAttr) {

  def this(context: Context)(implicit activityContext: ActivityContextWrapper) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet)(implicit activityContext: ActivityContextWrapper) = this(context, attr, 0)

  override def createView(viewType: Int): StepWorkSpaceWidgetsHolder = new StepWorkSpaceWidgetsHolder

  override def populateView(view: Option[StepWorkSpaceWidgetsHolder], data: StepData, viewType: Int, position: Int): Ui[_] =
    view match {
      case Some(v: StepWorkSpaceWidgetsHolder) => v.bind(data)
      case _ => Ui.nop
    }

}

case class StepData(image: Int, message: String)

class StepWorkSpaceWidgetsHolder(implicit activityContext: ActivityContextWrapper)
  extends FrameLayout(activityContext.getOriginal)
  with TypedFindView {

  lazy val image = findView(TR.wizard_step_item_image)

  lazy val message = findView(TR.wizard_step_item_message)

  val root = LayoutInflater.from(activityContext.getOriginal).inflate(R.layout.wizard_step, javaNull, false)

  addView(root)

  def bind(data: StepData): Ui[_] =
    (image <~ ivSrc(data.image)) ~
      (message <~ tvText(data.message))

}

object StepsWorkspacesTweaks {
  type W = StepsWorkspaces

  def swData(data: Seq[StepData]) = Tweak[W] { view =>
    view.data = data
    view.init()
  }

}