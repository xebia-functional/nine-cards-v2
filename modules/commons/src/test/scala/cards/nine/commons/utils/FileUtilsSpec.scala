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

package cards.nine.commons.utils

import java.io._

import cards.nine.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Success

trait FileUtilsSpecification extends Specification with Mockito {

  trait FileUtilsScope extends Scope with FileUtilsData {

    val mockContextSupport = mock[ContextSupport]
    val mockStreamWrapper  = mock[StreamWrapper]
    val mockInputStream    = mock[InputStream]

    val fileUtils = new FileUtils(mockStreamWrapper)

  }

  trait ValidUtilsScope { self: FileUtilsScope =>

    mockStreamWrapper.openAssetsFile(fileName)(mockContextSupport) returns mockInputStream
    mockStreamWrapper.makeStringFromInputStream(mockInputStream) returns fileJson

  }

  trait ErrorUtilsScope { self: FileUtilsScope =>

    mockStreamWrapper.openAssetsFile(fileName)(mockContextSupport) throws new RuntimeException("")

  }

}

class FileUtilsSpec extends FileUtilsSpecification {

  "File Utils" should {

    "returns a json string when a valid fileName is provided" in
      new FileUtilsScope with ValidUtilsScope {
        val result = fileUtils.readFile(fileName)(mockContextSupport)
        result mustEqual Success(fileJson)
      }

    "returns an Exception when the file can't be opened" in
      new FileUtilsScope with ErrorUtilsScope {
        val result = fileUtils.readFile(fileName)(mockContextSupport)
        result must beFailedTry
      }

  }

}
