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

package cards.nine.app.ui.commons.ops

import android.view.View
import com.fortysevendeg.ninecardslauncher.R

object ViewOps {

  val positionId = R.id.position

  val viewTypeId = R.id.view_type

  val useLayerHardwareId = R.id.use_layer_hardware

  val runningAnimation = R.id.running_animation

  val fieldsMap = R.id.fields_map

  implicit class ViewExtras(view: View) {

    def isPosition(item: Int): Boolean =
      Option(view.getTag(positionId)) exists (tag => Int.unbox(tag).equals(item))

    def isRunningAnimation: Boolean =
      Option(view.getTag(runningAnimation)) exists Boolean.unbox

    def setRunningAnimation(running: Boolean): Unit =
      view.setTag(runningAnimation, running)

    def getFieldsMap: Map[String, Any] =
      Option(view.getTag(fieldsMap)) map (_.asInstanceOf[Map[String, Any]]) getOrElse Map.empty

    def getField[T](key: String): Option[T] =
      getFieldsMap find (_._1 == key) map (_._2.asInstanceOf[T])

    def getType: Option[String] =
      Option(view.getTag(viewTypeId)) map (_.toString)

    def isType(t: String): Boolean =
      Option(view.getTag(viewTypeId)).isDefined && view.getTag(viewTypeId).equals(t)

    def getPosition: Option[Int] =
      Option(view.getTag(positionId)) map (pos => Int.unbox(pos))

    def hasLayerHardware: Boolean =
      Option(view.getTag(useLayerHardwareId)).isDefined

    def calculateAnchorViewPosition: (Int, Int) = {
      val loc = new Array[Int](2)
      view.getLocationOnScreen(loc)
      (loc(0), loc(1))
    }

    def projectionScreenPositionInView(x: Int, y: Int): (Int, Int) = {
      val loc = new Array[Int](2)
      view.getLocationOnScreen(loc)
      (x - loc(0), y - loc(1))
    }

  }

}
