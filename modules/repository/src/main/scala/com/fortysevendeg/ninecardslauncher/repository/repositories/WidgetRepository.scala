package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
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

  def addWidget(data: WidgetData): ServiceDef2[Widget, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(data)

          val id = contentResolverWrapper.insert(
            uri = widgetUri,
            values = values,
            notificationUri = Some(widgetNotificationUri))

          Widget(id = id, data = data)
        }
      }
    }

  def addWidgets(datas: Seq[WidgetData]): ServiceDef2[Seq[Widget], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {

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

  def deleteWidgets(where: String = ""): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = widgetUri,
            where = where,
            notificationUri = Some(widgetNotificationUri))
        }
      }
    }

  def deleteWidget(widget: Widget): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = widgetUri,
            id = widget.id,
            notificationUri = Some(widgetNotificationUri))
        }
      }
    }

  def findWidgetById(id: Int): ServiceDef2[Option[Widget], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = widgetUri,
            id = id,
            projection = allFields)(getEntityFromCursor(widgetEntityFromCursor)) map toWidget
        }
      }
    }

  def fetchWidgetByAppWidgetId(appWidgetId: Int): ServiceDef2[Option[Widget], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          fetchWidget(selection = s"${WidgetEntity.appWidgetId} = ?", selectionArgs = Seq(appWidgetId.toString))
        }
      }
    }

  def fetchWidgetsByMoment(momentId: Int): ServiceDef2[Seq[Widget], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = widgetUri,
            projection = allFields,
            where = s"${WidgetEntity.momentId} = ?",
            whereParams = Seq(momentId.toString),
            orderBy = s"${WidgetEntity.momentId} asc")(getListFromCursor(widgetEntityFromCursor)) map toWidget
        }
      }
    }

  def fetchWidgets: ServiceDef2[Seq[Widget], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = widgetUri,
            projection = allFields)(getListFromCursor(widgetEntityFromCursor)) map toWidget
        }
      }
    }

  def fetchIterableWidgets(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): ServiceDef2[IterableCursor[Widget], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = widgetUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(widgetFromCursor)
        }
      }
    }

  def updateWidget(widget: Widget): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(widget.data)

          contentResolverWrapper.updateById(
            uri = widgetUri,
            id = widget.id,
            values = values,
            notificationUri = Some(widgetNotificationUri))
        }
      }
    }

  def updateWidgets(widgets: Seq[Widget]): ServiceDef2[Seq[Int], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
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
      spanX -> data.spanX,
      spanY -> data.spanY,
      startX -> data.startX,
      startY -> data.startY)
}
