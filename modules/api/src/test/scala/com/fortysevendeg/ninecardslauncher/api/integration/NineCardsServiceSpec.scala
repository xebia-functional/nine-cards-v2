package com.fortysevendeg.ninecardslauncher.api.integration

import com.fortysevendeg.ninecardslauncher.api.integration.ApiServiceHelper._
import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.ninecardslauncher.api.services.{ApiGooglePlayService, ApiRecommendationService, ApiSharedCollectionsService, ApiUserConfigService}
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import org.specs2.specification._
import org.specs2.specification.core.Fragments

import scala.concurrent.ExecutionContext

trait NineCardsServiceSpecification
  extends Specification
  with DisjunctionMatchers
  with MockServerService
  with UserConfigServer
  with SharedCollectionsServer
  with GooglePlayServer
  with RecommendationsServer {
  
  trait NineCardsServiceScope
    extends Scope {

    val serviceClient = new ServiceClient(new OkHttpClient, fakeBaseUrl)

    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    lazy val apiGooglePlayService = new ApiGooglePlayService(serviceClient)

    lazy val apiRecommendationService = new ApiRecommendationService(serviceClient)

    lazy val apiSharedCollectionsService = new ApiSharedCollectionsService(serviceClient)

    lazy val apiUserConfigService = new ApiUserConfigService(serviceClient)
    
  }
  
}

class NineCardsServiceSpec
  extends NineCardsServiceSpecification {

  override protected def before: Any = {}

  override protected def after: Any = {}

  override def map(fs: => Fragments) = step(beforeAll) ^ super.map(fs) ^ step(afterAll)

  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.RecommendationImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.SharedCollectionImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._

  "User Config Service component with OkHttpClient" should {

    "returns the UserConfig for a getUserConfig get call" in
      new NineCardsServiceScope {

        val result = apiUserConfigService.getUserConfig(Seq.empty).run

        result must be_\/-[ServiceClientResponse[UserConfig]].which { r =>
          r.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
        }

      }

    "returns the UserConfig for a saveDevice put call" in
      new NineCardsServiceScope {

        val result =
          apiUserConfigService.saveDevice(createUserConfigDevice(), Seq.empty).run

        result must be_\/-[ServiceClientResponse[UserConfig]].which { r =>
          r.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
        }
      }

    "returns the UserConfig for a saveGeoInfo put call" in
      new NineCardsServiceScope {

        val result =
          apiUserConfigService.saveGeoInfo(createUserConfigGeoInfo(), Seq.empty).run

        result must be_\/-[ServiceClientResponse[UserConfig]].which { r =>
          r.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
        }
      }

    "returns the UserConfig for a checkpoint purchase product put call" in
      new NineCardsServiceScope {

        val result =
          apiUserConfigService.checkpointPurchaseProduct(productId, Seq.empty).run

        result must be_\/-[ServiceClientResponse[UserConfig]].which { r =>
          r.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
        }
      }

    "returns the UserConfig for a checkpoint custom collection put call" in
      new NineCardsServiceScope {

        val result =
          apiUserConfigService.checkpointCustomCollection(Seq.empty).run

        result must be_\/-[ServiceClientResponse[UserConfig]].which { r =>
          r.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
        }
      }

    "returns the UserConfig for a checkpoint joined by put call" in
      new NineCardsServiceScope {

        val result =
          apiUserConfigService.checkpointJoinedBy(joinedById, Seq.empty).run

        result must be_\/-[ServiceClientResponse[UserConfig]].which { r =>
          r.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
        }
      }

    "returns the UserConfig for a tester put call" in
      new NineCardsServiceScope {

        val result =
          apiUserConfigService.tester(testerValues, Seq.empty).run

        result must be_\/-[ServiceClientResponse[UserConfig]].which { r =>
          r.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
        }
      }

  }

  "Shared Collections Service component with OkHttpClient" should {

    "returns the SharedCollection for a getSharedCollection get call" in
      new NineCardsServiceScope {

        val result = 
          apiSharedCollectionsService.getSharedCollection(sharedCollectionIdFirst, Seq.empty).run

        result must be_\/-[ServiceClientResponse[SharedCollection]].which { r =>
          r.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
        }
      }

    "returns the SharedCollectionList for a getSharedCollectionList get call" in
      new NineCardsServiceScope {

        val result = 
          apiSharedCollectionsService.getSharedCollectionList(sharedCollectionType, 0, 0, Seq.empty).run

        result must be_\/-[ServiceClientResponse[SharedCollectionList]].which { r =>
          r.data must beSome[SharedCollectionList].which { collectionList =>
            collectionList.items must have size sharedCollectionSize
            collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
            collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
          }
        }
      }

    "returns the SharedCollectionList for a getSharedCollectionListByCategory get call" in
      new NineCardsServiceScope {

        val result = 
          apiSharedCollectionsService.getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty).run

        result must be_\/-[ServiceClientResponse[SharedCollectionList]].which { r =>
          r.data must beSome[SharedCollectionList].which { collectionList =>
            collectionList.items must have size sharedCollectionSize
            collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
            collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
          }
        }
      }

    "returns the searchSharedCollection for a getSharedCollectionListByCategory get call" in
      new NineCardsServiceScope {

        val result = 
          apiSharedCollectionsService.searchSharedCollection(sharedCollectionKeywords, 0, 0, Seq.empty).run

        result must be_\/-[ServiceClientResponse[SharedCollectionList]].which { r =>
          r.data must beSome[SharedCollectionList].which { collectionList =>
            collectionList.items must have size sharedCollectionSize
            collectionList.items.head.sharedCollectionId shouldEqual sharedCollectionIdFirst
            collectionList.items.last.sharedCollectionId shouldEqual sharedCollectionIdLast
          }
        }
      }

    "returns the SharedCollection for a shareCollection post call" in
      new NineCardsServiceScope {

        val result = 
          apiSharedCollectionsService.shareCollection(createSharedCollection(), Seq.empty).run

        result must be_\/-[ServiceClientResponse[SharedCollection]].which { r =>
          r.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
        }
      }

    "returns the SharedCollection for a rateSharedCollection post call" in
      new NineCardsServiceScope {

        val result = 
          apiSharedCollectionsService.rateSharedCollection(sharedCollectionIdFirst, 0.0, Seq.empty).run

        result must be_\/-[ServiceClientResponse[SharedCollection]].which { r =>
          r.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
        }
      }

    "returns the SharedCollection for a subscribeSharedCollection put call" in
      new NineCardsServiceScope {

        val result = 
          apiSharedCollectionsService.subscribeSharedCollection(sharedCollectionIdFirst, Seq.empty).run

        result must be_\/-[ServiceClientResponse[SharedCollectionSubscription]].which { r =>
          r.data must beSome[SharedCollectionSubscription].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
        }
      }

    "returns the SharedCollection for a unsubscribeSharedCollection delete call" in
      new NineCardsServiceScope {

        val result = 
          apiSharedCollectionsService.unsubscribeSharedCollection(sharedCollectionIdFirst, Seq.empty).run

        result must be_\/-[ServiceClientResponse[Nothing]].which { r =>
          r.data must beNone
        }
      }

  }

  "Google Play Service component with OkHttpClient" should {

    "returns the GooglePlayPackage for a getGooglePlayPackage get call" in
      new NineCardsServiceScope {

        val result = 
          apiGooglePlayService.getGooglePlayPackage(packageName1, Seq.empty).run

        result must be_\/-[ServiceClientResponse[GooglePlayPackage]].which { r =>
          r.data must beSome[GooglePlayPackage].which(_.docV2.docid shouldEqual packageName1)
        }
      }

    "returns the GooglePlayPackages for a getGooglePlayPackages post call" in
      new NineCardsServiceScope {

        val result = 
          apiGooglePlayService.getGooglePlayPackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty).run

        result must be_\/-[ServiceClientResponse[GooglePlayPackages]].which { r =>
          r.data must beSome[GooglePlayPackages].which { packages =>
            packages.items must have size 2
            packages.items.head.docV2.docid shouldEqual packageName1
            packages.items.last.docV2.docid shouldEqual packageName2
          }
        }
      }

    "returns the GooglePlaySimplePackages for a getGooglePlaySimplePackages post call" in
      new NineCardsServiceScope {

        val result = 
          apiGooglePlayService.getGooglePlaySimplePackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty).run

        result must be_\/-[ServiceClientResponse[GooglePlaySimplePackages]].which { r =>
          r.data must beSome[GooglePlaySimplePackages].which { packages =>
            packages.items must have size 2
            packages.items.head.packageName shouldEqual packageName1
            packages.items.last.packageName shouldEqual packageName2
          }
        }
      }

    "returns the GooglePlaySearch for a search get call" in
      new NineCardsServiceScope {

        val result = 
          apiGooglePlayService.searchGooglePlay(searchQuery, searchOffset, searchLimit, Seq.empty).run

        result must be_\/-[ServiceClientResponse[GooglePlaySearch]].which { r =>
          r.data must beSome[GooglePlaySearch].which(_.originalQuery shouldEqual searchQuery)
        }
      }
  }

  "Recommendation Service component with OkHttpClient" should {

    "returns the GooglePlayRecommendation for a getRecommendedApps get call" in
      new NineCardsServiceScope {

        val result = 
          apiRecommendationService.getRecommendedApps(createRecommendationRequest(), Seq.empty).run

        result must be_\/-[ServiceClientResponse[GooglePlayRecommendation]].which { r =>
          r.data must beSome[GooglePlayRecommendation].which(_.items must have size 6)
        }
      }

    "returns the CollectionSponsored for a getSponsoredCollections get call" in
      new NineCardsServiceScope {

        val result = 
          apiRecommendationService.getSponsoredCollections(Seq.empty).run

        result must be_\/-[ServiceClientResponse[CollectionSponsored]].which { r =>
          r.data must beSome[CollectionSponsored].which(_.items must have size 3)
        }
      }
  }

}
