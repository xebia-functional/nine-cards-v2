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

sealed trait Value {
  def value: Long
}

case object NoValue extends Value {
  override def value: Long = 0
}

case object VeryLowValue extends Value {
  override def value: Long = 1
}

case object LowValue extends Value {
  override def value: Long = 2
}

case object MediumValue extends Value {
  override def value: Long = 3
}

case object HighValue extends Value {
  override def value: Long = 4
}

case object VeryHighValue extends Value {
  override def value: Long = 5
}

case class ProvideValue(v: Long) extends Value {
  override def value: Long = v
}

// Values related to manage apps

case object OpenAppFromCollectionValue extends Value {
  override def value: Long = 3
}

case object OpenAppFromAppDrawerValue extends Value {
  override def value: Long = 1
}

case object OpenAppFromDockValue extends Value {
  override def value: Long = 1
}

case object AddedToCollectionValue extends Value {
  override def value: Long = 10
}

case object RemovedFromCollectionValue extends Value {
  override def value: Long = -3
}

case object OpenMomentFromWorkspaceValue extends Value {
  override def value: Long = 3
}

// Values related to widget

case object AddedWidgetToMomentValue extends Value {
  override def value: Long = 10
}
