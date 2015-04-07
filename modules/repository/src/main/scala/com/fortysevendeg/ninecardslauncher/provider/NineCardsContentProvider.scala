package com.fortysevendeg.ninecardslauncher.provider

import android.content.{ContentProvider, ContentUris, ContentValues, UriMatcher}
import android.database.Cursor
import android.database.sqlite.{SQLiteDatabase, SQLiteQueryBuilder}
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.provider.NineCardsContentProvider._

class NineCardsContentProvider extends ContentProvider {

  lazy val nineCardsSqlHelper = new NineCardsSqlHelper(getContext)
  lazy val database: Option[SQLiteDatabase] = Option[SQLiteDatabase](nineCardsSqlHelper.getWritableDatabase)

  private def getUriInfo(uri: Uri): (String, MimeType) = uriMatcher.`match`(uri) match {
    case CodeCacheCategoryAllItems => (CacheCategoryEntity.Table, MimeTypeAllItems)
    case CodeCacheCategorySingleItem => (CacheCategoryEntity.Table, MimeTypeSingleItem)
    case CodeCardAllItems => (CardEntity.Table, MimeTypeAllItems)
    case CodeCardSingleItem => (CardEntity.Table, MimeTypeSingleItem)
    case CodeCollectionAllItems => (CollectionEntity.Table, MimeTypeAllItems)
    case CodeCollectionSingleItem => (CollectionEntity.Table, MimeTypeSingleItem)
    case CodeGeoInfoAllItems => (GeoInfoEntity.Table, MimeTypeAllItems)
    case CodeGeoInfoSingleItem => (GeoInfoEntity.Table, MimeTypeSingleItem)
    case _ => throw new IllegalArgumentException(InvalidUri + uri)
  }

  override def onCreate(): Boolean = database match {
    case Some(databaseObject) if databaseObject.isOpen => true
    case _ => false
  }

  override def onLowMemory() {
    super.onLowMemory()
    nineCardsSqlHelper.close()
  }

  override def getType(uri: Uri): String = {
    getUriInfo(uri) match {
      case (_, MimeTypeAllItems) => MimeTypeAllItemsValue
      case (_, MimeTypeSingleItem) => MimeTypeSingleItemValue
    }
  }

  override def update(uri: Uri, values: ContentValues, selection: String, selectionArgs: Array[String]): Int = {
    val (tableName, mimeType) = getUriInfo(uri)

    mimeType match {
      case MimeTypeSingleItem =>
        getOrOpenDatabase.update(
          tableName,
          values,
          s"${NineCardsSqlHelper.Id} = ?",
          Seq(uri.getPathSegments.get(1)).toArray)
      case MimeTypeAllItems =>
        getOrOpenDatabase.update(tableName, values, selection, selectionArgs)
    }
  }

  override def insert(uri: Uri, values: ContentValues): Uri = {
    val (tableName, mimeType) = getUriInfo(uri)

    mimeType match {
      case MimeTypeAllItems =>
        ContentUris.withAppendedId(
          ContentUriCacheCategory,
          getOrOpenDatabase.insert(tableName, NineCardsSqlHelper.DatabaseName, values))
      case _ => throw new IllegalArgumentException(InvalidUri + uri)
    }
  }

  override def delete(uri: Uri, selection: String, selectionArgs: Array[String]): Int = {
    val (tableName, mimeType) = getUriInfo(uri)

    mimeType match {
      case MimeTypeSingleItem =>
        getOrOpenDatabase.delete(
          tableName,
          s"${NineCardsSqlHelper.Id} = ?",
          Seq(uri.getPathSegments.get(1)).toArray)
      case MimeTypeAllItems =>
        getOrOpenDatabase.delete(tableName, selection, selectionArgs)
    }
  }

  override def query(
      uri: Uri,
      projection: Array[String],
      selection: String,
      selectionArgs: Array[String],
      sortOrder: String): Cursor = {
    val (tableName, mimeType) = getUriInfo(uri)

    mimeType match {
      case MimeTypeSingleItem =>
        val queryBuilder = new SQLiteQueryBuilder()
        queryBuilder.setTables(tableName)
        queryBuilder.query(
          getOrOpenDatabase,
          projection,
          s"${NineCardsSqlHelper.Id} = ?",
          Seq(uri.getPathSegments.get(1)).toArray,
          null,
          null,
          sortOrder)
      case MimeTypeAllItems =>
        val queryBuilder = new SQLiteQueryBuilder()
        queryBuilder.setTables(tableName)
        queryBuilder.query(getOrOpenDatabase, projection, selection, selectionArgs, null, null, sortOrder)
    }
  }

  private def getOrOpenDatabase = database match {
    case Some(databaseObject) if databaseObject.isOpen => databaseObject
    case _ => nineCardsSqlHelper.getWritableDatabase
  }
}

object NineCardsContentProvider {
  val InvalidUri = "Invalid uri: "
  val AuthorityPart = "com.fortysevendeg.ninecardslauncher"
  val ContentPrefix = "content://"
  val ContentUriCacheCategory = Uri.parse(s"$ContentPrefix$AuthorityPart/${CacheCategoryEntity.Table}")
  val CodeCacheCategoryAllItems = 1
  val CodeCacheCategorySingleItem = 2
  val ContentUriCard = Uri.parse(s"$ContentPrefix$AuthorityPart/${CardEntity.Table}")
  val CodeCardAllItems = 3
  val CodeCardSingleItem = 4
  val ContentUriCollection = Uri.parse(s"$ContentPrefix$AuthorityPart/${CollectionEntity.Table}")
  val CodeCollectionAllItems = 5
  val CodeCollectionSingleItem = 6
  val ContentUriGeoInfo = Uri.parse(s"$ContentPrefix$AuthorityPart/${GeoInfoEntity.Table}")
  val CodeGeoInfoAllItems = 7
  val CodeGeoInfoSingleItem = 8
  val MimeTypeAllItemsValue = "vnd.android.cursor.dir/vnd.com.fortysevendeg.ninecardslauncher"
  val MimeTypeSingleItemValue = "vnd.android.cursor.item/vnd.com.fortysevendeg.ninecardslauncher"

  val uriMatcher = new UriMatcher(UriMatcher.NO_MATCH)
  uriMatcher.addURI(AuthorityPart, CacheCategoryEntity.Table, CodeCacheCategoryAllItems)
  uriMatcher.addURI(AuthorityPart, s"${CacheCategoryEntity.Table}/#", CodeCacheCategorySingleItem)
  uriMatcher.addURI(AuthorityPart, CardEntity.Table, CodeCardAllItems)
  uriMatcher.addURI(AuthorityPart, s"${CardEntity.Table}/#", CodeCardSingleItem)
  uriMatcher.addURI(AuthorityPart, CollectionEntity.Table, CodeCollectionAllItems)
  uriMatcher.addURI(AuthorityPart, s"${CollectionEntity.Table}/#", CodeCollectionSingleItem)
  uriMatcher.addURI(AuthorityPart, GeoInfoEntity.Table, CodeGeoInfoAllItems)
  uriMatcher.addURI(AuthorityPart, s"${GeoInfoEntity.Table}/#", CodeGeoInfoSingleItem)
}

sealed trait MimeType

object MimeTypeAllItems extends MimeType

object MimeTypeSingleItem extends MimeType
