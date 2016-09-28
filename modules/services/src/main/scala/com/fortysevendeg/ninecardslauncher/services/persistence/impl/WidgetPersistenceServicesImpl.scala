package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cards.nine.commons.services.TaskService._
import cards.nine.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.repository.provider.WidgetEntity
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions

trait WidgetPersistenceServicesImpl extends PersistenceServices {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addWidget(request: AddWidgetRequest) =
    (for {
      widget <- widgetRepository.addWidget(toRepositoryWidgetData(request))
    } yield toWidget(widget)).resolve[PersistenceServiceException]

  def addWidgets(request: Seq[AddWidgetRequest]) =
    (for {
      widgets <- widgetRepository.addWidgets(request map toRepositoryWidgetData)
    } yield widgets map toWidget).resolve[PersistenceServiceException]

  def deleteAllWidgets() =
    (for {
      deleted <- widgetRepository.deleteWidgets()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteWidget(request: DeleteWidgetRequest) =
    (for {
      deleted <- widgetRepository.deleteWidget(toRepositoryWidget(request.widget))
    } yield deleted).resolve[PersistenceServiceException]

  def deleteWidgetsByMoment(momentId: Int) =
    (for {
      deleted <- widgetRepository.deleteWidgets(where = s"${WidgetEntity.momentId} = $momentId")
    } yield deleted).resolve[PersistenceServiceException]

  def fetchWidgetByAppWidgetId(appWidgetId: Int) =
    (for {
      widget <- widgetRepository.fetchWidgetByAppWidgetId(appWidgetId)
    } yield widget map toWidget).resolve[PersistenceServiceException]

  def fetchWidgetsByMoment(momentId: Int) =
    (for {
      widgets <- widgetRepository.fetchWidgetsByMoment(momentId)
    } yield widgets map toWidget).resolve[PersistenceServiceException]

  def fetchWidgets() =
    (for {
      widgetItems <- widgetRepository.fetchWidgets()
    } yield widgetItems map toWidget).resolve[PersistenceServiceException]

  def findWidgetById(widgetId: Int) =
    (for {
      maybeWidget <- widgetRepository.findWidgetById(widgetId)
    } yield maybeWidget map toWidget).resolve[PersistenceServiceException]

  def updateWidget(request: UpdateWidgetRequest) =
    (for {
      updated <- widgetRepository.updateWidget(toRepositoryWidget(request))
    } yield updated).resolve[PersistenceServiceException]

  def updateWidgets(request: UpdateWidgetsRequest) =
    (for {
      updated <- widgetRepository.updateWidgets(request.updateWidgetRequests map toRepositoryWidget)
    } yield updated).resolve[PersistenceServiceException]
}
