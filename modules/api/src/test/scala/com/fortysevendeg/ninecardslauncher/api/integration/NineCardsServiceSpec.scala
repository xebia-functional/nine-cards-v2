package com.fortysevendeg.ninecardslauncher.api.integration

import akka.actor.ActorSystem
import com.fortysevendeg.BaseTestSupport
import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.ninecardslauncher.api.services.{ApiGooglePlayService, ApiRecommendationService, ApiSharedCollectionsService, ApiUserConfigService}
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.{OkHttpClient, SprayHttpClient}
import org.specs2.mutable.Specification
import org.specs2.specification._
import org.specs2.specification.core.Fragments
import com.fortysevendeg.ninecardslauncher.api.integration.ApiServiceHelper._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait NineCardsServiceSupport
  extends Scope {

  val serviceClient: ServiceClient

  lazy val apiGooglePlayService = new ApiGooglePlayService(serviceClient)

  lazy val apiRecommendationService = new ApiRecommendationService(serviceClient)

  lazy val apiSharedCollectionsService = new ApiSharedCollectionsService(serviceClient)

  lazy val apiUserConfigService = new ApiUserConfigService(serviceClient)
}

trait WithOkServiceSupport
  extends NineCardsServiceSupport {

  val serviceClient = new ServiceClient(new OkHttpClient, fakeBaseUrl)

}

trait WithSprayServiceSupport
  extends NineCardsServiceSupport {

  implicit val actorSystem: ActorSystem = ActorSystem("http-spray-client")

  val serviceClient = new ServiceClient(new SprayHttpClient, fakeBaseUrl)

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
  import com.fortysevendeg.ninecardslauncher.api.reads.RecommendationImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.SharedCollectionImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._

  "User Config Service component with OkHttpClient" should {

    "returns the UserConfig for a getUserConfig get call" in
      new WithOkServiceSupport {

        val result = Await.result(apiUserConfigService.getUserConfig(Seq.empty), Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a saveDevice put call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiUserConfigService.saveDevice(createUserConfigDevice(), Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a saveGeoInfo put call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiUserConfigService.saveGeoInfo(createUserConfigGeoInfo(), Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a checkpoint purchase product put call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiUserConfigService.checkpointPurchaseProduct(productId, Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a checkpoint custom collection put call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiUserConfigService.checkpointCustomCollection(Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a checkpoint joined by put call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiUserConfigService.checkpointJoinedBy(joinedById, Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a tester put call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiUserConfigService.tester(testerValues, Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

  }

  "Shared Collections Service component with OkHttpClient" should {

    "returns the SharedCollection for a getSharedCollection get call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.getSharedCollection(sharedCollectionIdFirst, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
      }

    "returns the SharedCollectionList for a getSharedCollectionList get call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.getSharedCollectionList(sharedCollectionType, 0, 0, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollectionList].which { collectionList =>
          collectionList.items must have size sharedCollectionSize
          collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }
      }

    "returns the SharedCollectionList for a getSharedCollectionListByCategory get call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollectionList].which { collectionList =>
          collectionList.items must have size sharedCollectionSize
          collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }
      }

    "returns the searchSharedCollection for a getSharedCollectionListByCategory get call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.searchSharedCollection(sharedCollectionKeywords, 0, 0, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollectionList].which { collectionList =>
          collectionList.items must have size sharedCollectionSize
          collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }
      }

    "returns the SharedCollection for a shareCollection post call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.shareCollection(createSharedCollection(), Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
      }

    "returns the SharedCollection for a rateSharedCollection post call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.rateSharedCollection(sharedCollectionIdFirst, 0.0, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
      }

    "returns the SharedCollection for a subscribeSharedCollection put call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.subscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollectionSubscription].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
      }

    "returns the SharedCollection for a unsubscribeSharedCollection delete call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.unsubscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
          Duration.Inf)

        result.data must beNone
      }

  }

  "Google Play Service component with OkHttpClient" should {

    "returns the GooglePlayPackage for a getGooglePlayPackage get call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiGooglePlayService.getGooglePlayPackage(packageName1, Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlayPackage].which(_.docV2.docid shouldEqual packageName1)
      }

    "returns the GooglePlayPackages for a getGooglePlayPackages post call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiGooglePlayService.getGooglePlayPackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlayPackages].which { packages =>
          packages.items must have size 2
          packages.items.head.docV2.docid shouldEqual packageName1
          packages.items.last.docV2.docid shouldEqual packageName2
        }
      }

    "returns the GooglePlaySimplePackages for a getGooglePlaySimplePackages post call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiGooglePlayService.getGooglePlaySimplePackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlaySimplePackages].which { packages =>
          packages.items must have size 2
          packages.items.head.packageName shouldEqual packageName1
          packages.items.last.packageName shouldEqual packageName2
        }
      }

    "returns the GooglePlaySearch for a search get call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiGooglePlayService.searchGooglePlay(searchQuery, searchOffset, searchLimit, Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlaySearch].which(_.originalQuery shouldEqual searchQuery)
      }
  }

  "Recommendation Service component with OkHttpClient" should {

    "returns the GooglePlayRecommendation for a getRecommendedApps get call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiRecommendationService.getRecommendedApps(createRecommendationRequest(), Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlayRecommendation].which(_.items must have size 6)
      }

    "returns the CollectionSponsored for a getSponsoredCollections get call" in
      new WithOkServiceSupport {

        val result = Await.result(
          apiRecommendationService.getSponsoredCollections(Seq.empty),
          Duration.Inf)

        result.data must beSome[CollectionSponsored].which(_.items must have size 3)
      }
  }

  "User Config Service component with SprayHttpClient" should {

    "returns the UserConfig for a getUserConfig get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiUserConfigService.getUserConfig(Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a saveDevice put call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiUserConfigService.saveDevice(createUserConfigDevice(), Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a saveGeoInfo put call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiUserConfigService.saveGeoInfo(createUserConfigGeoInfo(), Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a checkpoint purchase product put call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiUserConfigService.checkpointPurchaseProduct(productId, Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a checkpoint custom collection put call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiUserConfigService.checkpointCustomCollection(Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a checkpoint joined by put call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiUserConfigService.checkpointJoinedBy(joinedById, Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

    "returns the UserConfig for a tester put call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiUserConfigService.tester(testerValues, Seq.empty),
          Duration.Inf)

        result.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
      }

  }

  "Shared Collections Service component with SprayHttpClient" should {

    "returns the SharedCollection for a getSharedCollection get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.getSharedCollection(sharedCollectionIdFirst, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
      }

    "returns the SharedCollectionList for a getSharedCollectionList get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.getSharedCollectionList(sharedCollectionType, 0, 0, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollectionList].which { collectionList =>
          collectionList.items must have size sharedCollectionSize
          collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }
      }

    "returns the SharedCollectionList for a getSharedCollectionListByCategory get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollectionList].which { collectionList =>
          collectionList.items must have size sharedCollectionSize
          collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }
      }

    "returns the searchSharedCollection for a getSharedCollectionListByCategory get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.searchSharedCollection(sharedCollectionKeywords, 0, 0, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollectionList].which { collectionList =>
          collectionList.items must have size sharedCollectionSize
          collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
          collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
        }
      }

    "returns the SharedCollection for a shareCollection post call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.shareCollection(createSharedCollection(), Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
      }

    "returns the SharedCollection for a rateSharedCollection post call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.rateSharedCollection(sharedCollectionIdFirst, 0.0, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
      }

    "returns the SharedCollection for a subscribeSharedCollection put call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.subscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
          Duration.Inf)

        result.data must beSome[SharedCollectionSubscription].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
      }

    "returns the SharedCollection for a unsubscribeSharedCollection delete call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiSharedCollectionsService.unsubscribeSharedCollection(sharedCollectionIdFirst, Seq.empty),
          Duration.Inf)

        result.data must beNone
      }

  }

  "Google Play Service component with SprayHttpClient" should {

    "returns the GooglePlayPackage for a getGooglePlayPackage get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiGooglePlayService.getGooglePlayPackage(packageName1, Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlayPackage].which(_.docV2.docid shouldEqual packageName1)
      }

    "returns the GooglePlayPackages for a getGooglePlayPackages post call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiGooglePlayService.getGooglePlayPackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlayPackages].which { collectionList =>
          collectionList.items must have size 2
          collectionList.items.head.docV2.docid shouldEqual packageName1
          collectionList.items.last.docV2.docid shouldEqual packageName2
        }
      }

    "returns the GooglePlaySimplePackages for a getGooglePlaySimplePackages post call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiGooglePlayService.getGooglePlaySimplePackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlaySimplePackages].which { collectionList =>
          collectionList.items must have size 2
          collectionList.items.head.packageName shouldEqual packageName1
          collectionList.items.last.packageName shouldEqual packageName2
        }
      }

    "returns the GooglePlaySearch for a search get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiGooglePlayService.searchGooglePlay(searchQuery, searchOffset, searchLimit, Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlaySearch].which(_.originalQuery shouldEqual searchQuery)
      }
  }

  "Recommendation Service component with SprayHttpClient" should {

    "returns the GooglePlayRecommendation for a getRecommendedApps get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiRecommendationService.getRecommendedApps(createRecommendationRequest(), Seq.empty),
          Duration.Inf)

        result.data must beSome[GooglePlayRecommendation].which(_.items must have size 6)
      }

    "returns the CollectionsSponsored for a getSponsoredCollections get call" in
      new WithSprayServiceSupport {

        val result = Await.result(
          apiRecommendationService.getSponsoredCollections(Seq.empty),
          Duration.Inf)

        result.data must beSome[CollectionSponsored].which(_.items must have size 3)
      }
  }

}
