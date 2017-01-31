/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.repository.dockapp

import android.net.Uri
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.repository.{IntDataType, MockCursor, StringDataType}
import cards.nine.repository.RepositoryException
import cards.nine.repository.model.DockApp
import cards.nine.repository.provider.DockAppEntity._
import cards.nine.repository.provider._
import cards.nine.repository.repositories.DockAppRepository
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait DockAppRepositorySpecification extends Specification with DisjunctionMatchers with Mockito {

  trait DockAppRepositoryScope extends Scope with DockAppRepositoryTestData {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val dockAppRepository = new DockAppRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]

    uriCreator.parse(any) returns mockUri

    val contentResolverException = new RuntimeException("Irrelevant message")
  }

}

trait DockAppMockCursor extends MockCursor with DockAppRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, dockAppSeq map (_.id), IntDataType),
    (name, 1, dockAppSeq map (_.data.name), StringDataType),
    (dockType, 2, dockAppSeq map (_.data.dockType), StringDataType),
    (intent, 4, dockAppSeq map (_.data.intent), StringDataType),
    (imagePath, 5, dockAppSeq map (_.data.imagePath), StringDataType),
    (position, 6, dockAppSeq map (_.data.position), IntDataType))

  prepareCursor[DockApp](dockAppSeq.size, cursorData)
}

trait EmptyDockAppMockCursor extends MockCursor with DockAppRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (name, 1, Seq.empty, StringDataType),
    (dockType, 2, Seq.empty, StringDataType),
    (intent, 4, Seq.empty, StringDataType),
    (imagePath, 5, Seq.empty, StringDataType),
    (position, 6, Seq.empty, IntDataType))

  prepareCursor[DockApp](0, cursorData)
}

class DockAppRepositorySpec extends DockAppRepositorySpecification {

  "DockAppRepositoryClient component" should {

    "addDockApp" should {

      "return a DockApp object with a valid request" in
        new DockAppRepositoryScope {

          contentResolverWrapper.insert(any, any, any) returns testId
          val result = dockAppRepository.addDockApp(data = createDockAppData).value.run

          result must beLike {
            case Right(dockAppResult) =>
              dockAppResult.id shouldEqual testId
              dockAppResult.data.name shouldEqual testName
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new DockAppRepositoryScope {

          contentResolverWrapper.insert(any, any, any) throws contentResolverException
          val result = dockAppRepository.addDockApp(data = createDockAppData).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "addDockApps" should {

      "return a sequence of DockApp objects with a valid request" in
        new DockAppRepositoryScope {

          contentResolverWrapper.inserts(any, any, any, any) returns dockAppIdSeq
          val result = dockAppRepository.addDockApps(datas = dockAppDataSeq).value.run

          result must beLike {
            case Right(dockApps) =>
              dockApps map (_.id) shouldEqual dockAppIdSeq
              dockApps map (_.data.name) shouldEqual (dockAppDataSeq map (_.name))
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new DockAppRepositoryScope {

          contentResolverWrapper.inserts(any, any, any, any) throws contentResolverException
          val result = dockAppRepository.addDockApps(datas = dockAppDataSeq).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }

    }

    "deleteDockApps" should {

      "return a successful result when all the dockApps are deleted" in
        new DockAppRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) returns 1
          val result = dockAppRepository.deleteDockApps().value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new DockAppRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) throws contentResolverException
          val result = dockAppRepository.deleteDockApps().value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "deleteDockApp" should {

      "return a successful result when a valid dockApp id is given" in
        new DockAppRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) returns 1
          val result = dockAppRepository.deleteDockApp(dockApp).value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new DockAppRepositoryScope {

          contentResolverWrapper
            .deleteById(any, any, any, any, any) throws contentResolverException
          val result = dockAppRepository.deleteDockApp(dockApp).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "findDockAppById" should {

      "return a DockApp object when a existing id is given" in
        new DockAppRepositoryScope {

          contentResolverWrapper.findById[DockAppEntity](any, any, any, any, any, any)(any) returns Some(
            dockAppEntity)
          val result = dockAppRepository.findDockAppById(id = testId).value.run

          result must beLike {
            case Right(maybeDockApp) =>
              maybeDockApp must beSome[DockApp].which { dockApp =>
                dockApp.id shouldEqual testId
                dockApp.data.name shouldEqual testName
              }
          }
        }

      "return None when a non-existing id is given" in
        new DockAppRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) returns None
          val result = dockAppRepository.findDockAppById(id = testNonExistingId).value.run
          result shouldEqual Right(None)
        }

      "return a RepositoryException when a exception is thrown" in
        new DockAppRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) throws contentResolverException
          val result = dockAppRepository.findDockAppById(id = testId).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "updateDockApp" should {

      "return a successful result when the dockApp is updated" in
        new DockAppRepositoryScope {

          contentResolverWrapper.updateById(any, any, any, any) returns 1
          val result = dockAppRepository.updateDockApp(item = dockApp).value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new DockAppRepositoryScope {

          contentResolverWrapper.updateById(any, any, any, any) throws contentResolverException
          val result = dockAppRepository.updateDockApp(item = dockApp).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "updateDockApps" should {

      "return a successful result when the dockApps are updated" in
        new DockAppRepositoryScope {

          contentResolverWrapper.updateByIds(any, any, any, any) returns Seq(5)
          val result = dockAppRepository.updateDockApps(items = dockAppSeq).value.run
          result shouldEqual Right(Seq(5))
        }

      "return a RepositoryException when a exception is thrown" in
        new DockAppRepositoryScope {

          contentResolverWrapper.updateByIds(any, any, any, any) throws contentResolverException
          val result = dockAppRepository.updateDockApps(items = dockAppSeq).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchDockApps" should {

      "return all DockApps" in
        new DockAppRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = "",
            whereParams = Seq.empty,
            orderBy = s"${DockAppEntity.position} asc")(
            f = getListFromCursor(dockAppEntityFromCursor)) returns dockAppEntitySeq

          val result = dockAppRepository.fetchDockApps().value.run
          result shouldEqual Right(dockAppSeq)
        }

      "return all DockApps that match the where clause" in
        new DockAppRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$position = ?",
            whereParams = Seq(testPosition.toString),
            orderBy = s"${DockAppEntity.position} asc")(
            f = getListFromCursor(dockAppEntityFromCursor)) returns dockAppEntitySeq

          val result = dockAppRepository
            .fetchDockApps(where = s"$position = ?", whereParams = Seq(testPosition.toString))
            .value
            .run
          result shouldEqual Right(dockAppSeq)
        }

      "return a RepositoryException when a exception is thrown" in
        new DockAppRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = "",
            whereParams = Seq.empty,
            orderBy = s"${DockAppEntity.position} asc")(
            f = getListFromCursor(dockAppEntityFromCursor)) throws contentResolverException

          val result = dockAppRepository.fetchDockApps().value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "getEntityFromCursor" should {

      "return None when an empty cursor is given" in
        new EmptyDockAppMockCursor with Scope {

          val result = getEntityFromCursor(dockAppEntityFromCursor)(mockCursor)
          result must beNone
        }

      "return a DockApp object when a cursor with data is given" in
        new DockAppMockCursor with Scope {

          val result = getEntityFromCursor(dockAppEntityFromCursor)(mockCursor)

          result must beSome[DockAppEntity].which { dockApp =>
            dockApp.id shouldEqual dockAppEntity.id
            dockApp.data shouldEqual dockAppEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyDockAppMockCursor with Scope {

          val result = getListFromCursor(dockAppEntityFromCursor)(mockCursor)
          result should beEmpty
        }

      "return a DockApp sequence when a cursor with data is given" in
        new DockAppMockCursor with Scope {

          val result = getListFromCursor(dockAppEntityFromCursor)(mockCursor)
          result shouldEqual dockAppEntitySeq
        }
    }
  }
}
