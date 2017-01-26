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

package cards.nine.process.social.impl

import cards.nine.commons.test.data.UserTestData
import cards.nine.commons.test.data.UserValues._
import cards.nine.services.plus.models.GooglePlusProfile

trait SocialProfileProcessImplData extends UserTestData {

  val account  = "example@domain.com"
  val clientId = "fake-client-id"

  val googlePlusProfile =
    GooglePlusProfile(name = Some(userName), avatarUrl = Some(avatar), coverUrl = Some(cover))

}
