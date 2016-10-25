package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService._
import cards.nine.commons.NineCardExtensions._
import cards.nine.models.{User, UserData}
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions

trait UserPersistenceServicesImpl extends PersistenceServices {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addUser(user: UserData) =
    (for {
      user <- userRepository.addUser(toRepositoryUserData(user))
    } yield toUser(user)).resolve[PersistenceServiceException]

  def deleteAllUsers() =
    (for {
      deleted <- userRepository.deleteUsers()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteUser(user: User) =
    (for {
      deleted <- userRepository.deleteUser(toRepositoryUser(user))
    } yield deleted).resolve[PersistenceServiceException]

  def fetchUsers =
    (for {
      userItems <- userRepository.fetchUsers
    } yield userItems map toUser).resolve[PersistenceServiceException]

  def findUserById(userId: Int) =
    (for {
      maybeUser <- userRepository.findUserById(userId)
    } yield maybeUser map toUser).resolve[PersistenceServiceException]

  def updateUser(user: User) =
    (for {
      updated <- userRepository.updateUser(toRepositoryUser(user))
    } yield updated).resolve[PersistenceServiceException]
}
