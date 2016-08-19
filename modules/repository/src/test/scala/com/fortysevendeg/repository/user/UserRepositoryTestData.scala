package com.fortysevendeg.repository.user

import com.fortysevendeg.ninecardslauncher.repository.model.{User, UserData}
import com.fortysevendeg.ninecardslauncher.repository.provider.{UserEntity, UserEntityData}

import scalaz.Scalaz._

import scala.util.Random

trait UserRepositoryTestData {

  val testId = Random.nextInt(10)
  val testNonExistingId = 15
  val testEmail = Random.nextString(10)
  val testApiKey = Random.nextString(10)
  val testSessionToken= Random.nextString(10)
  val testDeviceToken = Random.nextString(10)
  val testAndroidToken = Random.nextString(10)
  val testName = Random.nextString(10)
  val testAvatar = Random.nextString(10)
  val testCover = Random.nextString(10)
  val testDeviceName = Random.nextString(10)
  val testDeviceCloudId = Random.nextString(10)

  val userEntitySeq = createUserEntitySeq(5)
  val userEntity = userEntitySeq.head
  val userSeq = createUserSeq(5)
  val user = userSeq.head

  def createUserEntitySeq(num: Int) = List.tabulate(num)(
    i => UserEntity(
      id = testId + i,
      data = UserEntityData(
        email = testEmail,
        apiKey = testApiKey,
        sessionToken = testSessionToken,
        deviceToken = testDeviceToken,
        marketToken = testAndroidToken,
        name = testName,
        avatar = testAvatar,
        cover = testCover,
        deviceName = testDeviceName,
        deviceCloudId = testDeviceCloudId)))

  def createUserSeq(num: Int) = List.tabulate(num)(
    i => User(
      id = testId + i,
      data = UserData(
        email = testEmail.some,
        apiKey = testApiKey.some,
        sessionToken = testSessionToken.some,
        deviceToken = testDeviceToken.some,
        marketToken = testAndroidToken.some,
        name = testName.some,
        avatar = testAvatar.some,
        cover = testCover.some,
        deviceName = testDeviceName.some,
        deviceCloudId = testDeviceCloudId.some)))

  def createUserValues = Map[String, Any](
    UserEntity.email -> testEmail,
    UserEntity.apiKey -> testApiKey,
    UserEntity.sessionToken -> testSessionToken,
    UserEntity.deviceToken -> testDeviceToken,
    UserEntity.marketToken -> testAndroidToken,
    UserEntity.name -> testName,
    UserEntity.avatar -> testAvatar,
    UserEntity.cover -> testCover,
    UserEntity.deviceName -> testDeviceName,
    UserEntity.deviceCloudId -> testDeviceCloudId)

  def createUserData = UserData(
    email = testEmail.some,
    apiKey = testApiKey.some,
    sessionToken = testSessionToken.some,
    deviceToken = testDeviceToken.some,
    marketToken = testAndroidToken.some,
    name = testName.some,
    avatar = testAvatar.some,
    cover = testCover.some,
    deviceName = testDeviceName.some,
    deviceCloudId = testDeviceCloudId.some)
}
