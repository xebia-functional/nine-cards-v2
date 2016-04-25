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

  trait UserRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val userRepository = new UserRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]
  }

  trait ValidUserRepositoryResponses
    extends UserRepositoryTestData {

    self: UserRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createUserValues,
      notificationUri = Some(mockUri)) returns testId

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testId,
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testId,
      projection = allFields)(
        f = getEntityFromCursor(userEntityFromCursor)) returns Some(userEntity)

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testNonExistingId,
      projection = allFields)(
        f = getEntityFromCursor(userEntityFromCursor)) returns None

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testId,
      values = createUserValues,
      notificationUri = Some(mockUri)) returns 1
  }

  trait ErrorUserRepositoryResponses
    extends UserRepositoryTestData {

    self: UserRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = createUserValues,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testId,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testId,
      projection = allFields)(
        f = getEntityFromCursor(userEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testId,
      values = createUserValues,
      notificationUri = Some(mockUri)) throws contentResolverException
  }

}

trait UserMockCursor
  extends MockCursor
  with UserRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, userSeq map (_.id), IntDataType),
    (userId, 1, userSeq map (_.data.userId orNull), StringDataType),
    (email, 2, userSeq map (_.data.email orNull), StringDataType),
    (sessionToken, 3, userSeq map (_.data.sessionToken orNull), StringDataType),
    (installationId, 4, userSeq map (_.data.installationId orNull), StringDataType),
    (deviceToken, 5, userSeq map (_.data.deviceToken orNull), StringDataType),
    (androidToken, 6, userSeq map (_.data.androidToken orNull), StringDataType),
    (name, 7, userSeq map (_.data.name orNull), StringDataType),
    (avatar, 8, userSeq map (_.data.avatar orNull), StringDataType),
    (cover, 9, userSeq map (_.data.cover orNull), StringDataType))

  prepareCursor[User](userSeq.size, cursorData)
}

trait EmptyUserMockCursor
  extends MockCursor
  with UserRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (userId, 1, Seq.empty, StringDataType),
    (email, 2, Seq.empty, StringDataType),
    (sessionToken, 3, Seq.empty, StringDataType),
    (installationId, 4, Seq.empty, StringDataType),
    (deviceToken, 5, Seq.empty, StringDataType),
    (androidToken, 6, Seq.empty, StringDataType),
    (name, 7, Seq.empty, StringDataType),
    (avatar, 8, Seq.empty, StringDataType),
    (cover, 9, Seq.empty, StringDataType))

  prepareCursor[User](0, cursorData)
}

class UserRepositorySpec
  extends UserRepositorySpecification {

  "UserRepositoryClient component" should {

    "addUser" should {

      "return a User object with a valid request" in
        new UserRepositoryScope
          with ValidUserRepositoryResponses {

          val result = userRepository.addUser(data = createUserData).run.run

          result must beLike {
            case Answer(userResponse) =>
              userResponse.id shouldEqual testId
              userResponse.data.userId shouldEqual testUserIdOption
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope
          with ErrorUserRepositoryResponses {

          val result = userRepository.addUser(data = createUserData).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteUsers" should {

      "return a successful result when all the users are deleted" in
        new UserRepositoryScope
          with ValidUserRepositoryResponses {

          val result = userRepository.deleteUsers().run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope
          with ErrorUserRepositoryResponses {

          val result = userRepository.deleteUsers().run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteUser" should {

      "return a successful result when a valid user id is given" in
        new UserRepositoryScope
          with ValidUserRepositoryResponses {

          val result = userRepository.deleteUser(user).run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope
          with ErrorUserRepositoryResponses {

          val result = userRepository.deleteUser(user).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "findUserById" should {

      "return a User object when a existing id is given" in
        new UserRepositoryScope
          with ValidUserRepositoryResponses {

          val result = userRepository.findUserById(id = testId).run.run

          result must beLike {
            case Answer(maybeUser) =>
              maybeUser must beSome[User].which { user =>
                user.id shouldEqual testId
                user.data.userId shouldEqual testUserIdOption
              }
          }
        }

      "return None when a non-existing id is given" in
        new UserRepositoryScope
          with ValidUserRepositoryResponses {
          val result = userRepository.findUserById(id = testNonExistingId).run.run

          result must beLike {
            case Answer(maybeUser) =>
              maybeUser must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope
          with ErrorUserRepositoryResponses {

          val result = userRepository.findUserById(id = testId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "updateUser" should {

      "return a successful result when the user is updated" in
        new UserRepositoryScope
          with ValidUserRepositoryResponses {

          val result = userRepository.updateUser(item = user).run.run

          result must beLike {
            case Answer(updated) =>
              updated shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new UserRepositoryScope
          with ErrorUserRepositoryResponses {

          val result = userRepository.updateUser(item = user).run.run

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
