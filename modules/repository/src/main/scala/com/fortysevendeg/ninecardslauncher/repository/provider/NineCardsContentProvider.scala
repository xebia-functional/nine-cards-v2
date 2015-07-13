package com.fortysevendeg.ninecardslauncher.repository.provider

import android.content.{ContentProvider, ContentUris, ContentValues, UriMatcher}
import android.database.Cursor
import android.database.sqlite.{SQLiteDatabase, SQLiteQueryBuilder}
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.repository.commons._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsContentProvider._

class NineCardsContentProvider extends ContentProvider {

  lazy val nineCardsSqlHelper = new NineCardsSqlHelper(getContext)
  lazy val database: Option[SQLiteDatabase] = Option[SQLiteDatabase](nineCardsSqlHelper.getWritableDatabase)

  private def getUriInfo(uri: Uri): (String, MimeType) = uriMatcher.`match`(uri) match {
    case `codeCacheCategoryAllItems` => (CacheCategoryEntity.table, MimeTypeAllItems)
    case `codeCacheCategorySingleItem` => (CacheCategoryEntity.table, MimeTypeSingleItem)
    case `codeCardAllItems` => (CardEntity.table, MimeTypeAllItems)
    case `codeCardSingleItem` => (CardEntity.table, MimeTypeSingleItem)
    case `codeCollectionAllItems` => (CollectionEntity.table, MimeTypeAllItems)
    case `codeCollectionSingleItem` => (CollectionEntity.table, MimeTypeSingleItem)
    case `codeGeoInfoAllItems` => (GeoInfoEntity.table, MimeTypeAllItems)
    case `codeGeoInfoSingleItem` => (GeoInfoEntity.table, MimeTypeSingleItem)
    case _ => throw new IllegalArgumentException(invalidUri + uri)
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
      case (_, MimeTypeAllItems) => mimeTypeAllItemsValue
      case (_, MimeTypeSingleItem) => mimeTypeSingleItemValue
    }
  }

  override def update(uri: Uri, values: ContentValues, selection: String, selectionArgs: Array[String]): Int = {
    val (tableName, mimeType) = getUriInfo(uri)

    mimeType match {
      case MimeTypeSingleItem =>
        getOrOpenDatabase.update(
          tableName,
          values,
          s"${NineCardsSqlHelper.id} = ?",
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
          getUri(CacheCategoryUri),
          getOrOpenDatabase.insert(tableName, NineCardsSqlHelper.databaseName, values))
      case _ => throw new IllegalArgumentException(invalidUri + uri)
    }
  }

  override def delete(uri: Uri, selection: String, selectionArgs: Array[String]): Int = {
    val (tableName, mimeType) = getUriInfo(uri)

    mimeType match {
      case MimeTypeSingleItem =>
        getOrOpenDatabase.delete(
          tableName,
          s"${NineCardsSqlHelper.id} = ?",
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
    sortOrder: String
    ): Cursor = {
    val (tableName, mimeType) = getUriInfo(uri)

    mimeType match {
      case MimeTypeSingleItem =>
        val queryBuilder = new SQLiteQueryBuilder()
        queryBuilder.setTables(tableName)
        queryBuilder.query(
          getOrOpenDatabase,
          projection,
          s"${NineCardsSqlHelper.id} = ?",
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
  val invalidUri = "Invalid uri: "
  val authorityPart = "com.fortysevendeg.ninecardslauncher2"
  val contentPrefix = "content://"
  val codeCacheCategoryAllItems = 1
  val codeCacheCategorySingleItem = 2
  val codeCardAllItems = 3
  val codeCardSingleItem = 4
  val codeCollectionAllItems = 5
  val codeCollectionSingleItem = 6
  val codeGeoInfoAllItems = 7
  val codeGeoInfoSingleItem = 8
  val mimeTypeAllItemsValue = "vnd.android.cursor.dir/vnd.com.fortysevendeg.ninecardslauncher"
  val mimeTypeSingleItemValue = "vnd.android.cursor.item/vnd.com.fortysevendeg.ninecardslauncher"

  val uriMatcher = new UriMatcher(UriMatcher.NO_MATCH)
  uriMatcher.addURI(authorityPart, CacheCategoryEntity.table, codeCacheCategoryAllItems)
  uriMatcher.addURI(authorityPart, s"${CacheCategoryEntity.table}/#", codeCacheCategorySingleItem)
  uriMatcher.addURI(authorityPart, CardEntity.table, codeCardAllItems)
  uriMatcher.addURI(authorityPart, s"${CardEntity.table}/#", codeCardSingleItem)
  uriMatcher.addURI(authorityPart, CollectionEntity.table, codeCollectionAllItems)
  uriMatcher.addURI(authorityPart, s"${CollectionEntity.table}/#", codeCollectionSingleItem)
  uriMatcher.addURI(authorityPart, GeoInfoEntity.table, codeGeoInfoAllItems)
  uriMatcher.addURI(authorityPart, s"${GeoInfoEntity.table}/#", codeGeoInfoSingleItem)

  def getUri(nineCardsUri: NineCardsUri): Uri = nineCardsUri match {
    case CacheCategoryUri => Uri.parse(s"$contentPrefix$authorityPart/${CacheCategoryEntity.table}")
    case CardUri => Uri.parse(s"$contentPrefix$authorityPart/${CardEntity.table}")
    case CollectionUri => Uri.parse(s"$contentPrefix$authorityPart/${CollectionEntity.table}")
    case GeoInfoUri => Uri.parse(s"$contentPrefix$authorityPart/${GeoInfoEntity.table}")
  }
}

sealed trait MimeType

object MimeTypeAllItems extends MimeType

object MimeTypeSingleItem extends MimeType
