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

package cards.nine.app.ui.components.preferences

import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.commons.javaNull
import cards.nine.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.{R, TR}
import macroid.extras.TextViewTweaks._
import macroid._
import macroid.FullDsl._

class AboutHeaderPreference(context: Context, attrs: AttributeSet, defStyle: Int)
    extends Preference(context, attrs, defStyle) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  implicit lazy val contextWrapper = ContextWrapper(context)

  override def onCreateView(parent: ViewGroup): View = {
    val aboutView = LayoutInflater
      .from(context)
      .inflate(R.layout.about_header_preference, javaNull)
      .asInstanceOf[ViewGroup]

    import cards.nine.app.ui.commons.ViewGroupFindViews._

    val version = findView(TR.preference_about_name).run(aboutView)
    val github  = findView(TR.preference_about_github).run(aboutView)

    val info =
      context.getPackageManager.getPackageInfo(context.getPackageName, 0)

    ((version <~ tvText(info.versionName)) ~
      (github <~ On.click(uiOpenUrlIntent(context.getString(R.string.nine_cards_github))))).run

    aboutView
  }

}
