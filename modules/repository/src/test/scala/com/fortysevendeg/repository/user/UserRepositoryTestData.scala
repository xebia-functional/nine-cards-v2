package com.fortysevendeg.repository.user

import com.fortysevendeg.ninecardslauncher.repository.model.{User, UserData}
import com.fortysevendeg.ninecardslauncher.repository.provider.{UserEntity, UserEntityData}

import scala.util.Random

trait UserRepositoryTestData {

  val testId = Random.nextInt(10)
  val testNonExistingId = 15
  val testUserId = Random.nextString(10)
  val testEmail = Random.nextString(10)
  val testSessionToken= Random.nextString(10)
  val testInstallationId= Random.nextString(10)
  val testDeviceToken = Random.nextString(10)
  val testAndroidToken = Random.nextString(10)
  val testName = Random.nextString(10)
  val testAvatar = Random.nextString(10)
  val testCover = Random.nextString(10)

  val testUserIdOption = Option(testUserId)
  val testEmailOption = Option(testEmail)
  val testSessionTokenOption = Option(testSessionToken)
  val testInstallationIdOption = Option(testInstallationId)
  val testDeviceTokenOption = Option(testDeviceToken)
  val testAndroidTokenOption = Option(testAndroidToken)
  val testNameOption = Option(testName)
  val testAvatarOption = Option(testAvatar)
  val testCoverOption = Option(testCover)

  val userEntitySeq = createUserEntitySeq(5)
  val userEntity = userEntitySeq(0)
  val userSeq = createUserSeq(5)
  val user = userSeq(0)

  def createUserEntitySeq(num: Int) = List.tabulate(num)(
    i => UserEntity(
      id = testId + i,
      data = UserEntityData(
        userId = testUserId,
        email = testEmail,
        sessionToken = testSessionToken,
        installationId = testInstallationId,
        deviceToken = testDeviceToken,
        androidToken = testAndroidToken,
        name = testName,
        avatar = testAvatar,
        cover = testCover)))

  def createUserSeq(num: Int) = List.tabulate(num)(
    i => User(
      id = testId + i,
      data = UserData(
        userId = testUserIdOption,
        email = testEmailOption,
        sessionToken = testSessionTokenOption,
        installationId = testInstallationIdOption,
        deviceToken = testDeviceTokenOption,
        androidToken = testAndroidTokenOption,
        name = testNameOption,
        avatar = testAvatarOption,
        cover = testCoverOption)))

  def createUserValues = Map[String, Any](
    UserEntity.userId -> (testUserIdOption orNull),
    UserEntity.email -> (testEmailOption orNull),
    UserEntity.sessionToken -> (testSessionTokenOption orNull),
    UserEntity.installationId -> (testInstallationIdOption orNull),
    UserEntity.deviceToken -> (testDeviceTokenOption orNull),
    UserEntity.androidToken -> (testAndroidTokenOption orNull),
    UserEntity.name -> (testNameOption orNull),
    UserEntity.avatar -> (testAvatarOption orNull),
    UserEntity.cover -> (testCoverOption orNull))

  def createUserData = UserData(
    userId = testUserIdOption,
    email = testEmailOption,
    sessionToken = testSessionTokenOption,
    installationId = testInstallationIdOption,
    deviceToken = testDeviceTokenOption,
    androidToken = testAndroidTokenOption,
    name = testNameOption,
    avatar = testAvatarOption,
    cover = testCoverOption)
}
