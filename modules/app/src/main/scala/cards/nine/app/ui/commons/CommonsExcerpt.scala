/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.commons

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.TextView
import macroid.Excerpt

object CommonsExcerpt {

  def height = Excerpt[View, Int](_.getHeight)

  def width = Excerpt[View, Int](_.getWidth)

  def isVisible = Excerpt[View, Boolean](_.getVisibility == View.VISIBLE)

  def isGone = Excerpt[View, Boolean](_.getVisibility == View.GONE)

  def isInvisible = Excerpt[View, Boolean](_.getVisibility == View.INVISIBLE)

  def isEnabled = Excerpt[View, Boolean](_.isEnabled)

  def text =
    Excerpt[TextView, Option[String]](tv => Option(tv.getText) map (_.toString))

  def dlIsLockedClosedDrawerStart: Excerpt[DrawerLayout, Boolean] =
    Excerpt[DrawerLayout, Boolean](
      _.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

  def dlIsLockedClosedDrawerEnd: Excerpt[DrawerLayout, Boolean] =
    Excerpt[DrawerLayout, Boolean](
      _.getDrawerLockMode(GravityCompat.END) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

  def dlIsLockedOpenedDrawerStart: Excerpt[DrawerLayout, Boolean] =
    Excerpt[DrawerLayout, Boolean](
      _.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)

  def dlIsLockedOpenedDrawerEnd: Excerpt[DrawerLayout, Boolean] =
    Excerpt[DrawerLayout, Boolean](
      _.getDrawerLockMode(GravityCompat.END) == DrawerLayout.LOCK_MODE_LOCKED_OPEN)
}
