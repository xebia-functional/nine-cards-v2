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

package cards.nine.app.ui.preferences.about

import android.app.Fragment
import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.{Preference, PreferenceCategory, PreferenceFragment}
import cards.nine.app.ui.preferences.commons.FindPreferences
import cards.nine.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

class AboutFragment extends PreferenceFragment with Contexts[Fragment] with FindPreferences {

  val dom = AboutDOM(this)

  val technologies = Seq(
    Library(
      "Scala",
      R.drawable.tech_scala,
      "https://www.scala-lang.org/",
      R.string.server_and_client),
    Library(
      "Cats",
      R.drawable.tech_cats,
      "http://typelevel.org/cats/",
      R.string.server_and_client),
    Library("Monix", R.drawable.tech_monix, "https://monix.io/", R.string.server_and_client),
    Library(
      "Macroid",
      R.drawable.tech_macroid,
      "http://47deg.github.io/macroid/",
      R.string.only_client),
    Library("Spray", R.drawable.tech_spray, "http://spray.io/", R.string.only_server),
    Library("Akka", R.drawable.tech_akka, "http://akka.io/", R.string.only_server),
    Library(
      "Circe",
      R.drawable.tech_circe,
      "https://circe.github.io/circe/",
      R.string.only_server),
    Library(
      "Doobie",
      R.drawable.tech_doobie,
      "https://github.com/tpolecat/doobie",
      R.string.only_server),
    Library(
      "Shapeless",
      R.drawable.tech_shapeless,
      "https://github.com/milessabin/shapeless",
      R.string.only_server))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach (_.setTitle(getString(R.string.aboutTitle)))
    addPreferencesFromResource(R.xml.preferences_about)

    technologies foreach { tech =>
      val preference = new Preference(fragmentContextWrapper.bestAvailable)
      preference.setTitle(tech.name)
      preference.setSummary(tech.summary)
      preference.setIcon(tech.icon)
      preference.setOnPreferenceClickListener(new OnPreferenceClickListener {
        override def onPreferenceClick(preference: Preference): Boolean = {
          uiOpenUrlIntent(tech.url).run
          true
        }
      })
      dom.aboutPreferenceCategory.addPreference(preference)
    }
  }

}

case class AboutDOM(dom: FindPreferences) {

  def aboutPreferenceCategory = dom.findByName[PreferenceCategory]("libraries")
}

case class Library(name: String, icon: Int, url: String, summary: Int)
