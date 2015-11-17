package com.fortysevendeg.ninecardslauncher.services.calls.impl

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.ContentResolverWrapperImpl
import com.fortysevendeg.ninecardslauncher.services.calls.CallsContentProvider._
import com.fortysevendeg.ninecardslauncher.services.calls.CallsServicesException
import com.fortysevendeg.ninecardslauncher.services.contacts.Fields
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

trait CallsServicesSpecification
  extends Specification
  with Mockito
  with CallsServicesImplData {

  trait CallsServicesScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val callServices = new CallsServicesImpl(contentResolverWrapper)

  }

  trait ValidCallsServicesResponses
    extends CallsServicesScope {

    contentResolverWrapper.fetchAll(
      uri = Fields.CALL_CONTENT_URI,
      projection = allFields,
      orderBy = s"${Fields.CALL_DATE} desc")(getListFromCursor(callFromCursor)) returns calls

  }

  trait ErrorCallsServicesResponses
    extends CallsServicesScope
    with CallsServicesImplData {

    val contentResolverException = new RuntimeException("Irrelevant message")

    contentResolverWrapper.fetchAll(
      uri = Fields.CALL_CONTENT_URI,
      projection = allFields,
      orderBy = s"${Fields.CALL_DATE} desc")(getListFromCursor(callFromCursor)) throws contentResolverException

  }

}

class CallsServicesImplSpec
  extends CallsServicesSpecification {

  "CallsService component" should {

    "getCalls" should {

      "returns all the contacts from the content resolver" in
        new ValidCallsServicesResponses {
          val result = callServices.getLastCalls.run.run

          result must beLike {
            case Answer(seq) => seq shouldEqual calls
          }
        }

      "return a CallsServiceException when the content resolver throws an exception" in
        new ErrorCallsServicesResponses {
          val result = callServices.getLastCalls.run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beLike {
                case e: CallsServicesException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }

    }

  }

}
