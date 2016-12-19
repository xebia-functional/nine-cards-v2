package cards.nine.repository.provider

import android.content.{ContentProvider, ContentUris, ContentValues, UriMatcher}
import android.database.Cursor
import android.database.sqlite.{SQLiteDatabase, SQLiteQueryBuilder}
import android.net.Uri
import cards.nine.repository.provider.NineCardsContentProvider._
import cards.nine.repository.provider.NineCardsUri._
import cards.nine.commons.javaNull

class NineCardsContentProvider extends ContentProvider {

  lazy val nineCardsSqlHelper = new NineCardsSqlHelper(getContext)

  lazy val database: Option[SQLiteDatabase] =
    Option[SQLiteDatabase](nineCardsSqlHelper.getWritableDatabase)

  private[this] def getUriInfo(uri: Uri): (String, MimeType) =
    uriMatcher.`match`(uri) match {
      case `codeCardAllItems`         => (CardEntity.table, MimeTypeAllItems)
      case `codeCardSingleItem`       => (CardEntity.table, MimeTypeSingleItem)
      case `codeCollectionAllItems`   => (CollectionEntity.table, MimeTypeAllItems)
      case `codeCollectionSingleItem` => (CollectionEntity.table, MimeTypeSingleItem)
      case `codeAppAllItems`          => (AppEntity.table, MimeTypeAllItems)
      case `codeAppSingleItem`        => (AppEntity.table, MimeTypeSingleItem)
      case `codeDockAppAllItems`      => (DockAppEntity.table, MimeTypeAllItems)
      case `codeDockAppSingleItem`    => (DockAppEntity.table, MimeTypeSingleItem)
      case `codeMomentAllItems`       => (MomentEntity.table, MimeTypeAllItems)
      case `codeMomentSingleItem`     => (MomentEntity.table, MimeTypeSingleItem)
      case `codeUserAllItems`         => (UserEntity.table, MimeTypeAllItems)
      case `codeUserSingleItem`       => (UserEntity.table, MimeTypeSingleItem)
      case `codeWidgetAllItems`       => (WidgetEntity.table, MimeTypeAllItems)
      case `codeWidgetSingleItem`     => (WidgetEntity.table, MimeTypeSingleItem)

      case _ => throw new IllegalArgumentException(invalidUri + uri)
    }

  override def onCreate(): Boolean =
    database match {
      case Some(databaseObject) if databaseObject.isOpen => true
      case _                                             => false
    }

  override def onLowMemory() = {
    super.onLowMemory()
    nineCardsSqlHelper.close()
  }

  override def getType(uri: Uri): String =
    getUriInfo(uri) match {
      case (_, MimeTypeAllItems)   => mimeTypeAllItemsValue
      case (_, MimeTypeSingleItem) => mimeTypeSingleItemValue
    }

  override def update(
      uri: Uri,
      values: ContentValues,
      selection: String,
      selectionArgs: Array[String]): Int =
    getUriInfo(uri) match {
      case (tableName, MimeTypeSingleItem) =>
        getOrOpenDatabase.update(
          tableName,
          values,
          s"${NineCardsSqlHelper.id} = ?",
          Seq(uri.getPathSegments.get(1)).toArray)
      case (tableName, MimeTypeAllItems) =>
        getOrOpenDatabase.update(tableName, values, selection, selectionArgs)
    }

  override def insert(uri: Uri, values: ContentValues): Uri =
    getUriInfo(uri) match {
      case (tableName, MimeTypeAllItems) =>
        ContentUris.withAppendedId(
          uri,
          getOrOpenDatabase.insert(tableName, NineCardsSqlHelper.databaseName, values))
      case _ =>
        throw new IllegalArgumentException(invalidUri + uri)
    }

  override def delete(uri: Uri, selection: String, selectionArgs: Array[String]): Int =
    getUriInfo(uri) match {
      case (tableName, MimeTypeSingleItem) =>
        getOrOpenDatabase.delete(
          tableName,
          s"${NineCardsSqlHelper.id} = ?",
          Seq(uri.getPathSegments.get(1)).toArray)
      case (tableName, MimeTypeAllItems) =>
        getOrOpenDatabase.delete(tableName, selection, selectionArgs)
    }

  override def query(
      uri: Uri,
      projection: Array[String],
      selection: String,
      selectionArgs: Array[String],
      sortOrder: String): Cursor =
    getUriInfo(uri) match {
      case (tableName, MimeTypeSingleItem) =>
        val queryBuilder = new SQLiteQueryBuilder()
        queryBuilder.setTables(tableName)
        queryBuilder.query(
          getOrOpenDatabase,
          projection,
          s"${NineCardsSqlHelper.id} = ?",
          Seq(uri.getPathSegments.get(1)).toArray,
          javaNull,
          javaNull,
          sortOrder)
      case (tableName, MimeTypeAllItems) =>
        val queryBuilder = new SQLiteQueryBuilder()
        queryBuilder.setTables(tableName)
        queryBuilder.query(
          getOrOpenDatabase,
          projection,
          selection,
          selectionArgs,
          javaNull,
          javaNull,
          sortOrder)
    }

  private[this] def getOrOpenDatabase = database match {
    case Some(databaseObject) if databaseObject.isOpen => databaseObject
    case _                                             => nineCardsSqlHelper.getWritableDatabase
  }
}

object NineCardsContentProvider {

  val invalidUri               = "Invalid uri: "
  val codeAppAllItems          = 1
  val codeAppSingleItem        = 2
  val codeCardAllItems         = 3
  val codeCardSingleItem       = 4
  val codeCollectionAllItems   = 5
  val codeCollectionSingleItem = 6
  val codeDockAppAllItems      = 7
  val codeDockAppSingleItem    = 8
  val codeMomentAllItems       = 9
  val codeMomentSingleItem     = 10
  val codeUserAllItems         = 11
  val codeUserSingleItem       = 12
  val codeWidgetAllItems       = 13
  val codeWidgetSingleItem     = 14
  val mimeTypeAllItemsValue    = "vnd.android.cursor.dir/vnd.cards.nine"
  val mimeTypeSingleItemValue  = "vnd.android.cursor.item/vnd.cards.nine"

  val uriMatcher = new UriMatcher(UriMatcher.NO_MATCH)
  uriMatcher.addURI(authorityPart, AppEntity.table, codeAppAllItems)
  uriMatcher.addURI(authorityPart, s"${AppEntity.table}/#", codeAppSingleItem)
  uriMatcher.addURI(authorityPart, CardEntity.table, codeCardAllItems)
  uriMatcher.addURI(authorityPart, s"${CardEntity.table}/#", codeCardSingleItem)
  uriMatcher.addURI(authorityPart, CollectionEntity.table, codeCollectionAllItems)
  uriMatcher.addURI(authorityPart, s"${CollectionEntity.table}/#", codeCollectionSingleItem)
  uriMatcher.addURI(authorityPart, DockAppEntity.table, codeDockAppAllItems)
  uriMatcher.addURI(authorityPart, s"${DockAppEntity.table}/#", codeDockAppSingleItem)
  uriMatcher.addURI(authorityPart, MomentEntity.table, codeMomentAllItems)
  uriMatcher.addURI(authorityPart, s"${MomentEntity.table}/#", codeMomentSingleItem)
  uriMatcher.addURI(authorityPart, UserEntity.table, codeUserAllItems)
  uriMatcher.addURI(authorityPart, s"${UserEntity.table}/#", codeUserSingleItem)
  uriMatcher.addURI(authorityPart, WidgetEntity.table, codeWidgetAllItems)
  uriMatcher.addURI(authorityPart, s"${WidgetEntity.table}/#", codeWidgetSingleItem)
}

sealed trait MimeType

object MimeTypeAllItems extends MimeType

object MimeTypeSingleItem extends MimeType
