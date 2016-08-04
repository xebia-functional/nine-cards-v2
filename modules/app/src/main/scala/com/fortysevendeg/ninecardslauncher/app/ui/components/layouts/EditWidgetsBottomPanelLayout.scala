package com.fortysevendeg.ninecardslauncher.app.ui.components.layouts

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.{LayoutInflater, View}
import android.widget.LinearLayout
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.WorkSpaceButtonTweaks._
import macroid.Contexts

class EditWidgetsBottomPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with Contexts[View]
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  LayoutInflater.from(context).inflate(R.layout.edit_widgets_bottom_paneal_layout, this)

  lazy val resizeAction = findView(TR.edit_widget_bottom_action_resize)

  lazy val moveAction = findView(TR.edit_widget_bottom_action_move)

  lazy val deleteAction = findView(TR.edit_widget_bottom_action_delete)

  ((resizeAction <~
    wbInit(WorkSpaceActionWidgetButton) <~
    wbPopulateIcon(R.drawable.icon_edit_widgets_resize, R.string.resize, R.color.edit_widget_resize)) ~
    (moveAction <~
      wbInit(WorkSpaceActionWidgetButton) <~
      wbPopulateIcon(R.drawable.icon_edit_widgets_move, R.string.move, R.color.edit_widget_move)) ~
    (deleteAction <~
      wbInit(WorkSpaceActionWidgetButton) <~
      wbPopulateIcon(R.drawable.icon_edit_widgets_delete, R.string.delete, R.color.edit_widget_delete))).run

}
