package com.fortysevendeg.ninecardslauncher.services.calls.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.ContentResolverWrapper
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.calls.CallsContentProvider.{allFields, _}
import com.fortysevendeg.ninecardslauncher.services.calls.{CallsServices, CallsServicesException, CallsServicesPermissionException, ImplicitsCallsExceptions}
import com.fortysevendeg.ninecardslauncher.services.contacts._

import scalaz.concurrent.Task

class CallsServicesImpl(
  contentResolverWrapper: ContentResolverWrapper)
  extends CallsServices
  with ImplicitsCallsExceptions {

  override def getLastCalls =
    TaskService {
      Task {
        Xor.catchNonFatal {
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
