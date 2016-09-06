package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.WidgetEntity
import com.fortysevendeg.ninecardslauncher.services.persistence.data.WidgetPersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

import scalaz.concurrent.Task


trait WidgetPersistenceServicesSpecification
  extends Specification
    with DisjunctionMatchers {

  trait WidgetPersistenceServicesScope
    extends RepositoryServicesScope
    with WidgetPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class WidgetPersistenceServicesImplSpec extends WidgetPersistenceServicesSpecification {

  "addWidget" should {

    "return a Widget for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidget(any) returns TaskService(Task(Xor.right(repoWidget)))
      val result = persistenceServices.addWidget(createAddWidgetRequest()).value.run

      result must beLike {
        case Xor.Right(widget) =>
          widget.id shouldEqual widgetId
          widget.packageName shouldEqual packageName
      }
    }

    "return Unit for a valid request when AppWidgetId is None" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidget(any) returns TaskService(Task(Xor.right(repoWidgetNone)))
      val result = persistenceServices.addWidget(createAddWidgetRequest()).value.run

      result must beLike {
        case Xor.Right(widget) =>
          widget.id shouldEqual widgetId
          widget.packageName shouldEqual packageName
          widget.appWidgetId shouldEqual None
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {
      mockWidgetRepository.addWidget(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.addWidget(createAddWidgetRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "addWidgets" should {

    "return Unit for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Xor.right(seqRepoWidget)))
      val result = persistenceServices.addWidgets(Seq(createAddWidgetRequest())).value.run
      result shouldEqual Xor.Right(seqWidget)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.addWidgets(Seq(createAddWidgetRequest())).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteAllWidgets" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets() returns TaskService(Task(Xor.right(items)))
      val result = persistenceServices.deleteAllWidgets().value.run
      result shouldEqual Xor.Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets() returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteAllWidgets().value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteWidget" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidget(any) returns TaskService(Task(Xor.right(item)))
      val result = persistenceServices.deleteWidget(createDeleteWidgetRequest(widget = servicesWidget)).value.run
      result shouldEqual Xor.Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidget(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteWidget(createDeleteWidgetRequest(widget = servicesWidget)).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteWidgetsByMoment" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets(any) returns TaskService(Task(Xor.right(items)))
      val result = persistenceServices.deleteWidgetsByMoment(momentId).value.run
      result shouldEqual Xor.Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteWidgetsByMoment(momentId).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchWidgetByAppWidgetId" should {

    "return a Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetByAppWidgetId(any) returns TaskService(Task(Xor.right(Some(repoWidget))))
      val result = persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).value.run
      result must beLike {
        case Xor.Right(cards) => cards.size shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetByAppWidgetId(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchWidgetsByMoment" should {

    "return a list of Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetsByMoment(any) returns TaskService(Task(Xor.right(seqRepoWidget)))
      val result = persistenceServices.fetchWidgetsByMoment(momentId).value.run

      result must beLike {
        case Xor.Right(cards) => cards.size shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetsByMoment(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchWidgetsByMoment(momentId).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchWidgets" should {

    "return a list of Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgets() returns TaskService(Task(Xor.right(seqRepoWidget)))
      val result = persistenceServices.fetchWidgets.value.run

      result must beLike {
        case Xor.Right(collections) => collections.size shouldEqual seqWidget.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgets() returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchWidgets.value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "findWidgetById" should {

    "return a Widget for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(Task(Xor.right(Option(repoWidget))))
      val result = persistenceServices.findWidgetById(widgetId).value.run

      result must beLike {
        case Xor.Right(maybeWidget) =>
          maybeWidget must beSome[Widget].which { widget =>
            widget.id shouldEqual widgetId
          }
      }
    }

    "return None when a non-existent id is given" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(Task(Xor.right(None)))
      val result = persistenceServices.findWidgetById(nonExistentWidgetId).value.run
      result shouldEqual Xor.Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.findWidgetById(widgetId).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "updateWidget" should {

    "return the number of elements updated for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidget(any) returns TaskService(Task(Xor.right(item)))
      val result = persistenceServices.updateWidget(createUpdateWidgetRequest()).value.run
      result shouldEqual Xor.Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidget(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.updateWidget(createUpdateWidgetRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "updateWidgets" should {

    "return the sequence with the number of elements updated for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidgets(any) returns TaskService(Task(Xor.right(item to items)))
      val result = persistenceServices.updateWidgets(createUpdateWidgetsRequest()).value.run
      result shouldEqual Xor.Right(item to items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidgets(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.updateWidgets(createUpdateWidgetsRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }
}
