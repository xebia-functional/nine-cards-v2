package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.view.View
import android.widget.TextView
import macroid.Excerpt

object CommonsExcerpt {

  def height = Excerpt[View, Int] (_.getHeight)

  def width = Excerpt[View, Int] (_.getWidth)

  def text = Excerpt[TextView, Option[String]] (tv => Option(tv.getText) map (_.toString))

}
