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

package cards.nine.app.ui.commons.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader

class IconFromPackageLoader extends ModelLoader[Int, Int] {

  override def getResourceFetcher(model: Int, width: Int, height: Int): DataFetcher[Int] =
    new DataFetcher[Int]() {
      override def cleanup(): Unit = {}

      override def loadData(priority: Priority): Int = model

      override def cancel(): Unit = {}

      override def getId: String = model.toString
    }

}
