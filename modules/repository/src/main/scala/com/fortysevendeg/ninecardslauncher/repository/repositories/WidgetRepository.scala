package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toWidget
import com.fortysevendeg.ninecardslauncher.repository.model.{Widget, WidgetData}
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.provider.WidgetEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.{NineCardsUri, WidgetEntity}
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scala.language.postfixOps
import scalaz.concurrent.Task

class WidgetRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val widgetUri = uriCreator.parse(widgetUriString)

  val widgetNotificationUri = uriCreator.parse(widgetUriNotificationString)

  def addWidget(data: WidgetData): CatsService[Widget] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = widgetUri,
            values = values,
            notificationUri = Some(widgetNotificationUri))

          Widget(id = id, data = data)
        }
      }
    }

  def addWidgets(datas: Seq[WidgetData]): CatsService[Seq[Widget]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {

          val values = datas map createMapValues

          val ids = contentResolverWrapper.inserts(
            authority = NineCardsUri.authorityPart,
            uri = widgetUri,
            allValues = values,
            notificationUri = Some(widgetNotificationUri))

          datas zip ids map {
            case (data, id) => Widget(id = id, data = data)
          }
        }
      }
    }

  def deleteWidgets(where: String = ""): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = widgetUri,
            where = where,
            notificationUri = Some(widgetNotificationUri))
        }
      }
    }

  def deleteWidget(widget: Widget): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = widgetUri,
            id = widget.id,
            notificationUri = Some(widgetNotificationUri))
        }
      }
    }

  def findWidgetById(id: Int): CatsService[Option[Widget]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = widgetUri,
            id = id,
            projection = allFields)(getEntityFromCursor(widgetEntityFromCursor)) map toWidget
        }
      }
    }

  def fetchWidgetByAppWidgetId(appWidgetId: Int): CatsService[Option[Widget]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          fetchWidget(selection = s"${WidgetEntity.appWidgetId} = ?", selectionArgs = Seq(appWidgetId.toString))
        }
      }
    }

  def fetchWidgetsByMoment(momentId: Int): CatsService[Seq[Widget]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = widgetUri,
            projection = allFields,
            where = s"${WidgetEntity.momentId} = ?",
            whereParams = Seq(momentId.toString),
            orderBy = s"${WidgetEntity.momentId} asc")(getListFromCursor(widgetEntityFromCursor)) map toWidget
        }
      }
    }

  def fetchWidgets(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): CatsService[Seq[Widget]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = widgetUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy)(getListFromCursor(widgetEntityFromCursor)) map toWidget
        }
      }
    }

  def fetchIterableWidgets(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): CatsService[IterableCursor[Widget]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = widgetUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(widgetFromCursor)
        }
      }
    }

  def updateWidget(widget: Widget): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(widget.data)

          contentResolverWrapper.updateById(
            uri = widgetUri,
            id = widget.id,
            values = values,
            notificationUri = Some(widgetNotificationUri))
        }
      }
    }

  def updateWidgets(widgets: Seq[Widget]): CatsService[Seq[Int]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = widgets map { widget =>
            (widget.id, createMapValues(widget.data))
          }

          contentResolverWrapper.updateByIds(
            authority = NineCardsUri.authorityPart,
            uri = widgetUri,
            idAndValues = values,
            notificationUri = Some(widgetNotificationUri))
        }
      }
    }

  private[this] def fetchWidget(
    uri: Uri = widgetUri,
    projection: Seq[String] = allFields,
    selection: String = "",
    selectionArgs: Seq[String] = Seq.empty[String],
    sortOrder: String = "") =
    contentResolverWrapper.fetch(
      uri = uri,
      projection = projection,
      where = selection,
      whereParams = selectionArgs,
      orderBy = sortOrder)(getEntityFromCursor(widgetEntityFromCursor)) map toWidget

  private[this] def createMapValues(data: WidgetData) =
    Map[String, Any](
      momentId -> data.momentId,
      packageName -> data.packageName,
      className -> data.className,
      appWidgetId -> data.appWidgetId,
      startX -> data.startX,
      startY -> data.startY,
      spanX -> data.spanX,
      spanY -> data.spanY,
      widgetType -> data.widgetType,
      label -> (data.label orNull),
      imagePath -> (data.imagePath orNull),
      intent -> (data.intent orNull))
}
