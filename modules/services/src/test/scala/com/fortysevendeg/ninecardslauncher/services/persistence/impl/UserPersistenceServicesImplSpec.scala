package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.UserTestData
import cards.nine.commons.test.data.UserValues._
import cards.nine.models.User
import cards.nine.repository.RepositoryException
import cards.nine.services.persistence.data.UserPersistenceServicesData
import cats.syntax.either._
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

trait UserPersistenceServicesDataSpecification
  extends Specification
  with DisjunctionMatchers {


  trait UserPersistenceServicesScope
    extends RepositoryServicesScope
    with UserTestData
    with UserPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class UserPersistenceServicesImplSpec extends UserPersistenceServicesDataSpecification {

  "addUser" should {

    "return a User value for a valid request" in new UserPersistenceServicesScope {

      mockUserRepository.addUser(any) returns TaskService(Task(Either.right(repoUser)))
      val result = persistenceServices.addUser(userData).value.run

      result must beLike {
        case Right(user) => user.id shouldEqual userId
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.addUser(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addUser(userData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAllUsers" should {

    "return the number of elements deleted for a valid request" in new UserPersistenceServicesScope {

      mockUserRepository.deleteUsers() returns TaskService(Task(Either.right(deletedUsers)))
      val result = persistenceServices.deleteAllUsers().value.run
      result shouldEqual Right(deletedUsers)
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.deleteUsers() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAllUsers().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteUser" should {

    "return the number of elements deleted for a valid request" in new UserPersistenceServicesScope {
      mockUserRepository.deleteUser(any) returns TaskService(Task(Either.right(deletedUser)))
      val result = persistenceServices.deleteUser(user).value.run
      result shouldEqual Right(deletedUser)
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.deleteUser(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteUser(user).value.run
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
      val result = persistenceServices.findUserById(userId).value.run

      result must beLike {
        case Right(maybeUser) =>
          maybeUser must beSome[User].which { user =>
            user.id shouldEqual userId
          }
      }
    }

    "return None when a non-existent id is given" in new UserPersistenceServicesScope {

      mockUserRepository.findUserById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findUserById(nonExistentUserId).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.findUserById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findUserById(userId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateUser" should {

    "return the number of elements updated for a valid request" in new UserPersistenceServicesScope {

      mockUserRepository.updateUser(any) returns TaskService(Task(Either.right(updatedUser)))
      val result = persistenceServices.updateUser(user).value.run
      result shouldEqual Right(updatedUser)
    }

    "return a PersistenceServiceException if the service throws a exception" in new UserPersistenceServicesScope {

      mockUserRepository.updateUser(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateUser(user).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

}
