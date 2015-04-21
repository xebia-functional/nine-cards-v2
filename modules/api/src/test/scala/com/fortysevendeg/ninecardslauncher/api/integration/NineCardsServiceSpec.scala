package com.fortysevendeg.ninecardslauncher.api.integration

import akka.actor.ActorSystem
import com.fortysevendeg.BaseTestSupport
import com.fortysevendeg.ninecardslauncher.api.NineCardsServiceClient
import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.{HttpClient, OkHttpClient, SprayHttpClient}
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

trait NineCardsServiceSupport
    extends NineCardsServiceClient
    with Scope {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val fakeBaseUrl = "http://localhost:9999"

  def createUserConfigDevice(
      deviceId: String = "",
      deviceName: String = "",
      collections: Seq[UserConfigCollection] = Seq.empty) =
    UserConfigDevice(
      deviceId = deviceId,
      deviceName = deviceName,
      collections = collections)

  def createUserConfigGeoInfo(
      homeMorning: Option[UserConfigUserLocation] = None,
      homeNight: Option[UserConfigUserLocation] = None,
      work: Option[UserConfigUserLocation] = None,
      current: Option[UserConfigUserLocation] = None) =
    UserConfigGeoInfo(
      homeMorning = homeMorning,
      homeNight = homeNight,
      work = work,
      current = current)

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

class NineCardsServiceOkHttpSupport
    extends NineCardsServiceSupport {

  override val serviceClient: ServiceClient = new ServiceClient {

    override val httpClient: HttpClient = new OkHttpClient {}
    
    override val baseUrl: String = fakeBaseUrl

  }

}

class NineCardsServiceSprayHttpSupport
    extends NineCardsServiceSupport {
  
  override val serviceClient: ServiceClient = new ServiceClient {
    
    override val httpClient: HttpClient = new SprayHttpClient {
      implicit val actorSystem: ActorSystem = ActorSystem("http-spray-client")
    }
    
    override val baseUrl: String = fakeBaseUrl
    
  }
}

trait MockServerService
    extends BeforeAfterEach {

  val userConfigIdFirst = "12345678"
  val productId = "987654321"
  val joinedById = "13579"
  val testerValues = Map("key1" -> "value1", "key2" -> "value2")
  val packageName1 = "com.fortysevendeg.scala.android"
  val packageName2 = "com.fortysevendeg.android.scaladays"
  val searchQuery = "scala"
  val searchOffset = 0
  val searchLimit = 10

  val sharedCollectionIdFirst = "12345678"
  val sharedCollectionIdLast = "87654321"
  val sharedCollectionSize = 2
  val sharedCollectionType = "collectionType"
  val sharedCollectionCategory = "collectionCategory"
  val sharedCollectionKeywords = "keyword1,keyword2"

  val userConfigPathPrefix = "/ninecards/userconfig"
  val sharedCollectionPathPrefix = "/ninecards/collections"
  val googlePlayPathPrefix = "/googleplay/package"
  val googlePlayPackagesPathPrefix = "/googleplay/packages/detailed"
  val googlePlaySearchPathPrefix = "/googleplay/search"
  val googlePlaySimplePackagesPathPrefix = "/googleplay/packages/simple"
  val regexpPath = "[a-zA-Z0-9,\\.\\/]*"
  val jsonHeader = new Header("Content-Type", "application/json; charset=utf-8")
  val userConfigJson = "userConfig.json"
  val sharedCollectionJsonSingle = "sharedCollection.json"
  val sharedCollectionJsonList = "sharedCollectionList.json"
  val sharedCollectionJsonSubscription = "sharedCollectionSubscription.json"
  val googlePlayPackageJsonSingle = "googlePlayPackage.json"
  val googlePlayPackageJsonList = "googlePlayPackageList.json"
  val googlePlaySimplePackageJsonList = "googlePlaySimplePackageList.json"
  val googlePlaySearchJson = "googlePlaySearch.json"

  lazy val mockServer = startClientAndServer(9999)

  def beforeAll = {
    Logging.overrideLogLevel("ERROR")
    mockServer
  }

  def loadJson(file: String): String =
    scala.io.Source.fromInputStream(getClass.getResourceAsStream(s"/$file"), "UTF-8").mkString

  def afterAll = {
    mockServer.stop()
  }

}

class NineCardsServiceSpec
    extends Specification
    with MockServerService
    with BaseTestSupport {

  override protected def before: Any = {}

  override protected def after: Any = {}

  override def map(fs: => Fragments) = step(beforeAll) ^ super.map(fs) ^ step(afterAll)

  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.SharedCollectionImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._

//  "User Config Service component with OkHttpClient" should {
//
//    "returns the UserConfig for a getUserConfig get call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$userConfigPathPrefix"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson)))
//
//          val result = Await.result(getUserConfig(Seq.empty), Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a saveDevice put call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/device"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            saveDevice(createUserConfigDevice(), Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a saveGeoInfo put call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/geoInfo"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            saveGeoInfo(createUserConfigGeoInfo(), Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a checkpoint purchase product put call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/checkpoint/purchase/$productId"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            checkpointPurchaseProduct(productId, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a checkpoint custom collection put call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/checkpoint/collection"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            checkpointCustomCollection(Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a checkpoint joined by put call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/checkpoint/joined/$joinedById"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            checkpointJoinedBy(joinedById, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a tester put call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/tester"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            tester(testerValues, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//  }
//
//  "Shared Collections Service component with OkHttpClient" should {
//
//    "returns the SharedCollection for a getSharedCollection get call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonSingle))
//              )
//
//          val result = Await.result(
//            getSharedCollection(sharedCollectionIdFirst, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
//        }
//
//    "returns the SharedCollectionList for a getSharedCollectionList get call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionType/$regexpPath"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonList))
//              )
//
//          val result = Await.result(
//            getSharedCollectionList(sharedCollectionType, 0, 0, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.items.size shouldEqual sharedCollectionSize
//          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
//          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
//        }
//
//    "returns the SharedCollectionList for a getSharedCollectionListByCategory get call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionType/$regexpPath"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonList))
//              )
//
//          val result = Await.result(
//            getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.items.size shouldEqual sharedCollectionSize
//          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
//          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
//        }
//
//    "returns the searchSharedCollection for a getSharedCollectionListByCategory get call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$sharedCollectionPathPrefix/search/$regexpPath"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonList))
//              )
//
//          val result = Await.result(
//            searchSharedCollection(sharedCollectionKeywords, 0, 0, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.items.size shouldEqual sharedCollectionSize
//          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
//          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
//        }
//
//    "returns the SharedCollection for a shareCollection post call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("POST")
//                .withPath(sharedCollectionPathPrefix))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonSingle))
//              )
//
//          val result = Await.result(
//            shareCollection(createSharedCollection(), Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
//        }
//
//    "returns the SharedCollection for a rateSharedCollection post call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("POST")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/rate/$regexpPath"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonSingle))
//              )
//
//          val result = Await.result(
//            rateSharedCollection(sharedCollectionIdFirst, 0.0, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
//        }
//
//    "returns the SharedCollection for a subscribeSharedCollection put call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/subscribe"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonSubscription))
//              )
//
//          val result = Await.result(
//            subscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
//        }
//
//    "returns the SharedCollection for a unsubscribeSharedCollection delete call" in
//        new NineCardsServiceOkHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("DELETE")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/subscribe"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withBody(" ")
//              )
//
//          val result = Await.result(
//            unsubscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
//            Duration.Inf)
//
//          result.data == None
//        }
//
//  }

  "Google Play Service component with OkHttpClient" should {

    "returns the GooglePlayPackage for a getGooglePlayPackage get call" in
        new NineCardsServiceOkHttpSupport {
          mockServer.when(
            request()
                .withMethod("GET")
                .withPath(s"$googlePlayPathPrefix/$packageName1"))
              .respond(
                response()
                    .withStatusCode(200)
                    .withHeader(jsonHeader)
                    .withBody(loadJson(googlePlayPackageJsonSingle)))

          val result = Await.result(getGooglePlayPackage(packageName1, Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.docV2.docid shouldEqual packageName1
        }

    "returns the GooglePlayPackages for a getGooglePlayPackages post call" in
        new NineCardsServiceOkHttpSupport {
          mockServer.when(
            request()
                .withMethod("POST")
                .withPath(googlePlayPackagesPathPrefix))
              .respond(
                response()
                    .withStatusCode(200)
                    .withHeader(jsonHeader)
                    .withBody(loadJson(googlePlayPackageJsonList)))

          val result = Await.result(getGooglePlayPackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 2
          result.data.get.items.head.docV2.docid shouldEqual packageName1
          result.data.get.items(1).docV2.docid shouldEqual packageName2
        }

    "returns the GooglePlaySimplePackages for a getGooglePlaySimplePackages post call" in
        new NineCardsServiceOkHttpSupport {
          mockServer.when(
            request()
                .withMethod("POST")
                .withPath(googlePlaySimplePackagesPathPrefix))
              .respond(
                response()
                    .withStatusCode(200)
                    .withHeader(jsonHeader)
                    .withBody(loadJson(googlePlaySimplePackageJsonList)))

          val result = Await.result(getGooglePlaySimplePackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 2
          result.data.get.items.head.packageName shouldEqual packageName1
          result.data.get.items(1).packageName shouldEqual packageName2
        }

    "returns the GooglePlaySearch for a search get call" in
        new NineCardsServiceOkHttpSupport {
          mockServer.when(
            request()
                .withMethod("GET")
                .withPath(s"$googlePlaySearchPathPrefix/$searchQuery/$searchOffset/$searchLimit"))
              .respond(
                response()
                    .withStatusCode(200)
                    .withHeader(jsonHeader)
                    .withBody(loadJson(googlePlaySearchJson)))

          val result = Await.result(searchGooglePlay(searchQuery, searchOffset, searchLimit, Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.originalQuery shouldEqual searchQuery
        }
  }

//  "User Config Service component with SprayHttpClient" should {
//
//    "returns the UserConfig for a getUserConfig get call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$userConfigPathPrefix"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson)))
//
//          val result = Await.result(getUserConfig(Seq.empty), Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a saveDevice put call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/device"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            saveDevice(createUserConfigDevice(), Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a saveGeoInfo put call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/geoInfo"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            saveGeoInfo(createUserConfigGeoInfo(), Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a checkpoint purchase product put call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/checkpoint/purchase/$productId"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            checkpointPurchaseProduct(productId, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a checkpoint custom collection put call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/checkpoint/collection"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            checkpointCustomCollection(Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a checkpoint joined by put call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/checkpoint/joined/$joinedById"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            checkpointJoinedBy(joinedById, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//    "returns the UserConfig for a tester put call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$userConfigPathPrefix/tester"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(userConfigJson))
//              )
//
//          val result = Await.result(
//            tester(testerValues, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get._id shouldEqual userConfigIdFirst
//        }
//
//  }
//
//  "Shared Collections Service component with SprayHttpClient" should {
//
//    "returns the SharedCollection for a getSharedCollection get call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonSingle))
//              )
//
//          val result = Await.result(
//            getSharedCollection(sharedCollectionIdFirst, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
//        }
//
//    "returns the SharedCollectionList for a getSharedCollectionList get call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionType/$regexpPath"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonList))
//              )
//
//          val result = Await.result(
//            getSharedCollectionList(sharedCollectionType, 0, 0, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.items.size shouldEqual sharedCollectionSize
//          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
//          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
//        }
//
//    "returns the SharedCollectionList for a getSharedCollectionListByCategory get call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionType/$regexpPath"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonList))
//              )
//
//          val result = Await.result(
//            getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.items.size shouldEqual sharedCollectionSize
//          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
//          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
//        }
//
//    "returns the searchSharedCollection for a getSharedCollectionListByCategory get call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("GET")
//                .withPath(s"$sharedCollectionPathPrefix/search/$regexpPath"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonList))
//              )
//
//          val result = Await.result(
//            searchSharedCollection(sharedCollectionKeywords, 0, 0, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.items.size shouldEqual sharedCollectionSize
//          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
//          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
//        }
//
//    "returns the SharedCollection for a shareCollection post call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("POST")
//                .withPath(sharedCollectionPathPrefix))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonSingle))
//              )
//
//          val result = Await.result(
//            shareCollection(createSharedCollection(), Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
//        }
//
//    "returns the SharedCollection for a rateSharedCollection post call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("POST")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/rate/$regexpPath"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonSingle))
//              )
//
//          val result = Await.result(
//            rateSharedCollection(sharedCollectionIdFirst, 0.0, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
//        }
//
//    "returns the SharedCollection for a subscribeSharedCollection put call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("PUT")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/subscribe"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withHeader(jsonHeader)
//                    .withBody(loadJson(sharedCollectionJsonSubscription))
//              )
//
//          val result = Await.result(
//            subscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
//            Duration.Inf)
//
//          result.data.isDefined shouldEqual true
//          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
//        }
//
//    "returns the SharedCollection for a unsubscribeSharedCollection delete call" in
//        new NineCardsServiceSprayHttpSupport {
//          mockServer.when(
//            request()
//                .withMethod("DELETE")
//                .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/subscribe"))
//              .respond(
//                response()
//                    .withStatusCode(200)
//                    .withBody(" ")
//              )
//
//          val result = Await.result(
//            unsubscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
//            Duration.Inf)
//
//          result.data == None
//        }
//
//  }

}
