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

package cards.nine.models.types.theme

sealed trait ThemeStyleType

case object PrimaryColor extends ThemeStyleType

case object DockPressedColor extends ThemeStyleType

case object CardLayoutBackgroundColor extends ThemeStyleType

case object CardBackgroundColor extends ThemeStyleType

case object CardBackgroundPressedColor extends ThemeStyleType

case object CardTextColor extends ThemeStyleType

case object CollectionDetailTextTabDefaultColor extends ThemeStyleType

case object CollectionDetailTextTabSelectedColor extends ThemeStyleType

case object DrawerTabsBackgroundColor extends ThemeStyleType

case object DrawerBackgroundColor extends ThemeStyleType

case object DrawerTextColor extends ThemeStyleType

case object SearchBackgroundColor extends ThemeStyleType

case object SearchGoogleColor extends ThemeStyleType

case object SearchIconsColor extends ThemeStyleType

case object SearchTextColor extends ThemeStyleType

case object SearchPressedColor extends ThemeStyleType

case object DrawerIconColor extends ThemeStyleType
