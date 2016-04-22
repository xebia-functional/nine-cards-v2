package com.fortysevendeg.ninecardslauncher.repository.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.repository.model.User
import com.fortysevendeg.ninecardslauncher.repository.provider.UserEntity._
import com.fortysevendeg.ninecardslauncher.repository.Conversions._

case class UserEntity(id: Int, data: UserEntityData)

case class UserEntityData(
  userId: String,
  email: String,
  sessionToken: String,
  installationId: String,
  deviceToken: String,
  androidToken: String,
  name: String,
  avatar: String,
  cover: String)

object UserEntity {
  val table = "User"
  val userId = "userId"
  val email = "email"
  val sessionToken = "sessionToken"
  val installationId = "installationId"
  val deviceToken = "deviceToken"
  val androidToken = "androidToken"
  val name = "name"
  val avatar = "avatar"
  val cover = "cover"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    userId,
    email,
    sessionToken,
    installationId,
    deviceToken,
    androidToken,
    name,
    avatar,
    cover)

  def userEntityFromCursor(cursor: Cursor): UserEntity =
    UserEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = UserEntityData(
        userId = cursor.getString(cursor.getColumnIndex(userId)),
        email = cursor.getString(cursor.getColumnIndex(email)),
        sessionToken = cursor.getString(cursor.getColumnIndex(sessionToken)),
        installationId = cursor.getString(cursor.getColumnIndex(installationId)),
        deviceToken = cursor.getString(cursor.getColumnIndex(deviceToken)),
        androidToken = cursor.getString(cursor.getColumnIndex(androidToken)),
        name = cursor.getString(cursor.getColumnIndex(name)),
        avatar = cursor.getString(cursor.getColumnIndex(avatar)),
        cover = cursor.getString(cursor.getColumnIndex(cover))))

  def userFromCursor(cursor: Cursor): User = toUser(userEntityFromCursor(cursor))

  def createTableSQL: String =
    s"""CREATE TABLE ${UserEntity.table}
        |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
        |${UserEntity.userId} TEXT,
        |${UserEntity.email} TEXT,
        |${UserEntity.sessionToken} INTEGER,
        |${UserEntity.installationId} TEXT,
        |${UserEntity.deviceToken} TEXT,
        |${UserEntity.androidToken} INTEGER,
        |${UserEntity.name} TEXT,
        |${UserEntity.avatar } TEXT,
        |${UserEntity.cover} TEXT)""".stripMargin

}
