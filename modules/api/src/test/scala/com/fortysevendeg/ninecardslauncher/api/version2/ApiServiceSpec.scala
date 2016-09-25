package com.fortysevendeg.ninecardslauncher.api.version2

import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceSpecification
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait ApiServiceSpecification
  extends TaskServiceSpecification
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

      val response = ApiLoginResponse(apiKey, sessionToken)

      mockedServiceClient.post[ApiLoginRequest, ApiLoginResponse](any, any, any, any, any)(any) returns
        serviceRight(ServiceClientResponse(statusCodeOk, Some(response)))

      val request = ApiLoginRequest(email, loginId, tokenId)

      apiService.login(request) mustRight { r =>
        r.statusCode shouldEqual statusCodeOk
        r.data must beSome(response)
      }

      there was one(mockedServiceClient).post(
        path = "/login",
        headers = Seq((headerContentType, headerContentTypeValue)),
        body = request,
        reads = Some(loginResponseReads),
        emptyResponse = false)(loginRequestWrites)

    }

  }

  "installations" should {

    "return the status code and the response" in new ApiServiceScope {

      val response = InstallationResponse(androidId, deviceToken)

      mockedServiceClient.put[InstallationRequest, InstallationResponse](any, any, any, any, any)(any) returns
        serviceRight(ServiceClientResponse(statusCodeOk, Some(response)))

      val request = InstallationRequest(deviceToken)

      apiService.installations(request, serviceHeader) mustRight { r =>
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
          serviceRight(ServiceClientResponse(statusCodeOk, Some(response)))

        apiService.latestCollections(category, offset, limit, serviceMarketHeader) mustRight { r =>
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
          serviceRight(ServiceClientResponse(statusCodeOk, Some(response)))

        apiService.topCollections(category, offset, limit, serviceMarketHeader) mustRight { r =>
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
          serviceRight(ServiceClientResponse(statusCodeOk, Some(createCollectionResponse)))

        apiService.createCollection(createCollectionRequest, serviceHeader) mustRight { r =>
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
          serviceRight(ServiceClientResponse(statusCodeOk, Some(updateCollectionResponse)))

        apiService.updateCollection(publicIdentifier, updateCollectionRequest, serviceHeader) mustRight { r =>
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
          serviceRight(ServiceClientResponse(statusCodeOk, Some(response)))

        apiService.getCollection(publicIdentifier, serviceMarketHeader) mustRight { r =>
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
          serviceRight(ServiceClientResponse(statusCodeOk, Some(response)))

        apiService.getCollections(serviceMarketHeader) mustRight { r =>
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
          serviceRight(ServiceClientResponse(statusCodeOk, Some(categorizeResponse)))

        apiService.categorize(categorizeRequest, serviceMarketHeader) mustRight { r =>
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

    "categorizeDetail" should {

      "return the status code and the response" in new ApiServiceScope {

        mockedServiceClient.post[CategorizeRequest, CategorizeDetailResponse](any, any, any, any, any)(any) returns
          serviceRight(ServiceClientResponse(statusCodeOk, Some(categorizeDetailResponse)))

        apiService.categorizeDetail(categorizeRequest, serviceMarketHeader) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beSome(categorizeDetailResponse)
        }

        there was one(mockedServiceClient).post(
          path = "/applications/details",
          headers = createMarketHeaders(categorizeDetailAuthToken),
          body = categorizeRequest,
          reads = Some(categorizeDetailResponseReads),
          emptyResponse = false)(categorizeRequestWrites)

      }

    }

    "recommendations" should {

      "return the status code and the response and call with the right category" in new ApiServiceScope {

        val typePay = "FREE"

        mockedServiceClient.post[RecommendationsRequest, RecommendationsResponse](any, any, any, any, any)(any) returns
          serviceRight(ServiceClientResponse(statusCodeOk, Some(recommendationsResponse)))

        apiService.recommendations(category, Option(typePay), recommendationsRequest, serviceMarketHeader) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beSome(recommendationsResponse)
        }

        there was one(mockedServiceClient).post(
          path = s"/recommendations/$category/$typePay",
          headers = createMarketHeaders(recommendationsAuthToken),
          body = recommendationsRequest,
          reads = Some(recommendationsResponseReads),
          emptyResponse = false)(recommendationsRequestWrites)

      }

    }

    "recommendations by apps" should {

      "return the status code and the response" in new ApiServiceScope {

        mockedServiceClient.post[RecommendationsByAppsRequest, RecommendationsByAppsResponse](any, any, any, any, any)(any) returns
          serviceRight(ServiceClientResponse(statusCodeOk, Some(recommendationsByAppsResponse)))

        apiService.recommendationsByApps(recommendationsByAppsRequest, serviceMarketHeader) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beSome(recommendationsByAppsResponse)
        }

        there was one(mockedServiceClient).post(
          path = s"/recommendations",
          headers = createMarketHeaders(recommendationsByAppsAuthToken),
          body = recommendationsByAppsRequest,
          reads = Some(recommendationsByAppsResponseReads),
          emptyResponse = false)(recommendationsByAppsRequestWrites)

      }

    }

    "get subscriptions" should {

      "return the status code and the response" in new ApiServiceScope {

        val response = SubscriptionsResponse(subscriptions)

        mockedServiceClient.get[SubscriptionsResponse](any, any, any, any) returns
          serviceRight(ServiceClientResponse(statusCodeOk, Some(response)))

        apiService.getSubscriptions(serviceHeader) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beSome(response)
        }

        there was one(mockedServiceClient).get(
          path = "/collections/subscriptions",
          headers = createHeaders(subscriptionsAuthToken),
          reads = Some(subscriptionsResponseReads))

      }

    }

    "subscribe" should {

      "return the status code" in new ApiServiceScope {

        mockedServiceClient.emptyPut[Unit](any, any, any, any) returns
          serviceRight(ServiceClientResponse(statusCodeOk, None))

        apiService.subscribe(publicIdentifier, serviceHeader) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beNone
        }

        there was one(mockedServiceClient).emptyPut(
          path = s"/collections/subscriptions/$publicIdentifier",
          headers = createHeaders(subscriptionsAuthToken),
          reads = None,
          emptyResponse = true)

      }

    }

    "unsubscribe" should {

      "return the status code" in new ApiServiceScope {

        mockedServiceClient.delete[Unit](any, any, any, any) returns
          serviceRight(ServiceClientResponse(statusCodeOk, None))

        apiService.unsubscribe(publicIdentifier, serviceHeader) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beNone
        }

        there was one(mockedServiceClient).delete(
          path = s"/collections/subscriptions/$publicIdentifier",
          headers = createHeaders(subscriptionsAuthToken),
          reads = None,
          emptyResponse = true)

      }

    }

  }
}
