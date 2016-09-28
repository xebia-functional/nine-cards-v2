package cards.nine.services.calls.impl

import cards.nine.commons.contentresolver.ContentResolverWrapper
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.services.TaskService
import cards.nine.services.calls.CallsContentProvider.{allFields, _}
import cards.nine.services.calls.{CallsServices, CallsServicesException, CallsServicesPermissionException, ImplicitsCallsExceptions}
import cards.nine.services.contacts._
import monix.eval.Task
import cats.syntax.either._

class CallsServicesImpl(
  contentResolverWrapper: ContentResolverWrapper)
  extends CallsServices
  with ImplicitsCallsExceptions {

  override def getLastCalls =
    TaskService {
      Task {
        Either.catchNonFatal {
          contentResolverWrapper.fetchAll(
            uri = Fields.CALL_CONTENT_URI,
            projection = allFields,
            orderBy = s"${Fields.CALL_DATE} desc")(getListFromCursor(callFromCursor))
        } leftMap {
          case e: SecurityException => CallsServicesPermissionException(e.getMessage, Some(e))
          case e => CallsServicesException(e.getMessage, Option(e))
        }
      }
    }

}
