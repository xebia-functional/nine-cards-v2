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

package cards.nine.services.utils

import java.io.File

import cards.nine.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ResourceUtilsSpecification extends Specification with Mockito {

  trait ResourceUtilsScope extends Scope with ResourceUtilsData {

    val mockContextSupport = mock[ContextSupport]
    val resourceUtils      = new ResourceUtils
    val mockFile           = mock[File]
  }
}

class ResourceUtilsSpec extends ResourceUtilsSpecification {

  "Resource Utils" should {

    "return the file path when a valid file name is provided" in
      new ResourceUtilsScope {

        mockContextSupport.getAppIconsDir returns mockFile
        mockFile.getPath returns fileFolder

        val result = resourceUtils.getPath(fileName)(mockContextSupport)
        result shouldEqual resultFilePath
      }
  }
}
