package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
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

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with WidgetPersistenceServicesData {

    mockWidgetRepository.addWidget(repoWidgetData) returns CatsService(Task(Xor.right(repoWidget)))

    mockWidgetRepository.addWidgets(any) returns CatsService(Task(Xor.right(seqRepoWidget)))

    mockWidgetRepository.deleteWidgets() returns CatsService(Task(Xor.right(items)))

    mockWidgetRepository.deleteWidget(repoWidget) returns CatsService(Task(Xor.right(item)))

    mockWidgetRepository.deleteWidgets(where = s"${WidgetEntity.momentId} = $momentId") returns CatsService(Task(Xor.right(items)))

    mockWidgetRepository.fetchWidgets() returns CatsService(Task(Xor.right(seqRepoWidget)))

    mockWidgetRepository.fetchWidgetByAppWidgetId(appWidgetId) returns CatsService(Task(Xor.right(Some(repoWidget))))

    mockWidgetRepository.fetchWidgetsByMoment(momentId) returns CatsService(Task(Xor.right(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = $momentId") returns CatsService(Task(Xor.right(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${momentId + 1}") returns CatsService(Task(Xor.right(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${momentId + 2}") returns CatsService(Task(Xor.right(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${momentId + 3}") returns CatsService(Task(Xor.right(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${momentId + 4}") returns CatsService(Task(Xor.right(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = $nonExistentMomentId") returns CatsService(Task(Xor.right(Seq.empty)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${None.orNull}") returns CatsService(Task(Xor.right(Seq.empty)))

    mockWidgetRepository.findWidgetById(widgetId) returns CatsService(Task(Xor.right(Option(repoWidget))))

    mockWidgetRepository.findWidgetById(nonExistentWidgetId) returns CatsService(Task(Xor.right(None)))

    mockWidgetRepository.updateWidget(repoWidget) returns CatsService(Task(Xor.right(item)))

    mockWidgetRepository.updateWidgets(seqRepoWidget) returns CatsService(Task(Xor.right(item to items)))

  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with WidgetPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockWidgetRepository.addWidget(repoWidgetData) returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.addWidgets(any) returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.deleteWidgets() returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.deleteWidget(repoWidget) returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.deleteWidgets(where = s"${WidgetEntity.momentId} = $momentId") returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.fetchWidgetByAppWidgetId(appWidgetId) returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.fetchWidgetsByMoment(momentId) returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.fetchWidgets() returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = $momentId") returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = $nonExistentMomentId") returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${None.orNull}") returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.findWidgetById(widgetId) returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.findWidgetById(nonExistentWidgetId) returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.updateWidget(repoWidget) returns CatsService(Task(Xor.left(exception)))

    mockWidgetRepository.updateWidgets(seqRepoWidget) returns CatsService(Task(Xor.left(exception)))
  }

}

class WidgetPersistenceServicesImplSpec extends WidgetPersistenceServicesSpecification {

  "addWidget" should {

    "return a Widget for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addWidget(createAddWidgetRequest()).value.run

      result must beLike {
        case Xor.Right(widget) =>
          widget.id shouldEqual widgetId
          widget.packageName shouldEqual packageName
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addWidget(createAddWidgetRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "addWidgets" should {

    "return Unit for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addWidgets(Seq(createAddWidgetRequest())).value.run

      result must beLike {
        case Xor.Right(widgets) => widgets shouldEqual seqWidget
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addWidgets(Seq(createAddWidgetRequest())).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllWidgets" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllWidgets().value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllWidgets().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteWidget" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteWidget(createDeleteWidgetRequest(widget = servicesWidget)).value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteWidget(createDeleteWidgetRequest(widget = servicesWidget)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteWidgetsByMoment" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteWidgetsByMoment(momentId).value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteWidgetsByMoment(momentId).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchWidgetByAppWidgetId" should {

    "return a Widget elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).value.run

      result must beLike {
        case Xor.Right(cards) => cards.size shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchWidgetsByMoment" should {

    "return a list of Widget elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgetsByMoment(momentId).value.run

      result must beLike {
        case Xor.Right(cards) => cards.size shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgetsByMoment(momentId).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchWidgets" should {

    "return a list of Widget elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgets.value.run

      result must beLike {
        case Xor.Right(collections) => collections.size shouldEqual seqWidget.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgets.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findWidgetById" should {

    "return a Widget for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findWidgetById(widgetId).value.run

      result must beLike {
        case Xor.Right(maybeWidget) =>
          maybeWidget must beSome[Widget].which { widget =>
            widget.id shouldEqual widgetId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findWidgetById(nonExistentWidgetId).value.run

      result must beLike {
        case Xor.Right(maybeWidget) => maybeWidget must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findWidgetById(widgetId).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateWidget" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateWidget(createUpdateWidgetRequest()).value.run

      result must beLike {
        case Xor.Right(updated) => updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateWidget(createUpdateWidgetRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateWidgets" should {

    "return the sequence with the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateWidgets(createUpdateWidgetsRequest()).value.run

      result must beLike {
        case Xor.Right(updated) => updated shouldEqual (item to items)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateWidgets(createUpdateWidgetsRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }
}
