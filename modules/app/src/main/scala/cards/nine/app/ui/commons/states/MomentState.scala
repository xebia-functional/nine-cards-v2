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

package cards.nine.app.ui.commons.states

import android.content.Context
import cards.nine.commons.javaNull
import cards.nine.models.types.NineCardsMoment
import macroid.ContextWrapper
import org.joda.time.DateTime

class MomentState(implicit contextWrapper: ContextWrapper) {

  private[this] val name = "persist-moment-preferences"

  private[this] val timeMomentChangedKey = "time-moment-changed"

  private[this] val momentPersistKey = "moment-persist"

  private[this] lazy val persistMomentPreferences =
    contextWrapper.bestAvailable.getSharedPreferences(name, Context.MODE_PRIVATE)

  def persist(momentType: NineCardsMoment): Unit =
    persistMomentPreferences.edit
      .putLong(timeMomentChangedKey, new DateTime().getMillis)
      .putString(momentPersistKey, momentType.name)
      .apply()

  def clean(): Unit =
    persistMomentPreferences.edit().remove(timeMomentChangedKey).remove(momentPersistKey).apply()

  def nonPersist: Boolean = {
    val defaultDate = new DateTime().minusDays(1)
    val timeChanged = new DateTime(
      persistMomentPreferences.getLong(timeMomentChangedKey, defaultDate.getMillis))
    timeChanged.plusHours(1).isBeforeNow
  }

  def getPersistMoment: Option[NineCardsMoment] =
    if (nonPersist) {
      None
    } else {
      Option(persistMomentPreferences.getString(momentPersistKey, javaNull)) map (m =>
                                                                                    NineCardsMoment(
                                                                                      m))
    }

}
