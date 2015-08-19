package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.view.View
import macroid.Tweak

/**
 * This tweaks should be moved to Macroid-Extras
 */
object ExtraTweaks {

  def vIntTag(tag: Int) = Tweak[View](_.setTag(tag))

  def vIntTag(id: Int, tag: Int) = Tweak[View](_.setTag(id, tag))

}
