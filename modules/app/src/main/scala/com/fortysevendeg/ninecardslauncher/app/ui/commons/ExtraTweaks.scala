package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.graphics.drawable.Drawable
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.View.OnClickListener
import macroid.{Ui, Tweak}
import macroid.FullDsl._

/**
 * This tweaks should be moved to Macroid-Extras
 */
object ExtraTweaks {

  def vIntTag(tag: Int) = Tweak[View](_.setTag(tag))

  def vIntTag(id: Int, tag: Int) = Tweak[View](_.setTag(id, tag))

  def tbTitle(res: Int) = Tweak[Toolbar](_.setTitle(res))

  def tbTitle(title: String) = Tweak[Toolbar](_.setTitle(title))

  def tbLogo(res: Int) = Tweak[Toolbar](_.setLogo(res))

  def tbLogo(drawable: Drawable) = Tweak[Toolbar](_.setLogo(drawable))

  def tbNavigationIcon(res: Int) = Tweak[Toolbar](_.setNavigationIcon(res))

  def tbNavigationIcon(drawable: Drawable) = Tweak[Toolbar](_.setNavigationIcon(drawable))

  def tbNavigationOnClickListener(click: (View) => Ui[_]) = Tweak[Toolbar](_.setNavigationOnClickListener(new OnClickListener {
    override def onClick(v: View): Unit = runUi(click(v))
  }))

}
