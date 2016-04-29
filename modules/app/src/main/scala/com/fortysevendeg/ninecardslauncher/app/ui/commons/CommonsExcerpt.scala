package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.view.View
import macroid.Excerpt

object CommonsExcerpt {

  def height = Excerpt[View, Int] (_.getHeight)

  def width = Excerpt[View, Int] (_.getWidth)

}
