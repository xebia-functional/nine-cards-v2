package com.fortysevendeg.ninecardslauncher.api.integration

import akka.actor.ActorSystem
import com.fortysevendeg.BaseTestSupport
import com.fortysevendeg.ninecardslauncher.api.model.{AssetResponse, SharedCollection, SharedCollectionPackage, UserConfigTimeSlot}
import com.fortysevendeg.ninecardslauncher.api.services.SharedCollectionsServiceClient
import com.fortysevendeg.rest.client.HttpClient
import com.typesafe.config.ConfigFactory
import org.mockserver.integration.ClientAndServer._
import org.mockserver.logging.Logging
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.specs2.mutable.Specification
import org.specs2.specification._
import org.specs2.specification.core.Fragments

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class SharedCollectionsServiceSupport
    extends SharedCollectionsServiceClient
    with HttpClient
    with Scope {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  implicit val actorSystem: ActorSystem = ActorSystem("http-spray-client")

  override val baseUrl = "http://localhost:9999"

  override val httpClient = this

  def createSharedCollection(
      _id: String = "",
      sharedCollectionId: String = "",
      publishedOn: Long = 0,
      description: String = "",
      screenshots: Seq[AssetResponse] = Seq.empty,
      author: String = "",
      tags: Seq[String] = Seq.empty,
      name: String = "",
      shareLink: String = "",
      packages: Seq[String] = Seq.empty,
      resolvedPackages: Seq[SharedCollectionPackage] = Seq.empty,
      occurrence: Seq[UserConfigTimeSlot] = Seq.empty,
      lat: Double = 0.0,
      lng: Double = 0.0,
      alt: Double = 0.0,
      views: Int = 1,
      category: String = "",
      icon: String = "",
      community: Boolean = false): SharedCollection =
    SharedCollection(_id = _id,
      sharedCollectionId = sharedCollectionId,
      publishedOn = publishedOn,
      description = description,
      screenshots = screenshots,
      author = author,
      tags = tags,
      name = name,
      shareLink = shareLink,
      packages = packages,
      resolvedPackages = resolvedPackages,
      occurrence = occurrence,
      lat = lat,
      lng = lng,
      alt = alt,
      views = views,
      category = category,
      icon = icon,
      community = community)

}

trait MockServerTestSupport
    extends BeforeAfterEach {

  val sharedCollectionIdFirst = "12345678"
  val sharedCollectionIdLast = "87654321"
  val sharedCollectionType = "collectionType"
  val sharedCollectionCategory = "collectionCategory"
  val sharedCollectionKeywords = "keyword1,keyword2"

  val pathPrefix = "/ninecards/collections"
  val regexpPath = "[a-zA-Z0-9,\\.\\/]*"
  val jsonHeader = new Header("Content-Type", "application/json; charset=utf-8")
  val jsonSingle = "sharedCollection.json"
  val jsonList = "sharedCollectionList.json"
  val jsonSubscription = "sharedCollectionSubscription.json"

  lazy val mockServer = startClientAndServer(9999)

  def beforeAll = {
    Logging.overrideLogLevel("WARN")
    mockServer.when(
      request()
          .withMethod("GET")
          .withPath(s"$pathPrefix/$sharedCollectionIdFirst"))
        .respond(
          response()
              .withStatusCode(200)
              .withHeader(jsonHeader)
              .withBody(loadJson(jsonSingle))
        )
    mockServer.when(
      request()
          .withMethod("GET")
          .withPath(s"$pathPrefix/$sharedCollectionType/$regexpPath"))
        .respond(
          response()
              .withStatusCode(200)
              .withHeader(jsonHeader)
              .withBody(loadJson(jsonList))
        )
    mockServer.when(
      request()
          .withMethod("GET")
          .withPath(s"$pathPrefix/search/$regexpPath"))
        .respond(
          response()
              .withStatusCode(200)
              .withHeader(jsonHeader)
              .withBody(loadJson(jsonList))
        )
    mockServer.when(
      request()
          .withMethod("POST")
          .withPath(pathPrefix))
        .respond(
          response()
              .withStatusCode(200)
              .withHeader(jsonHeader)
              .withBody(loadJson(jsonSingle))
        )
    mockServer.when(
      request()
          .withMethod("POST")
          .withPath(s"$pathPrefix/$sharedCollectionIdFirst/rate/$regexpPath"))
        .respond(
          response()
              .withStatusCode(200)
              .withHeader(jsonHeader)
              .withBody(loadJson(jsonSingle))
        )
    mockServer.when(
      request()
          .withMethod("PUT")
          .withPath(s"$pathPrefix/$sharedCollectionIdFirst/subscribe"))
        .respond(
          response()
              .withStatusCode(200)
              .withHeader(jsonHeader)
              .withBody(loadJson(jsonSubscription))
        )
    mockServer.when(
      request()
          .withMethod("DELETE")
          .withPath(s"$pathPrefix/$sharedCollectionIdFirst/subscribe"))
        .respond(
          response()
              .withStatusCode(200)
              .withBody(" ")
        )
  }

  private def loadJson(file: String): String =
    scala.io.Source.fromInputStream(getClass.getResourceAsStream(s"/$file"), "UTF-8").mkString

  def afterAll = {
    mockServer.stop()
  }

}

class SharedCollectionsServiceSpec
    extends Specification
    with MockServerTestSupport
    with BaseTestSupport {

  override protected def before: Any = {}

  override protected def after: Any = {}

  override def map(fs: => Fragments) = step(beforeAll) ^ super.map(fs) ^ step(afterAll)

  "Test component" should {

    "returns the SharedCollection for a getSharedCollection get call" in
        new SharedCollectionsServiceSupport {

          val response = Await.result(
            getSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          response.data.isDefined shouldEqual true
          response.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollectionList for a getSharedCollectionList get call" in
        new SharedCollectionsServiceSupport {

          val response = Await.result(
            getSharedCollectionList(sharedCollectionType, 0, 0, Seq.empty),
            Duration.Inf)

          response.data.isDefined shouldEqual true
          response.data.get.items.size shouldEqual 2
          response.data.get.items(0).sharedCollectionId shouldEqual sharedCollectionIdFirst
          response.data.get.items(1).sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the SharedCollectionList for a getSharedCollectionListByCategory get call" in
        new SharedCollectionsServiceSupport {

          val response = Await.result(
            getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty),
            Duration.Inf)

          response.data.isDefined shouldEqual true
          response.data.get.items.size shouldEqual 2
          response.data.get.items(0).sharedCollectionId shouldEqual sharedCollectionIdFirst
          response.data.get.items(1).sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the searchSharedCollection for a getSharedCollectionListByCategory get call" in
        new SharedCollectionsServiceSupport {

          val response = Await.result(
            searchSharedCollection(sharedCollectionKeywords, 0, 0, Seq.empty),
            Duration.Inf)

          response.data.isDefined shouldEqual true
          response.data.get.items.size shouldEqual 2
          response.data.get.items(0).sharedCollectionId shouldEqual sharedCollectionIdFirst
          response.data.get.items(1).sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the SharedCollection for a shareCollection post call" in
        new SharedCollectionsServiceSupport {

          val response = Await.result(
            shareCollection(createSharedCollection(), Seq.empty),
            Duration.Inf)

          response.data.isDefined shouldEqual true
          response.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a rateSharedCollection post call" in
        new SharedCollectionsServiceSupport {

          val response = Await.result(
            rateSharedCollection(sharedCollectionIdFirst, 0.0, Seq.empty),
            Duration.Inf)

          response.data.isDefined shouldEqual true
          response.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a subscribeSharedCollection put call" in
        new SharedCollectionsServiceSupport {

          val response = Await.result(
            subscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          response.data.isDefined shouldEqual true
          response.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a unsubscribeSharedCollection delete call" in
        new SharedCollectionsServiceSupport {

          val response = Await.result(
            unsubscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          response.data == None
        }

  }

}
