package com.fortysevendeg.ninecardslauncher.process.user.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait UserProcessSpecification
  extends Specification
  with Mockito {

  trait UserProcessScope
    extends Scope
    with UserProcessData {

    val mockContextSupport = mock[ContextSupport]

    val mockApiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]

    val userProcess = new UserProcessImpl(mockApiServices, mockPersistenceServices)

  }

}

class UserProcessImplSpec
  extends UserProcessSpecification {

  "Sign In in UserProcess" should {

    "returns a UserException if there is no active user" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns None

        val result = userProcess.signIn(email, marketToken, emailTokenId)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).login(any, any, any)
        there was no(mockPersistenceServices).findUserById(any)
        there was no(mockPersistenceServices).updateUser(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "returns a UserException if the user doesn't exists in the database" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.getAndroidId(any) returns CatsService(Task(Xor.right(deviceId)))
        mockApiServices.login(any, any, any) returns CatsService(Task(Xor.right(loginResponse)))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(None)))

        val result = userProcess.signIn(email, marketToken, emailTokenId)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).getAndroidId(mockContextSupport)
        there was one(mockApiServices).login(email, deviceId, emailTokenId)
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was no(mockPersistenceServices).updateUser(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "returns Unit when all services work fine" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.getAndroidId(any) returns CatsService(Task(Xor.right(deviceId)))
        mockApiServices.login(any, any, any) returns CatsService(Task(Xor.right(loginResponse)))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(persistenceUser))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.signIn(email, marketToken, emailTokenId)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).getAndroidId(mockContextSupport)
        there was one(mockApiServices).login(email, deviceId, emailTokenId)
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).updateUser(UpdateUserRequest(
          id = persistenceUser.id,
          email = persistenceUser.email,
          apiKey = Some(loginResponse.apiKey),
          sessionToken = Some(loginResponse.sessionToken),
          deviceToken = persistenceUser.deviceToken,
          marketToken = persistenceUser.marketToken,
          name = persistenceUser.name,
          avatar = persistenceUser.avatar,
          cover = persistenceUser.cover,
          deviceName = persistenceUser.deviceName,
          deviceCloudId = persistenceUser.deviceCloudId))

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

  }

  "Register in UserProcess" should {

    "register as active the first user return by fetchUsers service when there is no active user id" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns None
        mockPersistenceServices.fetchUsers returns CatsService(Task(Xor.right(Seq(persistenceUser, anotherUser))))

        val result = userProcess.register(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).fetchUsers
        there was one(mockContextSupport).setActiveUserId(persistenceUser.id)
        there was no(mockPersistenceServices).findUserById(any)
        there was no(mockPersistenceServices).addUser(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "register as active a new user when fetchUsers return an empty seq and there is no active user id" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns None
        mockPersistenceServices.fetchUsers returns CatsService(Task(Xor.right(Seq.empty)))
        mockPersistenceServices.addUser(any) returns CatsService(Task(Xor.right(persistenceUser)))

        val result = userProcess.register(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).fetchUsers
        there was one(mockContextSupport).setActiveUserId(persistenceUser.id)
        there was one(mockPersistenceServices).addUser(emptyAddUserRequest)
        there was no(mockPersistenceServices).findUserById(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "not register an active user when there is one active user id and it exists in the database" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(persistenceUser))))

        val result = userProcess.register(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was no(mockContextSupport).setActiveUserId(persistenceUser.id)
        there was no(mockPersistenceServices).addUser(emptyAddUserRequest)
        there was no(mockPersistenceServices).fetchUsers

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "register as active a new user when there is one active user id but it doesn't exists in the database" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(None)))
        mockPersistenceServices.addUser(any) returns CatsService(Task(Xor.right(persistenceUser)))

        val result = userProcess.register(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockContextSupport).setActiveUserId(persistenceUser.id)
        there was one(mockPersistenceServices).addUser(emptyAddUserRequest)
        there was no(mockPersistenceServices).fetchUsers

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

  }

  "Unregister in UserProcess" should {

    "returns a UserException if there is no active user" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns None

        val result = userProcess.unregister(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was no(mockPersistenceServices).findUserById(any)
        there was no(mockPersistenceServices).updateUser(any)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "returns a UserException if the user doesn't exists in the database" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(None)))

        val result = userProcess.unregister(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was no(mockPersistenceServices).updateUser(any)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "update the user in the database but don't call to update installation if the user doesn't have a device token" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = Some(sessionToken),
          deviceToken = None)
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.unregister(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).updateUser(emptyUpdateUserRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "update the user in the database but don't call to update installation if the user doesn't have an api key" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = None,
          sessionToken = Some(sessionToken),
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.unregister(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).updateUser(emptyUpdateUserRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "update the user in the database but don't call to update installation if the user doesn't have a session token" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = None,
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.unregister(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).updateUser(emptyUpdateUserRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "update the user in the database and call to update installation with None if the user has a device token, api key and session token" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = Some(sessionToken),
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))
        mockPersistenceServices.getAndroidId(any) returns CatsService(Task(Xor.right(deviceId)))
        mockApiServices.updateInstallation(any)(any) returns CatsService(Task(Xor.right(updateInstallationResponse)))

        val result = userProcess.unregister(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).updateUser(emptyUpdateUserRequest)
        there was one(mockPersistenceServices).getAndroidId(mockContextSupport)
        there was one(mockApiServices).updateInstallation(None)(RequestConfig(apiKey, sessionToken, deviceId))

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

  }

  "Get User in UserProcess" should {

    "returns a UserException if there is no active user" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns None

        val result = userProcess.getUser(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was no(mockPersistenceServices).findUserById(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "returns a UserException if the user doesn't exists in the database" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(None)))

        val result = userProcess.getUser(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "returns the user that exists in the database" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(persistenceUser))))

        val result = userProcess.getUser(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))

        result must beLike {
          case Xor.Right(user) => user shouldEqual processUser
        }
      }
  }

  "Update User Device in UserProcess" should {

    "returns a UserException if there is no active user" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns None

        val result = userProcess.updateUserDevice(deviceName, deviceCloudId, Some(deviceToken))(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was no(mockPersistenceServices).findUserById(any)
        there was no(mockPersistenceServices).updateUser(any)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "returns a UserException if the user doesn't exists in the database" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(None)))

        val result = userProcess.updateUserDevice(deviceName, deviceCloudId, Some(deviceToken))(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was no(mockPersistenceServices).updateUser(any)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "updates the user in the database with the new data but doesn't call to update installation when the user has the same device token" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = Some(sessionToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.updateUserDevice(anotherDeviceName, anotherDeviceCloudId, Some(deviceToken))(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        val updateRequest = updateUserRequest.copy(
          deviceName = Some(anotherDeviceName),
          deviceCloudId = Some(anotherDeviceCloudId),
          deviceToken = Some(deviceToken))
        there was one(mockPersistenceServices).updateUser(updateRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "updates the user in the database with the new data but doesn't call to update installation when the user doesn't have api key" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = None,
          sessionToken = Some(sessionToken),
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.updateUserDevice(anotherDeviceName, anotherDeviceCloudId, Some(anotherDeviceToken))(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        val updateRequest = updateUserRequest.copy(
          deviceName = Some(anotherDeviceName),
          deviceCloudId = Some(anotherDeviceCloudId),
          deviceToken = Some(anotherDeviceToken))
        there was one(mockPersistenceServices).updateUser(updateRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "updates the user in the database with the new data but doesn't call to update installation when the user doesn't have session key" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = None,
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.updateUserDevice(anotherDeviceName, anotherDeviceCloudId, Some(anotherDeviceToken))(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        val updateRequest = updateUserRequest.copy(
          deviceName = Some(anotherDeviceName),
          deviceCloudId = Some(anotherDeviceCloudId),
          deviceToken = Some(anotherDeviceToken))
        there was one(mockPersistenceServices).updateUser(updateRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "updates the user in the database with the new data and calls to update installation when the user have an api key, a session key and the device token is different" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = Some(sessionToken),
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))
        mockPersistenceServices.getAndroidId(any) returns CatsService(Task(Xor.right(deviceId)))
        mockApiServices.updateInstallation(any)(any) returns CatsService(Task(Xor.right(updateInstallationResponse)))

        val result = userProcess.updateUserDevice(anotherDeviceName, anotherDeviceCloudId, Some(anotherDeviceToken))(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        val updateRequest = updateUserRequest.copy(
          deviceName = Some(anotherDeviceName),
          deviceCloudId = Some(anotherDeviceCloudId),
          deviceToken = Some(anotherDeviceToken))
        there was one(mockPersistenceServices).updateUser(updateRequest)
        there was one(mockPersistenceServices).getAndroidId(mockContextSupport)
        there was one(mockApiServices).updateInstallation(Some(anotherDeviceToken))(RequestConfig(apiKey, sessionToken, deviceId))

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "updates the user in the database with the new data and calls to update installation passing the user device token when is called with no device token" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = Some(sessionToken),
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))
        mockPersistenceServices.getAndroidId(any) returns CatsService(Task(Xor.right(deviceId)))
        mockApiServices.updateInstallation(any)(any) returns CatsService(Task(Xor.right(updateInstallationResponse)))

        val result = userProcess.updateUserDevice(anotherDeviceName, anotherDeviceCloudId, None)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        val updateRequest = updateUserRequest.copy(
          deviceName = Some(anotherDeviceName),
          deviceCloudId = Some(anotherDeviceCloudId),
          deviceToken = Some(anotherDeviceToken))
        there was one(mockPersistenceServices).updateUser(updateRequest)
        there was one(mockPersistenceServices).getAndroidId(mockContextSupport)
        there was one(mockApiServices).updateInstallation(Some(deviceToken))(RequestConfig(apiKey, sessionToken, deviceId))

        result must beAnInstanceOf[Xor.Right[Unit]]
      }
  }

  "Update Device token in UserProcess" should {

    "returns a UserException if there is no active user" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns None

        val result = userProcess.updateDeviceToken(deviceToken)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was no(mockPersistenceServices).findUserById(any)
        there was no(mockPersistenceServices).updateUser(any)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "returns a UserException if the user doesn't exists in the database" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(None)))

        val result = userProcess.updateDeviceToken(deviceToken)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was no(mockPersistenceServices).updateUser(any)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[UserException]
        }
      }

    "updates the user in the database with the new data but doesn't call to update installation when the user has the same device token" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = Some(sessionToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.updateDeviceToken(deviceToken)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).updateUser(updateUserRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "updates the user in the database with the new data but doesn't call to update installation when the user doesn't have api key" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = None,
          sessionToken = Some(sessionToken),
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.updateDeviceToken(deviceToken)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).updateUser(updateUserRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "updates the user in the database with the new data but doesn't call to update installation when the user doesn't have session key" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = None,
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))

        val result = userProcess.updateDeviceToken(deviceToken)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).updateUser(updateUserRequest)
        there was no(mockPersistenceServices).getAndroidId(any)
        there was no(mockApiServices).updateInstallation(any)(any)

        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "updates the user in the database with the new data and calls to update installation when the user have an api key, a session key and the device token is different" in
      new UserProcessScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        val user = persistenceUser.copy(
          apiKey = Some(apiKey),
          sessionToken = Some(sessionToken),
          deviceToken = Some(deviceToken))
        mockPersistenceServices.findUserById(any) returns CatsService(Task(Xor.right(Some(user))))
        mockPersistenceServices.updateUser(any) returns CatsService(Task(Xor.right(1)))
        mockPersistenceServices.getAndroidId(any) returns CatsService(Task(Xor.right(deviceId)))
        mockApiServices.updateInstallation(any)(any) returns CatsService(Task(Xor.right(updateInstallationResponse)))

        val result = userProcess.updateDeviceToken(anotherDeviceToken)(mockContextSupport).value.run

        there was one(mockContextSupport).getActiveUserId
        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        val updateRequest = updateUserRequest.copy(deviceToken = Some(anotherDeviceToken))
        there was one(mockPersistenceServices).updateUser(updateRequest)
        there was one(mockPersistenceServices).getAndroidId(mockContextSupport)
        there was one(mockApiServices).updateInstallation(Some(anotherDeviceToken))(RequestConfig(apiKey, sessionToken, deviceId))

        result must beAnInstanceOf[Xor.Right[Unit]]
      }
  }

}
