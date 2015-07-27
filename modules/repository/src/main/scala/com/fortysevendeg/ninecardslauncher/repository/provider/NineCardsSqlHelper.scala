package com.fortysevendeg.ninecardslauncher.repository.provider

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.os.Handler

class NineCardsSqlHelper(context: Context)
  extends SQLiteOpenHelper(context, NineCardsSqlHelper.databaseName, null, NineCardsSqlHelper.databaseVersion)
  with DBUtils {

  override def onCreate(db: SQLiteDatabase) = {

    db.execSQL("CREATE TABLE " + CacheCategoryEntity.table +
      "(" + NineCardsSqlHelper.id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      CacheCategoryEntity.packageName + " TEXT not null, " +
      CacheCategoryEntity.category + " TEXT not null, " +
      CacheCategoryEntity.starRating + " DOUBLE, " +
      CacheCategoryEntity.numDownloads + " TEXT, " +
      CacheCategoryEntity.ratingsCount + " INTEGER, " +
      CacheCategoryEntity.commentCount + " INTEGER )")

    db.execSQL("CREATE TABLE " + CollectionEntity.table +
      "(" + NineCardsSqlHelper.id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      CollectionEntity.position + " INTEGER not null, " +
      CollectionEntity.name + " TEXT not null, " +
      CollectionEntity.collectionType + " TEXT not null, " +
      CollectionEntity.icon + " TEXT not null, " +
      CollectionEntity.themedColorIndex + " INTEGER not null, " +
      CollectionEntity.appsCategory + " TEXT, " +
      CollectionEntity.originalSharedCollectionId + " TEXT, " +
      CollectionEntity.sharedCollectionId + " TEXT, " +
      CollectionEntity.sharedCollectionSubscribed + " INTEGER, " +
      CollectionEntity.constrains + " TEXT )")

    db.execSQL("CREATE TABLE " + CardEntity.table +
      "(" + NineCardsSqlHelper.id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      CardEntity.position + " INTEGER not null, " +
      CardEntity.collectionId + " INTEGER not null, " +
      CardEntity.term + " TEXT not null, " +
      CardEntity.packageName + " TEXT, " +
      CardEntity.cardType + " TEXT not null, " +
      CardEntity.intent + " TEXT, " +
      CardEntity.imagePath + " TEXT, " +
      CardEntity.starRating + " DOUBLE, " +
      CardEntity.micros + " INTEGER, " +
      CardEntity.notification + " TEXT, " +
      CardEntity.numDownloads + " TEXT )")

    db.execSQL("CREATE TABLE " + GeoInfoEntity.table +
      "(" + NineCardsSqlHelper.id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      GeoInfoEntity.constrain + " TEXT not null, " +
      GeoInfoEntity.occurrence + " TEXT not null, " +
      GeoInfoEntity.wifi + " TEXT, " +
      GeoInfoEntity.latitude + " DOUBLE, " +
      GeoInfoEntity.longitude + " DOUBLE, " +
      GeoInfoEntity.system + " INTEGER )")

    new Handler().postDelayed(
      new Runnable() {
        override def run() = execAllVersionsDB()
      }, 0)
  }

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {

    (oldVersion + 1 to newVersion) foreach {
      case 2 =>
        db.execSQL("ALTER TABLE " + CardEntity.table + " ADD COLUMN " + CardEntity.notification + " TEXT")
      case 3 =>
        db.execSQL("ALTER TABLE " + CardEntity.table + " ADD COLUMN " + CardEntity.micros + " INTEGER")
      case 4 =>
        db.execSQL("ALTER TABLE " + CollectionEntity.table + " ADD COLUMN " + CollectionEntity.sharedCollectionId + " TEXT")
        db.execSQL("ALTER TABLE " + CollectionEntity.table + " ADD COLUMN " + CollectionEntity.originalSharedCollectionId + " TEXT")
        db.execSQL("ALTER TABLE " + CollectionEntity.table + " ADD COLUMN " + CollectionEntity.sharedCollectionSubscribed + " INTEGER")
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
  val databaseVersion = 4
}
