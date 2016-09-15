package com.fortysevendeg.ninecardslauncher.api.version1

import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope


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

      mockedServiceClient.post[User, User](any, any, any, any, any)(any) returns
        TaskService(Task(Either.right(ServiceClientResponse(statusCodeOk, Some(user)))))

      val serviceResponse = apiService.login(emptyUser, headers).value.run

      serviceResponse must beLike {
        case Right(r) =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beSome(user)
      }

      there was one(mockedServiceClient).post(
        path = "/users",
        headers = headers,
        body = emptyUser,
        reads = Some(userReads),
        emptyResponse = false)(userWrites)

    }

  }

  "getUserConfig" should {

    "return the status code and the response" in new ApiServiceScope {

      mockedServiceClient.get[UserConfig](any, any, any, any) returns
        TaskService(Task(Either.right(ServiceClientResponse(statusCodeOk, Some(userConfig)))))

      val serviceClientResponse = apiService.getUserConfig(headers).value.run

      serviceClientResponse must beLike {
        case Right(r) =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beSome(userConfig)
      }

      there was one(mockedServiceClient).get(
        path = "/ninecards/userconfig",
        headers = headers,
        reads = Some(userConfigReads),
        emptyResponse = false)

    }

  }
}
