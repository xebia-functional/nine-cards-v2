package com.fortysevendeg.repository.dockapp

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.DockApp
import com.fortysevendeg.ninecardslauncher.repository.provider.DockAppEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories.DockAppRepository
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

trait DockAppRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait DockAppRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val dockAppRepository = new DockAppRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]
  }

  trait ValidDockAppRepositoryResponses
    extends DockAppRepositoryTestData {

    self: DockAppRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(mockUri, createDockAppValues) returns testId

    contentResolverWrapper.delete(mockUri, where = "") returns 1

    contentResolverWrapper.deleteById(mockUri, testId) returns 1

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testId,
      projection = allFields)(
        f = getEntityFromCursor(dockAppEntityFromCursor)) returns Some(dockAppEntity)

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testNonExistingId,
      projection = allFields)(
        f = getEntityFromCursor(dockAppEntityFromCursor)) returns None

    contentResolverWrapper.updateById(mockUri, testId, createDockAppValues) returns 1
  }

  trait ErrorDockAppRepositoryResponses
    extends DockAppRepositoryTestData {

    self: DockAppRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(mockUri, createDockAppValues) throws contentResolverException

    contentResolverWrapper.delete(mockUri, where = "") throws contentResolverException

    contentResolverWrapper.deleteById(mockUri, testId) throws contentResolverException

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testId,
      projection = allFields)(
        f = getEntityFromCursor(dockAppEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(mockUri, testId, createDockAppValues) throws contentResolverException
  }

}

trait DockAppMockCursor
  extends MockCursor
  with DockAppRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, dockAppSeq map (_.id), IntDataType),
    (name, 1, dockAppSeq map (_.data.name), StringDataType),
    (dockType, 2, dockAppSeq map (_.data.dockType), StringDataType),
    (intent, 4, dockAppSeq map (_.data.intent), StringDataType),
    (imagePath, 5, dockAppSeq map (_.data.imagePath), StringDataType),
    (position, 6, dockAppSeq map (_.data.position), IntDataType))

  prepareCursor[DockApp](dockAppSeq.size, cursorData)
}

trait EmptyDockAppMockCursor
  extends MockCursor
  with DockAppRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (name, 1, Seq.empty, StringDataType),
    (dockType, 2, Seq.empty, StringDataType),
    (intent, 4, Seq.empty, StringDataType),
    (imagePath, 5, Seq.empty, StringDataType),
    (position, 6, Seq.empty, IntDataType))

  prepareCursor[DockApp](0, cursorData)
}

class DockAppRepositorySpec
  extends DockAppRepositorySpecification {

  "DockAppRepositoryClient component" should {

    "addDockApp" should {

      "return a DockApp object with a valid request" in
        new DockAppRepositoryScope
          with ValidDockAppRepositoryResponses {

          val result = dockAppRepository.addDockApp(data = createDockAppData).run.run

          result must beLike {
            case Answer(dockAppResult) =>
              dockAppResult.id shouldEqual testId
              dockAppResult.data.name shouldEqual testName
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new DockAppRepositoryScope
          with ErrorDockAppRepositoryResponses {

          val result = dockAppRepository.addDockApp(data = createDockAppData).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteDockApps" should {

      "return a successful result when all the dockApps are deleted" in
        new DockAppRepositoryScope
          with ValidDockAppRepositoryResponses {

          val result = dockAppRepository.deleteDockApps().run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new DockAppRepositoryScope
          with ErrorDockAppRepositoryResponses {

          val result = dockAppRepository.deleteDockApps().run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteDockApp" should {

      "return a successful result when a valid dockApp id is given" in
        new DockAppRepositoryScope
          with ValidDockAppRepositoryResponses {

          val result = dockAppRepository.deleteDockApp(dockApp).run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new DockAppRepositoryScope
          with ErrorDockAppRepositoryResponses {

          val result = dockAppRepository.deleteDockApp(dockApp).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "findDockAppById" should {

      "return a DockApp object when a existing id is given" in
        new DockAppRepositoryScope
          with ValidDockAppRepositoryResponses {

          val result = dockAppRepository.findDockAppById(id = testId).run.run

          result must beLike {
            case Answer(maybeDockApp) =>
              maybeDockApp must beSome[DockApp].which { dockApp =>
                dockApp.id shouldEqual testId
                dockApp.data.name shouldEqual testName
              }
          }
        }

      "return None when a non-existing id is given" in
        new DockAppRepositoryScope
          with ValidDockAppRepositoryResponses {
          val result = dockAppRepository.findDockAppById(id = testNonExistingId).run.run

          result must beLike {
            case Answer(maybeDockApp) =>
              maybeDockApp must beNone
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new DockAppRepositoryScope
          with ErrorDockAppRepositoryResponses {

          val result = dockAppRepository.findDockAppById(id = testId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "updateDockApp" should {

      "return a successful result when the dockApp is updated" in
        new DockAppRepositoryScope
          with ValidDockAppRepositoryResponses {

          val result = dockAppRepository.updateDockApp(item = dockApp).run.run

          result must beLike {
            case Answer(updated) =>
              updated shouldEqual 1
          }
        }

      "return a NineCardsException when a exception is thrown" in
        new DockAppRepositoryScope
          with ErrorDockAppRepositoryResponses {

          val result = dockAppRepository.updateDockApp(item = dockApp).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "getEntityFromCursor" should {

      "return None when an empty cursor is given" in
        new EmptyDockAppMockCursor
          with Scope {

          val result = getEntityFromCursor(dockAppEntityFromCursor)(mockCursor)

          result must beNone
        }

      "return a DockApp object when a cursor with data is given" in
        new DockAppMockCursor
          with Scope {

          val result = getEntityFromCursor(dockAppEntityFromCursor)(mockCursor)

          result must beSome[DockAppEntity].which { dockApp =>
            dockApp.id shouldEqual dockAppEntity.id
            dockApp.data shouldEqual dockAppEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyDockAppMockCursor
          with Scope {

          val result = getListFromCursor(dockAppEntityFromCursor)(mockCursor)

          result should beEmpty
        }

      "return a DockApp sequence when a cursor with data is given" in
        new DockAppMockCursor
          with Scope {

          val result = getListFromCursor(dockAppEntityFromCursor)(mockCursor)

          result shouldEqual dockAppEntitySeq
        }
    }
  }
}
