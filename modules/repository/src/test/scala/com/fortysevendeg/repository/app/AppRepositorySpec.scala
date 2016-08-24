package com.fortysevendeg.repository.app

import android.net.Uri
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.repository.model.App
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity._
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

  trait ValidAppRepositoryResponses {

    self: AppRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createAppValues,
      notificationUris = Seq(mockUri)) returns testAppId

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testAppId,
      notificationUris = Seq(mockUri)) returns 1

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUris = Seq(mockUri)) returns 1

    contentResolverWrapper.delete(
      uri = mockUri,
      where = s"$packageName = ?",
      whereParams = Seq(testPackageName),
      notificationUris = Seq(mockUri)) returns 1

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testAppId,
      projection = allFields)(
        f = getEntityFromCursor(appEntityFromCursor)) returns Some(appEntity)

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testNonExistingAppId,
      projection = allFields)(
        f = getEntityFromCursor(appEntityFromCursor)) returns None

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      orderBy = "")(
        f = getListFromCursor(appEntityFromCursor)) returns appEntitySeq

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$category = ?",
      whereParams = Seq(testCategory),
      orderBy = "")(
      f = getListFromCursor(appEntityFromCursor)) returns appEntitySeq

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$category = ?",
      whereParams = Seq(testNonExistingCategory),
      orderBy = "")(
      f = getListFromCursor(appEntityFromCursor)) returns Seq.empty

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$packageName = ?",
      whereParams = Seq(testPackageName))(
        f = getEntityFromCursor(appEntityFromCursor)) returns Some(appEntity)

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$packageName = ?",
      whereParams = Seq(testNonExistingPackageName))(
        f = getEntityFromCursor(appEntityFromCursor)) returns None

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testAppId,
      values = createAppValues,
      notificationUris = Seq(mockUri)) returns 1
  }

  trait ErrorAppRepositoryResponses {

    self: AppRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createAppValues,
      notificationUris = Seq(mockUri)) throws contentResolverException

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testAppId,
      notificationUris = Seq(mockUri)) throws contentResolverException

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUris = Seq(mockUri)) throws contentResolverException

    contentResolverWrapper.delete(
      uri = mockUri,
      where = s"$packageName = ?",
      whereParams = Seq(testPackageName),
      notificationUris = Seq(mockUri)) throws contentResolverException

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testAppId,
      projection = allFields)(
        f = getEntityFromCursor(appEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      orderBy = "")(
        f = getListFromCursor(appEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$category = ?",
      whereParams = Seq(testCategory),
      orderBy = "")(
      f = getListFromCursor(appEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$packageName = ?",
      whereParams = Seq(testPackageName))(
        f = getEntityFromCursor(appEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testAppId,
      values = createAppValues,
      notificationUris = Seq(mockUri)) throws contentResolverException
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
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.addApp(data = createAppData).value.run

          result must beLike {
            case Xor.Right(appResult) =>
              appResult.id shouldEqual testAppId
              appResult.data.packageName shouldEqual testPackageName
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.addApp(data = createAppData).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "fetchAlphabeticalAppsCounter" should {

      "return a sequence of DataCounter sort alphabetically" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.fetchAlphabeticalAppsCounter.value.run

          result must beLike {
            case Xor.Right(counters) =>
              counters shouldEqual appsDataCounters
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorCounterAppRepositoryResponses {

          val result = appRepositoryException.fetchAlphabeticalAppsCounter.value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
            }
          }
        }
    }

    "fetchCategorizedAppsCounter" should {

      "return a sequence of DataCounter sort by category" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.fetchCategorizedAppsCounter.value.run

          result must beLike {
            case Xor.Right(counters) =>
              counters shouldEqual categoryDataCounters
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorCounterAppRepositoryResponses {

          val result = appRepositoryException.fetchCategorizedAppsCounter.value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "fetchInstallationDateAppsCounter" should {

      "return a sequence of DataCounter sort by installation date" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.fetchInstallationDateAppsCounter.value.run

          result must beLike {
            case Xor.Right(counters) =>
              counters shouldEqual installationDateDateCounters
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorCounterAppRepositoryResponses {

          val result = appRepositoryException.fetchInstallationDateAppsCounter.value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "deleteApps" should {

      "return a successful response when all the apps are deleted" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.deleteApps().value.run

          result must beLike {
            case Xor.Right(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.deleteApps().value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "deleteApp" should {

      "return a successful response when a valid app id is given" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.deleteApp(app = app).value.run

          result must beLike {
            case Xor.Right(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.deleteApp(app = app).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "deleteAppByPackage" should {

      "return a successful response when a valid package name is given" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.deleteAppByPackage(packageName = testPackageName).value.run

          result must beLike {
            case Xor.Right(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.deleteAppByPackage(packageName = testPackageName).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "fetchApps" should {

      "return all the apps stored in the database" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.fetchApps().value.run

          result must beLike {
            case Xor.Right(apps) =>
              apps shouldEqual appSeq
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.fetchApps().value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "findAppById" should {

      "return an App object when a existent id is given" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.findAppById(id = testAppId).value.run

          result must beLike {
            case Xor.Right(maybeApp) =>
              maybeApp must beSome[App].which { app =>
                app.id shouldEqual testAppId
                app.data.packageName shouldEqual testPackageName
              }
          }
        }

      "return None when a non-existent id is given" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.findAppById(id = testNonExistingAppId).value.run

          result must beLike {
            case Xor.Right(maybeApp) =>
              maybeApp must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.findAppById(id = testAppId).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "fetchAppByPackage" should {
      "return an App object when a existent package name is given" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.fetchAppByPackage(packageName = testPackageName).value.run

          result must beLike {
            case Xor.Right(maybeApp) =>
              maybeApp must beSome[App].which { app =>
                app.id shouldEqual testAppId
                app.data.packageName shouldEqual testPackageName
              }
          }
        }

      "return None when a non-existent package name is given" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.fetchAppByPackage(packageName = testNonExistingPackageName).value.run

          result must beLike {
            case Xor.Right(maybeApp) =>
              maybeApp must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.fetchAppByPackage(packageName = testPackageName).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "fetchAppsByCategory" should {
      "return a sequence of Apps when a existent category is given" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.fetchAppsByCategory(category = testCategory).value.run

          result must beLike {
            case Xor.Right(apps) =>
              apps shouldEqual appSeq
          }
        }

      "return an empty sequence when a non-existent category is given" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.fetchAppsByCategory(category = testNonExistingCategory).value.run

          result must beLike {
            case Xor.Right(apps) =>
              apps shouldEqual Seq.empty
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.fetchAppsByCategory(category = testCategory).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
        }
    }

    "updateApp" should {

      "return a successful response when the app is updated" in
        new AppRepositoryScope
          with ValidAppRepositoryResponses {

          val result = appRepository.updateApp(app = app).value.run

          result must beLike {
            case Xor.Right(updated) =>
              updated shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new AppRepositoryScope
          with ErrorAppRepositoryResponses {

          val result = appRepository.updateApp(app = app).value.run

          result must beLike {
            case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual contentResolverException)
          }
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
