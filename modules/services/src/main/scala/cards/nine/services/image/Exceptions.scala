package cards.nine.services.image

import cards.nine.commons.services.TaskService.NineCardException


case class FileException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

case class BitmapTransformationException(message: String, cause: Option[Throwable] = None)
  extends RuntimeException(message)
  with NineCardException {
  cause map initCause
}

trait ImplicitsImageExceptions {

  implicit def fileException = (t: Throwable) => FileException(t.getMessage, Option(t))

  implicit def bitmapTransformationException = (t: Throwable) => BitmapTransformationException(t.getMessage, Option(t))

}