package com.fortysevendeg.ninecardslauncher.api.version1.integration

import ApiServiceHelper._
import com.fortysevendeg.ninecardslauncher.api.version1.model._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.GooglePlayImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.RecommendationImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.SharedCollectionImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.UserConfigImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.services.{ApiGooglePlayService, ApiRecommendationService, ApiSharedCollectionsService, ApiUserConfigService}
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.http.OkHttpClient
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import org.specs2.specification._
import org.specs2.specification.core.Fragments
import rapture.core.Answer

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

  "User Config Service component with OkHttpClient" should {

    "return the UserConfig for a getUserConfig get call" in
      new NineCardsServiceScope {

        val result = apiUserConfigService.getUserConfig(Seq.empty).run.run

        result must beLike {
          case Answer(r) => r.data must beSome[UserConfig].which(_._id shouldEqual userConfigIdFirst)
        }

      }

  }

  "Shared Collections Service component with OkHttpClient" should {

    "return the SharedCollectionList for a getSharedCollectionListByCategory get call" in
      new NineCardsServiceScope {

        val result =
          apiSharedCollectionsService.getSharedCollectionListByCategory(sharedCollectionType, sharedCollectionCategory, 0, 0, Seq.empty).run.run

        result must beLike {
          case Answer(r) => r.data must beSome[SharedCollectionList].which { collectionList =>
            collectionList.items must have size sharedCollectionSize
            collectionList.items.headOption map (_.sharedCollectionId) must beSome(sharedCollectionIdFirst)
            collectionList.items.lastOption map (_.sharedCollectionId) must beSome(sharedCollectionIdLast)
          }
        }
      }

    "return the SharedCollection for a shareCollection post call" in
      new NineCardsServiceScope {

        val result =
          apiSharedCollectionsService.shareCollection(createShareCollection(), Seq.empty).run.run

        result must beLike {
          case Answer(r) => r.data must beSome[SharedCollection].which(_.sharedCollectionId shouldEqual sharedCollectionIdFirst)
        }
      }

  }

  "Google Play Service component with OkHttpClient" should {

    "return the GooglePlayPackage for a getGooglePlayPackage get call" in
      new NineCardsServiceScope {

        val result =
          apiGooglePlayService.getGooglePlayPackage(packageName1, Seq.empty).run.run

        result must beLike {
          case Answer(r) => r.data must beSome[GooglePlayPackage].which(_.docV2.docid shouldEqual packageName1)
        }
      }

    "return the GooglePlayPackages for a getGooglePlayPackages post call" in
      new NineCardsServiceScope {

        val result =
          apiGooglePlayService.getGooglePlayPackages(PackagesRequest(Seq(packageName1, packageName2)), Seq.empty).run.run

        result must beLike {
          case Answer(r) => r.data must beSome[GooglePlayPackages].which { packages =>
            packages.items must have size 2
            packages.items.headOption map (_.docV2.docid) must beSome(packageName1)
            packages.items.lastOption map (_.docV2.docid) must beSome(packageName2)
          }
        }
      }
  }

  "Recommendation Service component with OkHttpClient" should {

    "return the GooglePlayRecommendation for a getRecommendedApps get call" in
      new NineCardsServiceScope {

        val result =
          apiRecommendationService.getRecommendedApps(createRecommendationRequest(), Seq.empty).run.run

        result must beLike {
          case Answer(r) => r.data must beSome[GooglePlayRecommendation].which(_.items must have size 6)
        }
      }
  }

}
