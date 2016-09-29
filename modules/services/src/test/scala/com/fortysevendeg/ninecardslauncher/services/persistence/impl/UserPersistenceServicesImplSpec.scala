package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.services.persistence.data.UserPersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.User
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import cards.nine.commons.test.TaskServiceTestOps._
import cats.syntax.either._


trait UserPersistenceServicesDataSpecification
  extends Specification
  with DisjunctionMatchers {


  trait UserPersistenceServicesScope
    extends RepositoryServicesScope
      with UserPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class UserPersistenceServicesImplSpec extends UserPersistenceServicesDataSpecification {

  "addUser" should {

    "return a User value for a valid request" in new UserPersistenceServicesScope {

      mockUserRepository.addUser(any) returns TaskService(Task(Either.right(repoUser)))
      val result = persistenceServices.addUser(createAddUserRequest()).value.run

      result must beLike {
        case Right(user) => user.id shouldEqual uId
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.addUser(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addUser(createAddUserRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAllUsers" should {

    "return the number of elements deleted for a valid request" in new UserPersistenceServicesScope {

      mockUserRepository.deleteUsers() returns TaskService(Task(Either.right(items)))
      val result = persistenceServices.deleteAllUsers().value.run
      result shouldEqual Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.deleteUsers() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAllUsers().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteUser" should {

    "return the number of elements deleted for a valid request" in new UserPersistenceServicesScope {
      mockUserRepository.deleteUser(any) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.deleteUser(createDeleteUserRequest(user = user)).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.deleteUser(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteUser(createDeleteUserRequest(user = user)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchUsers" should {

    "return a list of User elements for a valid request" in new UserPersistenceServicesScope {

      mockUserRepository.fetchUsers returns TaskService(Task(Either.right(seqRepoUser)))
      val result = persistenceServices.fetchUsers.value.run

      result must beLike {
        case Right(userItems) => userItems.size shouldEqual seqUser.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.fetchUsers returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchUsers.value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "findUserById" should {

    "return a User for a valid request" in new UserPersistenceServicesScope {

      mockUserRepository.findUserById(any) returns TaskService(Task(Either.right(Option(repoUser))))
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = uId)).value.run

      result must beLike {
        case Right(maybeUser) =>
          maybeUser must beSome[User].which { user =>
            user.id shouldEqual uId
          }
      }
    }

    "return None when a non-existent id is given" in new UserPersistenceServicesScope {

      mockUserRepository.findUserById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = nonExistentUserId)).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.findUserById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = uId)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateUser" should {

    "return the number of elements updated for a valid request" in new UserPersistenceServicesScope {

      mockUserRepository.updateUser(any) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.updateUser(createUpdateUserRequest()).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.updateUser(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateUser(createUpdateUserRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

}
