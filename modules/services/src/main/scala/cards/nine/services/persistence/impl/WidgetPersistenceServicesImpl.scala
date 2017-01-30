/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.persistence.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Widget, WidgetData}
import cards.nine.repository.provider.WidgetEntity
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions

trait WidgetPersistenceServicesImpl extends PersistenceServices {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addWidget(widget: WidgetData) =
    (for {
      widgetAdded <- widgetRepository.addWidget(toRepositoryWidgetData(widget))
    } yield toWidget(widgetAdded)).resolve[PersistenceServiceException]

  def addWidgets(widgets: Seq[WidgetData]) =
    (for {
      widgetsAdded <- widgetRepository.addWidgets(widgets map toRepositoryWidgetData)
    } yield widgetsAdded map toWidget).resolve[PersistenceServiceException]

  def deleteAllWidgets() =
    (for {
      deleted <- widgetRepository.deleteWidgets()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteWidget(widget: Widget) =
    (for {
      deleted <- widgetRepository.deleteWidget(toRepositoryWidget(widget))
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

  def updateWidget(widget: Widget) =
    (for {
      updated <- widgetRepository.updateWidget(toRepositoryWidget(widget))
    } yield updated).resolve[PersistenceServiceException]

  def updateWidgets(widgets: Seq[Widget]) =
    (for {
      updated <- widgetRepository.updateWidgets(widgets map toRepositoryWidget)
    } yield updated).resolve[PersistenceServiceException]
}
