package com.fortysevendeg.ninecardslauncher.services.calls.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{UriCreator, ContentResolverWrapper}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.contacts._
import com.fortysevendeg.ninecardslauncher.services.calls.{ImplicitsCallsExceptions, CallsServicesException, CallsServices}
import com.fortysevendeg.ninecardslauncher.services.calls.CallsContentProvider.{allFields, _}

import scalaz.concurrent.Task

class CallsServicesImpl(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator = new UriCreator)
  extends CallsServices
  with ImplicitsCallsExceptions {

  override def getLastCalls =
    Service {
      Task {
        CatchAll[CallsServicesException] {
          contentResolverWrapper.fetchAll(
            uri = Fields.CALL_CONTENT_URI,
            projection = allFields,
            orderBy = s"${Fields.CALL_DATE} desc")(getListFromCursor(callFromCursor))
        }
      }
    }

}
