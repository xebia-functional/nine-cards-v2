package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.Context
import android.graphics.Color
import android.view.{Gravity, View}
import android.widget.TextView
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.LauncherWorkSpaceHolder
import macroid.Contexts
import macroid.FullDsl._

class LauncherWorkSpaceMomentsHolder(context: Context)
  extends LauncherWorkSpaceHolder(context)
  with Contexts[View] {

  var text = slot[TextView]

  addView((w[TextView] <~ wire(text) <~ vMatchParent <~ tvSize(30) <~ tvColor(Color.WHITE) <~ tvText("MOMENTS") <~ tvGravity(Gravity.CENTER)).get)

}