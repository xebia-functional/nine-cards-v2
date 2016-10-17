package cards.nine.commons.utils.impl

import java.io._
import java.net.{URLConnection, URL}

import android.content.res.AssetManager
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.utils.{FileUtilsData, StreamWrapper}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait StreamWrapperSpecification
  extends Specification
    with Mockito
    with FileUtilsData {

  trait StreamWrapperScope
    extends Scope {

    val mockContextSupport = mock[ContextSupport]
    val streamWrapper: StreamWrapper = new StreamWrapperImpl
    val mockInputStream = mock[InputStream]
    val mockAssetManager = mock[AssetManager]
  }

}

class StreamWrapperImplSpec
  extends StreamWrapperSpecification {

  "Stream Wrapper" should {

    "return an InputStream when a filename is provided" in {
      new StreamWrapperScope {

        mockContextSupport.getAssets returns mockAssetManager
        mockAssetManager.open(any) returns mockInputStream

        val result = streamWrapper.openAssetsFile(fileName)(mockContextSupport)
        result mustEqual mockInputStream
      }
    }

    "return a String when an InputStream is provided" in {
      new StreamWrapperScope {

        val inputStream = new ByteArrayInputStream(sourceString.getBytes)
        val result = streamWrapper.makeStringFromInputStream(inputStream)
        result mustEqual sourceString
      }
    }

    "return a file output stream to write to the file represented by the specified File object" in {
      new StreamWrapperScope {

        val result = streamWrapper.createFileOutputStream(existingFile)
        result must beAnInstanceOf[FileOutputStream]
      }
    }

    "return an Object with the name of the class represented by this" in {
      new StreamWrapperScope {

        val newURL = new URL(uri)
        val result = streamWrapper.createInputStream(uri = uri)
        result.getClass.getName mustEqual newURL.getContent().getClass.getName
      }
    }
  }
}
