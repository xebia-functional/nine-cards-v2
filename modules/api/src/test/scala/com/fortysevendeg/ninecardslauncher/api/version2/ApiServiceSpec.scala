package com.fortysevendeg.ninecardslauncher.api.version2

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.{CatsService, Service}
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

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
        CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(response)))))

      val request = LoginRequest(email, loginId, tokenId)

      val serviceResponse = apiService.login(request).value.run

      serviceResponse must beLike {
        case Xor.Right(r) =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beSome(response)
      }

      there was one(mockedServiceClient).post(
        path = "/login",
        headers = Seq.empty,
        body = request,
        reads = Some(loginResponseReads),
        emptyResponse = false)(loginRequestWrites)

    }

  }

  "installations" should {

    "return the status code and the response" in new ApiServiceScope {

      val response = InstallationResponse(androidId, deviceToken)

      mockedServiceClient.put[InstallationRequest, InstallationResponse](any, any, any, any, any)(any) returns
        CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(response)))))

      val request = InstallationRequest(deviceToken)

      val serviceClientResponse = apiService.installations(request, serviceHeader).value.run

      serviceClientResponse must beLike {
        case Xor.Right(r) =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beSome(response)
      }

      there was one(mockedServiceClient).put(
        path = "/installations",
        headers = createHeaders(installationAuthToken),
        body = request,
        reads = Some(installationResponseReads),
        emptyResponse = false)(installationRequestWrites)

    }

    "latest collections" should {

      "return the status code and the response" in new ApiServiceScope {

        val response = CollectionsResponse(Seq(collection))

        mockedServiceClient.get[CollectionsResponse](any, any, any, any) returns
          CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(response)))))

        val serviceClientResponse = apiService.latestCollections(category, offset, limit, serviceMarketHeader).value.run

        serviceClientResponse must beLike {
          case Xor.Right(r) =>
            r.statusCode shouldEqual statusCodeOk
            r.data must beSome(response)
        }

        there was one(mockedServiceClient).get(
          path = s"/collections/latest/$category/$offset/$limit",
          headers = createMarketHeaders(latestCollectionsAuthToken),
          reads = Some(collectionsResponseReads),
          emptyResponse = false)

      }

    }

    "top collections" should {

      "return the status code and the response" in new ApiServiceScope {

        val response = CollectionsResponse(Seq(collection))

        mockedServiceClient.get[CollectionsResponse](any, any, any, any) returns
          CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(response)))))

        val serviceClientResponse = apiService.topCollections(category, offset, limit, serviceMarketHeader).value.run

        serviceClientResponse must beLike {
          case Xor.Right(r) =>
            r.statusCode shouldEqual statusCodeOk
            r.data must beSome(response)
        }

        there was one(mockedServiceClient).get(
          path = s"/collections/top/$category/$offset/$limit",
          headers = createMarketHeaders(topCollectionsAuthToken),
          reads = Some(collectionsResponseReads),
          emptyResponse = false)

      }

    }

    "create collection" should {

      "return the status code and the response" in new ApiServiceScope {

        mockedServiceClient.post[CreateCollectionRequest, CreateCollectionResponse](any, any, any, any, any)(any) returns
          CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(createCollectionResponse)))))

        val serviceClientResponse = apiService.createCollection(createCollectionRequest, serviceHeader).value.run

        serviceClientResponse must beLike {
          case Xor.Right(r) =>
            r.statusCode shouldEqual statusCodeOk
            r.data must beSome(createCollectionResponse)
        }

        there was one(mockedServiceClient).post(
          path = "/collections",
          headers = createHeaders(collectionsAuthToken),
          body = createCollectionRequest,
          reads = Some(createCollectionResponseReads),
          emptyResponse = false)(createCollectionRequestWrites)

      }

    }

    "update collection" should {

      "return the status code and the response" in new ApiServiceScope {

        mockedServiceClient.put[UpdateCollectionRequest, UpdateCollectionResponse](any, any, any, any, any)(any) returns
          CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(updateCollectionResponse)))))

        val serviceClientResponse = apiService.updateCollection(publicIdentifier, updateCollectionRequest, serviceHeader).value.run

        serviceClientResponse must beLike {
          case Xor.Right(r) =>
            r.statusCode shouldEqual statusCodeOk
            r.data must beSome(updateCollectionResponse)
        }

        there was one(mockedServiceClient).put(
          path = s"/collections/$publicIdentifier",
          headers = createHeaders(collectionsIdAuthToken),
          body = updateCollectionRequest,
          reads = Some(updateCollectionResponseReads),
          emptyResponse = false)(updateCollectionRequestWrites)

      }

    }

    "get collection" should {

      "return the status code and the response" in new ApiServiceScope {

        val response = collection

        mockedServiceClient.get[Collection](any, any, any, any) returns
          CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(response)))))

        val serviceClientResponse = apiService.getCollection(publicIdentifier, serviceMarketHeader).value.run

        serviceClientResponse must beLike {
          case Xor.Right(r) =>
            r.statusCode shouldEqual statusCodeOk
            r.data must beSome(response)
        }

        there was one(mockedServiceClient).get(
          path = s"/collections/$publicIdentifier",
          headers = createMarketHeaders(collectionsIdAuthToken),
          reads = Some(collectionReads),
          emptyResponse = false)

      }

    }

    "get collections" should {

      "return the status code and the response" in new ApiServiceScope {

        val response = CollectionsResponse(Seq(collection))

        mockedServiceClient.get[CollectionsResponse](any, any, any, any) returns
          CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(response)))))

        val serviceClientResponse = apiService.getCollections(serviceMarketHeader).value.run

        serviceClientResponse must beLike {
          case Xor.Right(r) =>
            r.statusCode shouldEqual statusCodeOk
            r.data must beSome(response)
        }

        there was one(mockedServiceClient).get(
          path = "/collections",
          headers = createMarketHeaders(collectionsAuthToken),
          reads = Some(collectionsResponseReads),
          emptyResponse = false)

      }

    }

    "categorize" should {

      "return the status code and the response" in new ApiServiceScope {

        mockedServiceClient.post[CategorizeRequest, CategorizeResponse](any, any, any, any, any)(any) returns
          CatsService(Task(Xor.Right(ServiceClientResponse(statusCodeOk, Some(categorizeResponse)))))

        val serviceClientResponse = apiService.categorize(categorizeRequest, serviceMarketHeader).value.run

        serviceClientResponse must beLike {
          case Xor.Right(r) =>
            r.statusCode shouldEqual statusCodeOk
            r.data must beSome(categorizeResponse)
        }

        there was one(mockedServiceClient).post(
          path = "/applications/categorize",
          headers = createMarketHeaders(categorizeAuthToken),
          body = categorizeRequest,
          reads = Some(categorizeResponseReads),
          emptyResponse = false)(categorizeRequestWrites)

      }

    }

  }
}
