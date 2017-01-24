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

package cards.nine.commons.utils.impl

import java.io.{ByteArrayInputStream, InputStream}

import android.content.res.AssetManager
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.utils.{FileUtilsData, StreamWrapper}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait StreamWrapperSpecification extends Specification with Mockito {

  trait StreamWrapperScope extends Scope with FileUtilsData {

    val mockContextSupport           = mock[ContextSupport]
    val streamWrapper: StreamWrapper = new StreamWrapperImpl
    val mockInputStream              = mock[InputStream]

  }

  trait OpenAssetsScope { self: StreamWrapperScope =>

    val mockAssetManager = mock[AssetManager]

    mockContextSupport.getAssets returns mockAssetManager
    mockAssetManager.open(fileName) returns mockInputStream

  }

  trait MakeStringScope { self: StreamWrapperScope =>

    val inputStream = new ByteArrayInputStream(sourceString.getBytes)
  }

}

class StreamWrapperImplSpec extends StreamWrapperSpecification {

  "Stream Wrapper" should {

    "return an InputStream when a filename is provided" in {
      new StreamWrapperScope with OpenAssetsScope {
        val result = streamWrapper.openAssetsFile(fileName)(mockContextSupport)
        result mustEqual mockInputStream
      }
    }

    "return a String when an InputStream is provided" in {
      new StreamWrapperScope with MakeStringScope {
        val result = streamWrapper.makeStringFromInputStream(inputStream)
        result mustEqual sourceString
      }
    }

  }
}
