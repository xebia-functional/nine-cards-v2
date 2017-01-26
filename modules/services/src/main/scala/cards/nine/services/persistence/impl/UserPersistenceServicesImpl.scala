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
