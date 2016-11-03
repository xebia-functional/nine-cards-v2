package cards.nine.app.ui.commons

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.TextView
import macroid.Excerpt

object CommonsExcerpt {

  def height = Excerpt[View, Int] (_.getHeight)

  def width = Excerpt[View, Int] (_.getWidth)

  def isVisible = Excerpt[View, Boolean] (_.getVisibility == View.VISIBLE)

  def isGone = Excerpt[View, Boolean] (_.getVisibility == View.GONE)

  def isInvisible = Excerpt[View, Boolean] (_.getVisibility == View.INVISIBLE)

  def text = Excerpt[TextView, Option[String]] (tv => Option(tv.getText) map (_.toString))

  def dlIsLockedClosedDrawerStart: Excerpt[DrawerLayout, Boolean] = Excerpt[DrawerLayout, Boolean](
    _.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

  def dlIsLockedClosedDrawerEnd: Excerpt[DrawerLayout, Boolean] = Excerpt[DrawerLayout, Boolean](
  _.getDrawerLockMode(GravityCompat.END) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

  def dlIsLockedOpenedDrawerStart: Excerpt[DrawerLayout, Boolean] = Excerpt[DrawerLayout, Boolean](
    _.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)

  def dlIsLockedOpenedDrawerEnd: Excerpt[DrawerLayout, Boolean] = Excerpt[DrawerLayout, Boolean](
    _.getDrawerLockMode(GravityCompat.END) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)
}
