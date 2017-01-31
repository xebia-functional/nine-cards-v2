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

package cards.nine.models.types

sealed trait Screen {
  def name: String
}

case object AppDrawerScreen extends Screen {
  override def name: String = "AppDrawer"
}

case object CollectionDetailScreen extends Screen {
  override def name: String = "CollectionDetail"
}

case object HomeScreen extends Screen {
  override def name: String = "Home"
}

case object LauncherScreen extends Screen {
  override def name: String = "Launcher"
}

case object MomentsScreen extends Screen {
  override def name: String = "Moments"
}

case object ProfileScreen extends Screen {
  override def name: String = "Profile"
}

case object SliderMenuScreen extends Screen {
  override def name: String = "SliderMenu"
}

case object WidgetScreen extends Screen {
  override def name: String = "Widget"
}

case object WizardScreen extends Screen {
  override def name: String = "Wizard"
}
