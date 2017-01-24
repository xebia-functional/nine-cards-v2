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

package cards.nine.models

import cards.nine.models.types.{NineCardsCategory, NineCardsMoment}

case class CategorizedDetailPackage(
    packageName: String,
    title: String,
    category: Option[NineCardsCategory],
    icon: String,
    free: Boolean,
    downloads: String,
    stars: Double)

case class CategorizedPackage(packageName: String, category: Option[NineCardsCategory])

case class LoginResponse(apiKey: String, sessionToken: String)

case class RankApps(category: NineCardsCategory, packages: Seq[String])

case class RankAppsByMoment(moment: NineCardsMoment, packages: Seq[String])

case class RankWidget(packageName: String, className: String)

case class RankWidgetsByMoment(moment: NineCardsMoment, widgets: Seq[RankWidget])

case class NotCategorizedPackage(
    packageName: String,
    title: String,
    icon: Option[String],
    downloads: String,
    stars: Double,
    free: Boolean,
    screenshots: Seq[String])

case class RequestConfig(
    apiKey: String,
    sessionToken: String,
    androidId: String,
    marketToken: Option[String] = None)

case class RequestConfigV1(deviceId: String, token: String, marketToken: Option[String])
