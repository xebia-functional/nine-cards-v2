package cards.nine.commons.utils

import java.io._

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.utils.impl.StreamWrapperImpl

import scala.util.Try
import scala.util.control.Exception._

class FileUtils(streamWrapper: StreamWrapper = new StreamWrapperImpl)
  extends ImplicitsAssetException {

  def readFile(filename: String)(implicit context: ContextSupport): Try[String] =
    Try {
      withResource[InputStream, String](streamWrapper.openAssetsFile(filename)) {
        stream => {
          streamWrapper.makeStringFromInputStream(stream)
        }
      }
    }

  private[this] def withResource[C <: Closeable, R](closeable: C)(f: C => R) = {
    allCatch.andFinally(closeable.close())(f(closeable))
  }

}
