package com.fortysevendeg.repository.app

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.App
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity.{allFields, imagePath, name, packageName, _}
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait AppRepositorySpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito
    with AppRepositoryTestData {

  val contentResolverException = new RuntimeException("Irrelevant message")

  trait AppRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val appRepository = new AppRepository(contentResolverWrapper, uriCreator) {
      override protected def getNamesAlphabetically: Seq[String] = appsDataSequence

      override protected def getCategoriesAlphabetically: Seq[String] = categoryDataSequence

      override protected def getInstallationDate: Seq[Long] = installationDateDataSequence
    }

    lazy val mockUri = mock[Uri]

    uriCreator.parse(any) returns mockUri
  }

  trait ErrorCounterAppRepositoryResponses {

    self: AppRepositoryScope =>

    lazy val appRepositoryException = new AppRepository(contentResolverWrapper, uriCreator) {
      override protected def getNamesAlphabetically: Seq[String] =
        throw contentResolverException

      override protected def getCategoriesAlphabetically: Seq[String] =
        throw contentResolverException

      override protected def getInstallationDate: Seq[Long] =
        throw contentResolverException
    }
  }

}

trait AppMockCursor
  extends MockCursor
    with AppRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, appSeq map (_.id), IntDataType),
    (name, 1, appSeq map (_.data.name), StringDataType),
    (packageName, 2, appSeq map (_.data.packageName), StringDataType),
    (className, 3, appSeq map (_.data.className), StringDataType),
    (category, 4, appSeq map (_.data.category), StringDataType),
    (imagePath, 5, appSeq map (_.data.imagePath), StringDataType),
    (dateInstalled, 7, appSeq map (_.data.dateInstalled), LongDataType),
    (dateUpdate, 8, appSeq map (_.data.dateUpdate), LongDataType),
    (version, 9, appSeq map (_.data.version), StringDataType),
    (installedFromGooglePlay, 10, appSeq map (item => if (item.data.installedFromGooglePlay) 1 else 0), IntDataType))

  prepareCursor[App](appSeq.size, cursorData)
}

trait EmptyAppMockCursor
  extends MockCursor
    with AppRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (name, 1, Seq.empty, StringDataType),
    (packageName, 2, Seq.empty, StringDataType),
    (className, 3, Seq.empty, StringDataType),
    (category, 4, Seq.empty, StringDataType),
    (imagePath, 5, Seq.empty, StringDataType),
    (dateInstalled, 7, Seq.empty, LongDataType),
    (dateUpdate, 8, Seq.empty, LongDataType),
    (version, 9, Seq.empty, StringDataType),
    (installedFromGooglePlay, 10, Seq.empty, IntDataType))

  prepareCursor[App](0, cursorData)
}

class AppRepositorySpec
  extends AppRepositorySpecification {

  "AppRepositoryClient component" should {

    "addApp" should {

      "return an App object with a valid request" in
        new AppRepositoryScope {

          contentResolverWrapper.insert(any, any, any) returns testAppId
          val result = appRepository.addApp(data = createAppData).value.run

          result must beLike {
            case Right(appResult) =>
              appResult.id shouldEqual testAppId
              appResult.data.packageName shouldEqual testPackageName
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope {

          contentResolverWrapper.insert(any, any, any) throws contentResolverException
          val result = appRepository.addApp(data = createAppData).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchAlphabeticalAppsCounter" should {

      "return a sequence of DataCounter sort alphabetically" in
        new AppRepositoryScope {

          val result = appRepository.fetchAlphabeticalAppsCounter.value.run
          result shouldEqual Right(appsDataCounters)
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorCounterAppRepositoryResponses {

          val result = appRepositoryException.fetchAlphabeticalAppsCounter.value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }
  }

  "fetchCategorizedAppsCounter" should {

    "return a sequence of DataCounter sort by category" in
      new AppRepositoryScope {

        val result = appRepository.fetchCategorizedAppsCounter.value.run
        result shouldEqual Right(categoryDataCounters)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope
        with ErrorCounterAppRepositoryResponses {

        val result = appRepositoryException.fetchCategorizedAppsCounter.value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "fetchInstallationDateAppsCounter" should {

    "return a sequence of DataCounter sort by installation date" in
      new AppRepositoryScope {

        val result = appRepository.fetchInstallationDateAppsCounter.value.run
        result shouldEqual Right(installationDateDateCounters)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope
        with ErrorCounterAppRepositoryResponses {

        val result = appRepositoryException.fetchInstallationDateAppsCounter.value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "deleteApps" should {

    "return a successful response when all the apps are deleted" in
      new AppRepositoryScope {

        contentResolverWrapper.delete(any, any, any, any) returns 1
        val result = appRepository.deleteApps().value.run
        result shouldEqual Right(1)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope {

        contentResolverWrapper.delete(any, any, any, any) throws contentResolverException
        val result = appRepository.deleteApps().value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "deleteApp" should {

    "return a successful response when a valid app id is given" in
      new AppRepositoryScope {

        contentResolverWrapper.deleteById(any, any, any, any, any) returns 1
        val result = appRepository.deleteApp(app = app).value.run
        result shouldEqual Right(1)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope {

        contentResolverWrapper.deleteById(any, any, any, any, any) throws contentResolverException
        val result = appRepository.deleteApp(app = app).value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "deleteAppByPackage" should {

    "return a successful response when a valid package name is given" in
      new AppRepositoryScope {

        contentResolverWrapper.delete(any, any, any, any) returns 1
        val result = appRepository.deleteAppByPackage(packageName = testPackageName).value.run
        result shouldEqual Right(1)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope {

        contentResolverWrapper.delete(any, any, any, any) throws contentResolverException
        val result = appRepository.deleteAppByPackage(packageName = testPackageName).value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "fetchApps" should {

    "return all the apps stored in the database" in
      new AppRepositoryScope {

        contentResolverWrapper.fetchAll(
          uri = mockUri,
          projection = allFields,
          orderBy = "")(
          f = getListFromCursor(appEntityFromCursor)) returns appEntitySeq

        val result = appRepository.fetchApps().value.run
        result shouldEqual Right(appSeq)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope {

        contentResolverWrapper.fetchAll(
          uri = mockUri,
          projection = allFields,
          orderBy = "")(
          f = getListFromCursor(appEntityFromCursor)) throws contentResolverException

        val result = appRepository.fetchApps().value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "findAppById" should {

    "return an App object when a existent id is given" in
      new AppRepositoryScope {

        contentResolverWrapper.findById[AppEntity](any, any, any, any, any, any)(any) returns Some(appEntity)
        val result = appRepository.findAppById(id = testAppId).value.run

        result must beLike {
          case Right(maybeApp) =>
            maybeApp must beSome[App].which { app =>
              app.id shouldEqual testAppId
              app.data.packageName shouldEqual testPackageName
            }
        }
      }

    "return None when a non-existent id is given" in
      new AppRepositoryScope {

        contentResolverWrapper.findById(any, any, any, any, any, any)(any) returns None
        val result = appRepository.findAppById(id = testNonExistingAppId).value.run
        result shouldEqual Right(None)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope {

        contentResolverWrapper.findById(any, any, any, any, any, any)(any) throws contentResolverException
        val result = appRepository.findAppById(id = testAppId).value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "fetchAppByPackage" should {
    "return an App object when a existent package name is given" in
      new AppRepositoryScope {

        contentResolverWrapper.fetch(
          uri = mockUri,
          projection = allFields,
          where = s"$packageName = ?",
          whereParams = Seq(testPackageName))(
          f = getEntityFromCursor(appEntityFromCursor)) returns Some(appEntity)

        val result = appRepository.fetchAppByPackage(packageName = testPackageName).value.run

        result must beLike {
          case Right(maybeApp) =>
            maybeApp must beSome[App].which { app =>
              app.id shouldEqual testAppId
              app.data.packageName shouldEqual testPackageName
            }
        }
      }

    "return None when a non-existent package name is given" in
      new AppRepositoryScope {

        contentResolverWrapper.fetch(
          uri = mockUri,
          projection = allFields,
          where = s"$packageName = ?",
          whereParams = Seq(testNonExistingPackageName))(
          f = getEntityFromCursor(appEntityFromCursor)) returns None

        val result = appRepository.fetchAppByPackage(packageName = testNonExistingPackageName).value.run
        result shouldEqual Right(None)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope {

        contentResolverWrapper.fetch(
          uri = mockUri,
          projection = allFields,
          where = s"$packageName = ?",
          whereParams = Seq(testPackageName))(
          f = getEntityFromCursor(appEntityFromCursor)) throws contentResolverException

        val result = appRepository.fetchAppByPackage(packageName = testPackageName).value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "fetchAppsByCategory" should {
    "return a sequence of Apps when a existent category is given" in
      new AppRepositoryScope {

        contentResolverWrapper.fetchAll(
          uri = mockUri,
          projection = allFields,
          where = s"$category = ?",
          whereParams = Seq(testCategory),
          orderBy = "")(
          f = getListFromCursor(appEntityFromCursor)) returns appEntitySeq

        val result = appRepository.fetchAppsByCategory(category = testCategory).value.run
        result shouldEqual Right(appSeq)
      }

    "return an empty sequence when a non-existent category is given" in
      new AppRepositoryScope {

        contentResolverWrapper.fetchAll(
          uri = mockUri,
          projection = allFields,
          where = s"$category = ?",
          whereParams = Seq(testNonExistingCategory),
          orderBy = "")(
          f = getListFromCursor(appEntityFromCursor)) returns Seq.empty

        val result = appRepository.fetchAppsByCategory(category = testNonExistingCategory).value.run
        result shouldEqual Right(Seq.empty)

      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope {

        contentResolverWrapper.fetchAll(
          uri = mockUri,
          projection = allFields,
          where = s"$category = ?",
          whereParams = Seq(testCategory),
          orderBy = "")(
          f = getListFromCursor(appEntityFromCursor)) throws contentResolverException

        val result = appRepository.fetchAppsByCategory(category = testCategory).value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "updateApp" should {

    "return a successful response when the app is updated" in
      new AppRepositoryScope {

        contentResolverWrapper.updateById(any, any, any, any) returns 1
        val result = appRepository.updateApp(app = app).value.run
        result shouldEqual Right(1)
      }

    "return a RepositoryException when a exception is thrown" in
      new AppRepositoryScope {

        contentResolverWrapper.updateById(any, any, any, any) throws contentResolverException
        val result = appRepository.updateApp(app = app).value.run
        result must beAnInstanceOf[Left[RepositoryException, _]]
      }
  }

  "getEntityFromCursor" should {

    "return None when an empty cursor is given" in
      new EmptyAppMockCursor
        with AppRepositoryScope {

        val result = getEntityFromCursor(appEntityFromCursor)(mockCursor)
        result must beNone
      }

    "return an App object when a cursor with data is given" in
      new AppMockCursor
        with AppRepositoryScope {

        val result = getEntityFromCursor(appEntityFromCursor)(mockCursor)

        result must beSome[AppEntity].which { app =>
          app.id shouldEqual appEntity.id
          app.data shouldEqual appEntity.data
        }
      }
  }

  "getListFromCursor" should {

    "return an empty sequence when an empty cursor is given" in
      new EmptyAppMockCursor
        with AppRepositoryScope {

        val result = getListFromCursor(appEntityFromCursor)(mockCursor)
        result should beEmpty
      }

    "return an App sequence when a cursor with data is given" in
      new AppMockCursor
        with AppRepositoryScope {

        val result = getListFromCursor(appEntityFromCursor)(mockCursor)
        result shouldEqual appEntitySeq
      }
  }

}
