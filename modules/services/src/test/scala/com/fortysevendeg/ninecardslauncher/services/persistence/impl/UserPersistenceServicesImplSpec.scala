package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.services.persistence.data.UserPersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

import scalaz.concurrent.Task

trait UserPersistenceServicesDataSpecification
  extends Specification
    with DisjunctionMatchers {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with UserPersistenceServicesData {

    mockUserRepository.addUser(repoUserData) returns CatsService(Task(Xor.right(repoUser)))

    mockUserRepository.deleteUsers() returns CatsService(Task(Xor.right(items)))

    mockUserRepository.deleteUser(repoUser) returns CatsService(Task(Xor.right(item)))

    mockUserRepository.fetchUsers returns CatsService(Task(Xor.right(seqRepoUser)))

    mockUserRepository.findUserById(uId) returns CatsService(Task(Xor.right(Option(repoUser))))

    mockUserRepository.findUserById(nonExistentUserId) returns CatsService(Task(Xor.right(None)))

    mockUserRepository.updateUser(repoUser) returns CatsService(Task(Xor.right(item)))
  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with UserPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockUserRepository.addUser(repoUserData) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.deleteUsers() returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.deleteUser(repoUser) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.fetchUsers returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.findUserById(uId) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.findUserById(nonExistentUserId) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.updateUser(repoUser) returns CatsService(Task(Xor.left(exception)))
  }

}

class UserPersistenceServicesImplSpec extends UserPersistenceServicesDataSpecification {

  "addUser" should {

    "return a User value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addUser(createAddUserRequest()).value.run

      result must beLike {
        case Xor.Right(user) => user.id shouldEqual uId
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addUser(createAddUserRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllUsers" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllUsers().value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllUsers().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteUser" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteUser(createDeleteUserRequest(user = user)).value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteUser(createDeleteUserRequest(user = user)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchUsers" should {

    "return a list of User elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchUsers.value.run

      result must beLike {
        case Xor.Right(userItems) => userItems.size shouldEqual seqUser.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchUsers.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findUserById" should {

    "return a User for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = uId)).value.run

      result must beLike {
        case Xor.Right(maybeUser) =>
          maybeUser must beSome[User].which { user =>
            user.id shouldEqual uId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = nonExistentUserId)).value.run

      result must beLike {
        case Xor.Right(maybeUser) => maybeUser must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = uId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateUser" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateUser(createUpdateUserRequest()).value.run

      result must beLike {
        case Xor.Right(updated) => updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateUser(createUpdateUserRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

}
