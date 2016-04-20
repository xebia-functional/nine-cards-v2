package com.fortysevendeg.ninecardslauncher.repository.provider

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.os.Handler
import com.fortysevendeg.ninecardslauncher.commons.javaNull

class NineCardsSqlHelper(context: Context)
  extends SQLiteOpenHelper(context, NineCardsSqlHelper.databaseName, javaNull, NineCardsSqlHelper.databaseVersion)
  with DBUtils{

  override def onCreate(db: SQLiteDatabase) = {

    db.execSQL(AppEntity.createTableSQL)
    db.execSQL(CollectionEntity.createTableSQL)
    db.execSQL(CardEntity.createTableSQL)
    db.execSQL(DockAppEntity.createTableSQL)
    db.execSQL(MomentEntity.createTableSQL)
    db.execSQL(UserEntity.createTableSQL)

    new Handler().postDelayed(() => execAllVersionsDB(), 0)
  }

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {

    (oldVersion + 1 to newVersion) foreach {
      case 2 =>
        db.execSQL(s"ALTER TABLE ${CardEntity.table} ADD COLUMN ${CardEntity.notification} TEXT")
      case 3 =>
        db.execSQL(s"ALTER TABLE ${CardEntity.table} ADD COLUMN micros INTEGER")
      case 4 =>
        db.execSQL(s"ALTER TABLE ${CollectionEntity.table} ADD COLUMN ${CollectionEntity.sharedCollectionId} TEXT")
        db.execSQL(s"ALTER TABLE ${CollectionEntity.table} ADD COLUMN ${CollectionEntity.originalSharedCollectionId} TEXT")
        db.execSQL(s"ALTER TABLE ${CollectionEntity.table} ADD COLUMN ${CollectionEntity.sharedCollectionSubscribed} INTEGER")
      case 5 => db.execSQL(AppEntity.createTableSQL)
      case 6 => db.execSQL("DROP TABLE CacheCategory")
      case 7 => db.execSQL(UserEntity.createTableSQL)
      case 8 => db.execSQL(DockAppEntity.createTableSQL)
      case 9 =>
        db.execSQL("DROP TABLE GeoInfo")
        db.execSQL(MomentEntity.createTableSQL)
      case 10 =>
        db.execSQL(s"ALTER TABLE ${MomentEntity.table} ADD COLUMN ${MomentEntity.momentType} TEXT")
    }

    new Handler().post(() => execVersionsDB(oldVersion, newVersion))
  }
}

object NineCardsSqlHelper {
  val id = "_id"
  val databaseName = "nineCards"
  val databaseVersion = 10
}
