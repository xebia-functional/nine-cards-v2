package cards.nine.commons.utils

import cards.nine.commons.services.TaskService.NineCardException

case class AssetException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException{
  cause map initCause
}

trait ImplicitsAssetException {
  implicit def assetException = (t: Throwable) => AssetException(t.getMessage, Option(t))
}