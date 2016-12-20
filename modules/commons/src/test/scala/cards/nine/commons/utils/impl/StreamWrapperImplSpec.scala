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
