package com.fortysevendeg.ninecardslauncher.api.version2

import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.Answer

import scalaz.concurrent.Task

trait ApiServiceSpecification
  extends Specification
  with Mockito {

  trait ApiServiceScope
    extends Scope {

    val mockedServiceClient = mock[ServiceClient]

    val apiService = new ApiService(mockedServiceClient)

  }

}

class ApiServiceSpec
  extends ApiServiceSpecification
  with ApiServiceData {

  import JsonImplicits._

  "login" should {

    "return the status code and empty response" in new ApiServiceScope {

      mockedServiceClient.post[LoginRequest, LoginResponse](any, any, any, any, any)(any) returns
        Service(Task(Answer(ServiceClientResponse(statusCodeNotFound, None))))

      val request = LoginRequest(email, loginId, tokenId)

      val serviceResponse = apiService.login(request).run.run

      serviceResponse must beLike {
        case Answer(r) =>
          r.statusCode shouldEqual statusCodeNotFound
          r.data must beNone
      }

      there was one(mockedServiceClient).post(
        path = "/login",
        headers = Seq.empty,
        body = request,
        reads = Some(JsonImplicits.loginResponseReads),
        emptyResponse = false)(JsonImplicits.loginRequestWrites)

    }

    "return the status code and the response" in new ApiServiceScope {

      val response = LoginResponse(apiKey, sessionToken)

      mockedServiceClient.post[LoginRequest, LoginResponse](any, any, any, any, any)(any) returns
        Service(Task(Answer(ServiceClientResponse(statusCodeNotFound, Some(response)))))

      val request = LoginRequest(email, loginId, tokenId)

      val serviceResponse = apiService.login(request).run.run

      serviceResponse must beLike {
        case Answer(r) =>
          r.statusCode shouldEqual statusCodeNotFound
          r.data must beSome(response)
      }

      there was one(mockedServiceClient).post(
        path = "/login",
        headers = Seq.empty,
        body = request,
        reads = Some(JsonImplicits.loginResponseReads),
        emptyResponse = false)(JsonImplicits.loginRequestWrites)

    }

  }

}
