package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.syntax.either._
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.services.persistence.data.WidgetPersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification



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

      mockWidgetRepository.addWidget(any) returns TaskService(Task(Either.right(repoWidget)))
      val result = persistenceServices.addWidget(createAddWidgetRequest()).value.run

      result must beLike {
        case Right(widget) =>
          widget.id shouldEqual widgetId
          widget.packageName shouldEqual packageName
      }
    }

    "return Unit for a valid request when AppWidgetId is None" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidget(any) returns TaskService(Task(Either.right(repoWidgetNone)))
      val result = persistenceServices.addWidget(createAddWidgetRequest()).value.run

      result must beLike {
        case Right(widget) =>
          widget.id shouldEqual widgetId
          widget.packageName shouldEqual packageName
          widget.appWidgetId shouldEqual None
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {
      mockWidgetRepository.addWidget(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addWidget(createAddWidgetRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "addWidgets" should {

    "return Unit for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.addWidgets(Seq(createAddWidgetRequest())).value.run
      result shouldEqual Right(seqWidget)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addWidgets(Seq(createAddWidgetRequest())).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAllWidgets" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets() returns TaskService(Task(Either.right(items)))
      val result = persistenceServices.deleteAllWidgets().value.run
      result shouldEqual Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAllWidgets().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteWidget" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidget(any) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.deleteWidget(createDeleteWidgetRequest(widget = servicesWidget)).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidget(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteWidget(createDeleteWidgetRequest(widget = servicesWidget)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteWidgetsByMoment" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets(any) returns TaskService(Task(Either.right(items)))
      val result = persistenceServices.deleteWidgetsByMoment(momentId).value.run
      result shouldEqual Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteWidgetsByMoment(momentId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchWidgetByAppWidgetId" should {

    "return a Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetByAppWidgetId(any) returns TaskService(Task(Either.right(Some(repoWidget))))
      val result = persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).value.run
      result must beLike {
        case Right(cards) => cards.size shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetByAppWidgetId(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchWidgetsByMoment" should {

    "return a list of Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetsByMoment(any) returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.fetchWidgetsByMoment(momentId).value.run

      result must beLike {
        case Right(cards) => cards.size shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetsByMoment(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchWidgetsByMoment(momentId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchWidgets" should {

    "return a list of Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgets() returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.fetchWidgets.value.run

      result must beLike {
        case Right(collections) => collections.size shouldEqual seqWidget.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgets() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchWidgets.value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "findWidgetById" should {

    "return a Widget for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(Task(Either.right(Option(repoWidget))))
      val result = persistenceServices.findWidgetById(widgetId).value.run

      result must beLike {
        case Right(maybeWidget) =>
          maybeWidget must beSome[Widget].which { widget =>
            widget.id shouldEqual widgetId
          }
      }
    }

    "return None when a non-existent id is given" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findWidgetById(nonExistentWidgetId).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findWidgetById(widgetId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateWidget" should {

    "return the number of elements updated for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidget(any) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.updateWidget(createUpdateWidgetRequest()).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidget(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateWidget(createUpdateWidgetRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateWidgets" should {

    "return the sequence with the number of elements updated for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidgets(any) returns TaskService(Task(Either.right(item to items)))
      val result = persistenceServices.updateWidgets(createUpdateWidgetsRequest()).value.run
      result shouldEqual Right(item to items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidgets(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateWidgets(createUpdateWidgetsRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }
}
