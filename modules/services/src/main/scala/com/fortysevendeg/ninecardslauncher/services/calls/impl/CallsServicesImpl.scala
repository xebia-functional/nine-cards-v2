package com.fortysevendeg.ninecardslauncher.services.calls.impl

import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.ContentResolverWrapper
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.services.calls.CallsContentProvider.{allFields, _}
import com.fortysevendeg.ninecardslauncher.services.calls.{CallsServices, CallsServicesException, ImplicitsCallsExceptions}
import com.fortysevendeg.ninecardslauncher.services.contacts._

import scalaz.concurrent.Task

class CallsServicesImpl(
  contentResolverWrapper: ContentResolverWrapper)
  extends CallsServices
  with ImplicitsCallsExceptions {

  override def getLastCalls =
    CatsService {
      Task {
        XorCatchAll[CallsServicesException] {
          contentResolverWrapper.fetchAll(
            uri = Fields.CALL_CONTENT_URI,
            projection = allFields,
            orderBy = s"${Fields.CALL_DATE} desc")(getListFromCursor(callFromCursor))
        }
      }
    }

}
