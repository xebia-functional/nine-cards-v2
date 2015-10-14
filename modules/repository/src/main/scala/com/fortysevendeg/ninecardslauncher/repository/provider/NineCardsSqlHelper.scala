package com.fortysevendeg.ninecardslauncher.repository.provider

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.os.Handler

class NineCardsSqlHelper(context: Context)
  extends SQLiteOpenHelper(context, NineCardsSqlHelper.databaseName, null, NineCardsSqlHelper.databaseVersion)
  with DBUtils{

  override def onCreate(db: SQLiteDatabase) = {

    db.execSQL(AppEntity.createTableSQL)
    db.execSQL(CacheCategoryEntity.createTableSQL)
    db.execSQL(CollectionEntity.createTableSQL)
    db.execSQL(CardEntity.createTableSQL)
    db.execSQL(GeoInfoEntity.createTableSQL)

    new Handler().postDelayed(
      new Runnable() {
        override def run() = execAllVersionsDB()
      }, 0)
  }

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {

    (oldVersion + 1 to newVersion) foreach {
      case 2 =>
        db.execSQL(s"ALTER TABLE ${CardEntity.table} ADD COLUMN ${CardEntity.notification} TEXT")
      case 3 =>
        db.execSQL(s"ALTER TABLE ${CardEntity.table} ADD COLUMN ${CardEntity.micros} INTEGER")
      case 4 =>
        db.execSQL(s"ALTER TABLE ${CollectionEntity.table} ADD COLUMN ${CollectionEntity.sharedCollectionId} TEXT")
        db.execSQL(s"ALTER TABLE ${CollectionEntity.table} ADD COLUMN ${CollectionEntity.originalSharedCollectionId} TEXT")
        db.execSQL(s"ALTER TABLE ${CollectionEntity.table} ADD COLUMN ${CollectionEntity.sharedCollectionSubscribed} INTEGER")
      case 5 => db.execSQL(AppEntity.createTableSQL)
    }

    new Handler().post(
      new Runnable() {
        override def run() = execVersionsDB(oldVersion, newVersion)
      })
  }
}

object NineCardsSqlHelper {
  val id = "_id"
  val databaseName = "nineCards"
  val databaseVersion = 5
}
