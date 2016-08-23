package com.fortysevendeg.repository.user

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.User
import com.fortysevendeg.ninecardslauncher.repository.provider.UserEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories.UserRepository
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

import scala.language.postfixOps

trait UserRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  val contentResolverException = new RuntimeException("Irrelevant message")

  trait UserRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val userRepository = new UserRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]

    uriCreator.parse(any) returns mockUri
  }

}

trait UserMockCursor
  extends MockCursor
  with UserRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, userSeq map (_.id), IntDataType),
    (email, 1, userSeq map (_.data.email orNull), StringDataType),
    (apiKey, 2, userSeq map (_.data.apiKey orNull), StringDataType),
    (sessionToken, 3, userSeq map (_.data.sessionToken orNull), StringDataType),
    (deviceToken, 5, userSeq map (_.data.deviceToken orNull), StringDataType),
    (marketToken, 6, userSeq map (_.data.marketToken orNull), StringDataType),
    (name, 7, userSeq map (_.data.name orNull), StringDataType),
    (avatar, 8, userSeq map (_.data.avatar orNull), StringDataType),
    (cover, 9, userSeq map (_.data.cover orNull), StringDataType),
    (deviceName, 10, userSeq map (_.data.deviceName orNull), StringDataType),
    (deviceCloudId, 11, userSeq map (_.data.deviceCloudId orNull), StringDataType))

  prepareCursor[User](userSeq.size, cursorData)
}

trait EmptyUserMockCursor
  extends MockCursor
  with UserRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (email, 1, Seq.empty, StringDataType),
    (apiKey, 2, Seq.empty, StringDataType),
    (sessionToken, 3, Seq.empty, StringDataType),
    (deviceToken, 4, Seq.empty, StringDataType),
    (marketToken, 5, Seq.empty, StringDataType),
    (name, 6, Seq.empty, StringDataType),
    (avatar, 7, Seq.empty, StringDataType),
    (cover, 8, Seq.empty, StringDataType),
    (deviceName, 9, Seq.empty, StringDataType),
    (deviceCloudId, 10, Seq.empty, StringDataType))

  prepareCursor[User](0, cursorData)
}

class UserRepositorySpec
  extends UserRepositorySpecification
  with UserRepositoryTestData {

  "UserRepositoryClient component" should {

    "addUser" should {

      "return a User object with a valid request" in
        new UserRepositoryScope {

          contentResolverWrapper.insert(any, any, any) returns testId

          val result = userRepository.addUser(data = createUserData).run.run

          result must beLike {
            case Answer(userResponse) =>
              userResponse.id shouldEqual testId
              userResponse.data.email should beSome(testEmail)
          }

          there was one(contentResolverWrapper).insert(mockUri, createUserValues, Seq(mockUri))
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope {

          contentResolverWrapper.insert(any, any, any) throws contentResolverException

          val result = userRepository.addUser(data = createUserData).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }

          there was one(contentResolverWrapper).insert(mockUri, createUserValues, Seq(mockUri))
        }
    }

    "deleteUsers" should {

      "return a successful result when all the users are deleted" in
        new UserRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) returns 1

          val result = userRepository.deleteUsers().run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }

          there was one(contentResolverWrapper).delete(
            uri = mockUri,
            where = "",
            notificationUris = Seq(mockUri))
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) throws contentResolverException thenReturn 1

          val result = userRepository.deleteUsers().run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }

          there was one(contentResolverWrapper).delete(
            uri = mockUri,
            where = "",
            notificationUris = Seq(mockUri))
        }
    }

    "deleteUser" should {

      "return a successful result when a valid user id is given" in
        new UserRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) returns 1

          val result = userRepository.deleteUser(user).run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }

          there was one(contentResolverWrapper).deleteById(
            uri = mockUri,
            id = testId,
            notificationUris = Seq(mockUri))
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) throws contentResolverException thenReturn 1

          val result = userRepository.deleteUser(user).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }

          there were one(contentResolverWrapper).deleteById(
            uri = mockUri,
            id = testId,
            notificationUris = Seq(mockUri))
        }
    }

    "findUserById" should {

      "return a User object when a existing id is given" in
        new UserRepositoryScope {

          contentResolverWrapper.findById[UserEntity](any, any, any, any, any, any)(any) returns Some(userEntity)

          val result = userRepository.findUserById(id = testId).run.run

          result must beLike {
            case Answer(maybeUser) =>
              maybeUser must beSome[User].which { user =>
                user.id shouldEqual testId
                user.data.email should beSome(testEmail)
              }
          }

          there was one(contentResolverWrapper).findById(
            uri = mockUri,
            id = testId,
            projection = allFields)(
            f = getEntityFromCursor(userEntityFromCursor))
        }

      "return None when a non-existing id is given" in
        new UserRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) returns None

          val result = userRepository.findUserById(id = testNonExistingId).run.run

          result must beLike {
            case Answer(maybeUser) =>
              maybeUser must beNone
          }

          there was one(contentResolverWrapper).findById(
            uri = mockUri,
            id = testNonExistingId,
            projection = allFields)(
            f = getEntityFromCursor(userEntityFromCursor))
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) throws contentResolverException thenReturn None

          val result = userRepository.findUserById(id = testId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }

          there was one(contentResolverWrapper).findById(
            uri = mockUri,
            id = testId,
            projection = allFields)(
            f = getEntityFromCursor(userEntityFromCursor))
        }
    }

    "updateUser" should {

      "return a successful result when the user is updated" in
        new UserRepositoryScope {

          contentResolverWrapper.updateById(any, any, any, any) returns 1

          val result = userRepository.updateUser(item = user).run.run

          result must beLike {
            case Answer(updated) =>
              updated shouldEqual 1
          }

          there was one(contentResolverWrapper).updateById(
            uri = mockUri,
            id = testId,
            values = createUserValues,
            notificationUris = Seq(mockUri))
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope {

          contentResolverWrapper.updateById(any, any, any, any) throws contentResolverException thenReturn 1

          val result = userRepository.updateUser(item = user).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }

          there was one(contentResolverWrapper).updateById(
            uri = mockUri,
            id = testId,
            values = createUserValues,
            notificationUris = Seq(mockUri))
        }
    }

    "getEntityFromCursor" should {

      "return None when an empty cursor is given" in
        new EmptyUserMockCursor
          with Scope {

          val result = getEntityFromCursor(userEntityFromCursor)(mockCursor)

          result must beNone
        }

      "return a User object when a cursor with data is given" in
        new UserMockCursor
          with Scope {

          val result = getEntityFromCursor(userEntityFromCursor)(mockCursor)

          result must beSome[UserEntity].which { user =>
            user.id shouldEqual userEntity.id
            user.data shouldEqual userEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyUserMockCursor
          with Scope {

          val result = getListFromCursor(userEntityFromCursor)(mockCursor)

          result should beEmpty
        }

      "return a User sequence when a cursor with data is given" in
        new UserMockCursor
          with Scope {

          val result = getListFromCursor(userEntityFromCursor)(mockCursor)

          result shouldEqual userEntitySeq
        }
    }
  }
}
