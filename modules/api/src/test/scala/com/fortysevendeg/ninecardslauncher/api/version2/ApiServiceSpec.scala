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
  with Mockito
  with ApiServiceData {

  trait ApiServiceScope
    extends Scope {

    val mockedServiceClient = mock[ServiceClient]

    mockedServiceClient.baseUrl returns baseUrl

    val apiService = new ApiService(mockedServiceClient)

  }

}

class ApiServiceSpec
  extends ApiServiceSpecification {

  import JsonImplicits._

  "login" should {

    "return the status code and the response" in new ApiServiceScope {

      val response = LoginResponse(apiKey, sessionToken)

      mockedServiceClient.post[LoginRequest, LoginResponse](any, any, any, any, any)(any) returns
        Service(Task(Answer(ServiceClientResponse(statusCodeOk, Some(response)))))

      val request = LoginRequest(email, loginId, tokenId)

      val serviceResponse = apiService.login(request).run.run

      serviceResponse must beLike {
        case Answer(r) =>
          r.statusCode shouldEqual statusCodeOk
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

  "installations" should {

    "return the status code and the response" in new ApiServiceScope {

      val response = InstallationResponse(androidId, deviceToken)

      mockedServiceClient.put[InstallationRequest, InstallationResponse](any, any, any, any, any)(any) returns
        Service(Task(Answer(ServiceClientResponse(statusCodeNotFound, Some(response)))))

      val request = InstallationRequest(deviceToken)

      val serviceClientResponse = apiService.installations(request, apiKey, sessionToken, androidId).run.run

      serviceClientResponse must beLike {
        case Answer(r) =>
          r.statusCode shouldEqual statusCodeNotFound
          r.data must beSome(response)
      }

      val headers = Seq(
        (headerAuthToken, installationAuthToken),
        (headerSessionToken, sessionToken),
        (headerAndroidId, androidId),
        (headerMarketLocalization, headerMarketLocalizationValue))

      there was one(mockedServiceClient).put(
        path = "/installations",
        headers = headers,
        body = request,
        reads = Some(JsonImplicits.installationResponseReads),
        emptyResponse = false)(JsonImplicits.installationRequestWrites)

    }

  }

}
