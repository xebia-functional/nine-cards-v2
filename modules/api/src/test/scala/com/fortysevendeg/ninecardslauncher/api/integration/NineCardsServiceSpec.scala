package com.fortysevendeg.ninecardslauncher.api.integration

import akka.actor.ActorSystem
import com.fortysevendeg.BaseTestSupport
import com.fortysevendeg.ninecardslauncher.api.NineCardsServiceClient
import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.{HttpClient, OkHttpClient, SprayHttpClient}
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

  def createAdsRequest(
      userAgentHeader: String = "",
      sessionId: String = "",
      ipAddress: String = "",
      siteId: String = "",
      placementId: String = "",
      totalCampaignsRequested: String = "",
      adTypeId: String = "",
      categoryId: String = "",
      androidId: String = "",
      aIdSHA1: String = "",
      aIdMD5: String = "",
      idfa: String = "",
      macAddress: String = "",
      campaignId: String = ""): AdsRequest =
    AdsRequest(
      userAgentHeader = userAgentHeader,
      sessionId = sessionId,
      ipAddress = ipAddress,
      siteId = siteId,
      placementId = placementId,
      totalCampaignsRequested = totalCampaignsRequested,
      adTypeId = adTypeId,
      categoryId = categoryId,
      androidId = androidId,
      aIdSHA1: String,
      aIdMD5: String,
      idfa = idfa,
      macAddress = macAddress,
      campaignId = campaignId)


  def createRecommendationRequest(
      collectionId: String = "",
      categories: Seq[String] = Seq.empty,
      adPresenceRatio: Double = 0.0,
      likePackages: Seq[String] = Seq.empty,
      excludePackages: Seq[String] = Seq.empty,
      limit: Int = 10,
      adsRequest: AdsRequest = createAdsRequest()): RecommendationRequest =
    RecommendationRequest(
      collectionId = collectionId,
      categories = categories,
      adPresenceRatio = adPresenceRatio,
      likePackages = likePackages,
      excludePackages = excludePackages,
      limit = limit,
      adsRequest = adsRequest)

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

class NineCardsServiceSpec
    extends Specification
    with MockServerService
    with UserConfigServer
    with SharedCollectionsServer
    with GooglePlayServer
    with RecommendationsServer
    with BaseTestSupport {

  override protected def before: Any = {}

  override protected def after: Any = {}

  override def map(fs: => Fragments) = step(beforeAll) ^ super.map(fs) ^ step(afterAll)

  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.SharedCollectionImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.RecommendationImplicits._

  "User Config Service component with OkHttpClient" should {

    "returns the UserConfig for a getUserConfig get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(getUserConfig(Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a saveDevice put call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            saveDevice(createUserConfigDevice(), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a saveGeoInfo put call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            saveGeoInfo(createUserConfigGeoInfo(), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a checkpoint purchase product put call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            checkpointPurchaseProduct(productId, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a checkpoint custom collection put call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            checkpointCustomCollection(Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a checkpoint joined by put call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            checkpointJoinedBy(joinedById, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a tester put call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            tester(testerValues, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

  }

  "Shared Collections Service component with OkHttpClient" should {

    "returns the SharedCollection for a getSharedCollection get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            getSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollectionList for a getSharedCollectionList get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            getSharedCollectionList(sharedCollectionType, 0, 0, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual sharedCollectionSize
          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the SharedCollectionList for a getSharedCollectionListByCategory get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual sharedCollectionSize
          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the searchSharedCollection for a getSharedCollectionListByCategory get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            searchSharedCollection(sharedCollectionKeywords, 0, 0, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual sharedCollectionSize
          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the SharedCollection for a shareCollection post call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            shareCollection(createSharedCollection(), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a rateSharedCollection post call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            rateSharedCollection(sharedCollectionIdFirst, 0.0, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a subscribeSharedCollection put call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            subscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a unsubscribeSharedCollection delete call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            unsubscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          result.data == None
        }

  }

  "Google Play Service component with OkHttpClient" should {

    "returns the GooglePlayPackage for a getGooglePlayPackage get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(getGooglePlayPackage(packageName1, Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.docV2.docid shouldEqual packageName1
        }

    "returns the GooglePlayPackages for a getGooglePlayPackages post call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(getGooglePlayPackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 2
          result.data.get.items.head.docV2.docid shouldEqual packageName1
          result.data.get.items(1).docV2.docid shouldEqual packageName2
        }

    "returns the GooglePlaySimplePackages for a getGooglePlaySimplePackages post call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            getGooglePlaySimplePackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 2
          result.data.get.items.head.packageName shouldEqual packageName1
          result.data.get.items(1).packageName shouldEqual packageName2
        }

    "returns the GooglePlaySearch for a search get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(searchGooglePlay(searchQuery, searchOffset, searchLimit, Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.originalQuery shouldEqual searchQuery
        }
  }

  "Recommendation Service component with OkHttpClient" should {

    "returns the GooglePlayRecommendation for a getRecommendedApps get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(
            getRecommendedApps(createRecommendationRequest(), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 6
        }

    "returns the CollectionsSponsored for a getSponsoredCollections get call" in
        new NineCardsServiceOkHttpSupport {

          val result = Await.result(getSponsoredCollections(Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 3
        }
  }

  "User Config Service component with SprayHttpClient" should {

    "returns the UserConfig for a getUserConfig get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(getUserConfig(Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a saveDevice put call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            saveDevice(createUserConfigDevice(), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a saveGeoInfo put call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            saveGeoInfo(createUserConfigGeoInfo(), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a checkpoint purchase product put call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            checkpointPurchaseProduct(productId, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a checkpoint custom collection put call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            checkpointCustomCollection(Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a checkpoint joined by put call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            checkpointJoinedBy(joinedById, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

    "returns the UserConfig for a tester put call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            tester(testerValues, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get._id shouldEqual userConfigIdFirst
        }

  }

  "Shared Collections Service component with SprayHttpClient" should {

    "returns the SharedCollection for a getSharedCollection get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            getSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollectionList for a getSharedCollectionList get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            getSharedCollectionList(sharedCollectionType, 0, 0, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual sharedCollectionSize
          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the SharedCollectionList for a getSharedCollectionListByCategory get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual sharedCollectionSize
          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the searchSharedCollection for a getSharedCollectionListByCategory get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            searchSharedCollection(sharedCollectionKeywords, 0, 0, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual sharedCollectionSize
          result.data.get.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          result.data.get.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }

    "returns the SharedCollection for a shareCollection post call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            shareCollection(createSharedCollection(), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a rateSharedCollection post call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            rateSharedCollection(sharedCollectionIdFirst, 0.0, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a subscribeSharedCollection put call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            subscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.sharedCollectionId shouldEqual sharedCollectionIdFirst
        }

    "returns the SharedCollection for a unsubscribeSharedCollection delete call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            unsubscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
            Duration.Inf)

          result.data == None
        }

  }

  "Google Play Service component with SprayHttpClient" should {

    "returns the GooglePlayPackage for a getGooglePlayPackage get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(getGooglePlayPackage(packageName1, Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.docV2.docid shouldEqual packageName1
        }

    "returns the GooglePlayPackages for a getGooglePlayPackages post call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(getGooglePlayPackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 2
          result.data.get.items.head.docV2.docid shouldEqual packageName1
          result.data.get.items(1).docV2.docid shouldEqual packageName2
        }

    "returns the GooglePlaySimplePackages for a getGooglePlaySimplePackages post call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(getGooglePlaySimplePackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 2
          result.data.get.items.head.packageName shouldEqual packageName1
          result.data.get.items(1).packageName shouldEqual packageName2
        }

    "returns the GooglePlaySearch for a search get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(searchGooglePlay(searchQuery, searchOffset, searchLimit, Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.originalQuery shouldEqual searchQuery
        }
  }

  "Recommendation Service component with SprayHttpClient" should {

    "returns the GooglePlayRecommendation for a getRecommendedApps get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(
            getRecommendedApps(createRecommendationRequest(), Seq.empty),
            Duration.Inf)

          result.data.isDefined shouldEqual true
          // TODO - Add some expectations
        }

    "returns the CollectionsSponsored for a getSponsoredCollections get call" in
        new NineCardsServiceSprayHttpSupport {

          val result = Await.result(getSponsoredCollections(Seq.empty), Duration.Inf)

          result.data.isDefined shouldEqual true
          result.data.get.items.size shouldEqual 3
        }
  }

}
