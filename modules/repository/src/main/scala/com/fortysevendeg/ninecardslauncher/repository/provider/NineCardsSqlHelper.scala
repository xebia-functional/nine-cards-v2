package com.fortysevendeg.ninecardslauncher.repository.provider

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.os.Handler
import com.fortysevendeg.ninecardslauncher.commons.javaNull

class NineCardsSqlHelper(context: Context)
  extends SQLiteOpenHelper(context, NineCardsSqlHelper.databaseName, javaNull, NineCardsSqlHelper.databaseVersion) {

  override def onCreate(db: SQLiteDatabase) = {

    db.execSQL(AppEntity.createTableSQL)
    db.execSQL(CollectionEntity.createTableSQL)
    db.execSQL(CardEntity.createTableSQL)
    db.execSQL(DockAppEntity.createTableSQL)
    db.execSQL(MomentEntity.createTableSQL)
    db.execSQL(UserEntity.createTableSQL)
    db.execSQL(WidgetEntity.createTableSQL)
  }

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {

    (oldVersion + 1 to newVersion) foreach {
      case 2 =>
        db.execSQL(s"ALTER TABLE Card ADD COLUMN notification TEXT")
      case 3 =>
        db.execSQL(s"ALTER TABLE Card ADD COLUMN micros INTEGER")
      case 4 =>
        db.execSQL(s"ALTER TABLE Collection ADD COLUMN sharedCollectionId TEXT")
        db.execSQL(s"ALTER TABLE Collection ADD COLUMN originalSharedCollectionId TEXT")
        db.execSQL(s"ALTER TABLE Collection ADD COLUMN sharedCollectionSubscribed INTEGER")
      case 5 => db.execSQL(
        s"""
            |CREATE TABLE app (
            |  _id                      INTEGER PRIMARY KEY AUTOINCREMENT,
            |  name                     TEXT not null,
            |  packageName              TEXT not null,
            |  className                TEXT not null,
            |  category                 TEXT not null,
            |  imagePath                TEXT not null,
            |  dateInstalled            INTEGER,
            |  dateUpdate               INTEGER,
            |  version                  TEXT not null,
            |  installedFromGooglePlay  INTEGER)""".stripMargin)
      case 6 => db.execSQL("DROP TABLE CacheCategory")
      case 7 => db.execSQL(
        s"""
            |CREATE TABLE User (
            |  _id            INTEGER PRIMARY KEY AUTOINCREMENT,
            |  email          TEXT,
            |  sessionToken   TEXT,
            |  deviceToken    TEXT,
            |  androidToken   TEXT)""".stripMargin)
      case 8 => db.execSQL(
        s"""
            |CREATE TABLE DockApp (
            |  _id        INTEGER PRIMARY KEY AUTOINCREMENT,
            |  name       TEXT not null,
            |  dockType   TEXT not null,
            |  intent     TEXT not null,
            |  imagePath  TEXT not null,
            |  position   INTEGER not null)""".stripMargin)
      case 9 =>
        db.execSQL("DROP TABLE GeoInfo")
        db.execSQL(
          s"""
              |CREATE TABLE Moment (
              |  _id            INTEGER PRIMARY KEY AUTOINCREMENT,
              |  collectionId   INTEGER,
              |  timeslot       TEXT not null,
              |  wifi           TEXT not null,
              |  headphone      INTEGER not null)""".stripMargin)
      case 10 =>
        db.execSQL(s"ALTER TABLE Moment ADD COLUMN momentType TEXT")
        db.execSQL(s"ALTER TABLE User ADD COLUMN name TEXT")
        db.execSQL(s"ALTER TABLE User ADD COLUMN avatar TEXT")
        db.execSQL(s"ALTER TABLE User ADD COLUMN cover TEXT")
      case 11 =>
        db.execSQL(s"ALTER TABLE User ADD COLUMN deviceName TEXT")
        db.execSQL(s"ALTER TABLE User ADD COLUMN deviceCloudId TEXT")
      case 12 =>
        db.execSQL(
          s"""
              |CREATE TABLE Widget (
              |  _id          INTEGER PRIMARY KEY AUTOINCREMENT,
              |  momentId     INTEGER not null,
              |  packageName  TEXT not null,
              |  className    TEXT not null,
              |  appWidgetId  INTEGER not null,
              |  startX       INTEGER not null,
              |  startY       INTEGER not null,
              |  spanX        INTEGER not null,
              |  spanY        INTEGER not null,
              |  widgetType   TEXT not null,
              |  label        TEXT,
              |  imagePath    TEXT,
              |  intent       TEXT)""".stripMargin)
      case 13 =>
        db.execSQL(s"ALTER TABLE User RENAME TO tmp_User")
        db.execSQL(
          s"""
             |CREATE TABLE User (
             |  _id             INTEGER PRIMARY KEY AUTOINCREMENT,
             |  email           TEXT,
             |  apiKey          TEXT,
             |  sessionToken    TEXT,
             |  deviceToken     TEXT,
             |  marketToken     TEXT,
             |  name            TEXT,
             |  avatar          TEXT,
             |  cover           TEXT,
             |  deviceName      TEXT,
             |  deviceCloudId   TEXT)""".stripMargin)
        db.execSQL(
          s"""
             |INSERT INTO User (
             |  _id,
             |  email,
             |  sessionToken,
             |  deviceToken,
             |  marketToken,
             |  name,
             |  avatar,
             |  cover,
             |  deviceName,
             |  deviceCloudId)
             |SELECT
             |  _id,
             |  email,
             |  sessionToken,
             |  deviceToken,
             |  androidToken,
             |  name,
             |  avatar,
             |  cover,
             |  deviceName,
             |  deviceCloudId
             |FROM tmp_User """.stripMargin)
        db.execSQL(s"DROP TABLE tmp_User")
    }
  }
}

object NineCardsSqlHelper {
  val id = "_id"
  val databaseName = "nineCards"
  val databaseVersion = 13
}
