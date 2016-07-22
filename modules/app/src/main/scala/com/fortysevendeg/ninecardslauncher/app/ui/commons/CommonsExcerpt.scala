package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.view.View
import android.widget.RadioButton
import macroid.Excerpt

object CommonsExcerpt {

  def height = Excerpt[View, Int] (_.getHeight)

  def width = Excerpt[View, Int] (_.getWidth)

  def rbChecked = Excerpt[RadioButton, Boolean] (_.isChecked)

}
