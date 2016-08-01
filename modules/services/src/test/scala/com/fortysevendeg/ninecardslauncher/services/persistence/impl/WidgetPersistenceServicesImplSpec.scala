package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.WidgetEntity
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServiceException
import com.fortysevendeg.ninecardslauncher.services.persistence.data.WidgetPersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task


trait WidgetPersistenceServicesSpecification
  extends Specification
  with DisjunctionMatchers {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with WidgetPersistenceServicesData {

    mockWidgetRepository.addWidget(repoWidgetData) returns Service(Task(Result.answer(repoWidget)))

    mockWidgetRepository.addWidgets(any) returns Service(Task(Result.answer(seqRepoWidget)))

    mockWidgetRepository.deleteWidgets() returns Service(Task(Result.answer(items)))

    mockWidgetRepository.deleteWidget(repoWidget) returns Service(Task(Result.answer(item)))

    mockWidgetRepository.deleteWidgets(where = s"${WidgetEntity.momentId} = $momentId") returns Service(Task(Result.answer(items)))

    mockWidgetRepository.fetchWidgets() returns Service(Task(Result.answer(seqRepoWidget)))

    mockWidgetRepository.fetchWidgetByAppWidgetId(appWidgetId) returns Service(Task(Result.answer(Some(repoWidget))))

    mockWidgetRepository.fetchWidgetsByMoment(momentId) returns Service(Task(Result.answer(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = $momentId") returns Service(Task(Result.answer(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${momentId + 1}") returns Service(Task(Result.answer(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${momentId + 2}") returns Service(Task(Result.answer(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${momentId + 3}") returns Service(Task(Result.answer(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${momentId + 4}") returns Service(Task(Result.answer(seqRepoWidget)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = $nonExistentMomentId") returns Service(Task(Result.answer(Seq.empty)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${None.orNull}") returns Service(Task(Result.answer(Seq.empty)))

    mockWidgetRepository.findWidgetById(widgetId) returns Service(Task(Result.answer(Option(repoWidget))))

    mockWidgetRepository.findWidgetById(nonExistentWidgetId) returns Service(Task(Result.answer(None)))

    mockWidgetRepository.updateWidget(repoWidget) returns Service(Task(Result.answer(item)))

    mockWidgetRepository.updateWidgets(seqRepoWidget) returns Service(Task(Result.answer(item to items)))

  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with WidgetPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockWidgetRepository.addWidget(repoWidgetData) returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.addWidgets(any) returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.deleteWidgets() returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.deleteWidget(repoWidget) returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.deleteWidgets(where = s"${WidgetEntity.momentId} = $momentId") returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.fetchWidgetByAppWidgetId(appWidgetId) returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.fetchWidgetsByMoment(momentId) returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.fetchWidgets() returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = $momentId") returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = $nonExistentMomentId") returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.fetchWidgets(where = s"${WidgetEntity.momentId} = ${None.orNull}") returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.findWidgetById(widgetId) returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.findWidgetById(nonExistentWidgetId) returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.updateWidget(repoWidget) returns Service(Task(Result.errata(exception)))

    mockWidgetRepository.updateWidgets(seqRepoWidget) returns Service(Task(Result.errata(exception)))
  }

}

class WidgetPersistenceServicesImplSpec extends WidgetPersistenceServicesSpecification{

  "addWidget" should {

    "return a Widget for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addWidget(createAddWidgetRequest()).run.run

      result must beLike {
        case Answer(widget) =>
          widget.id shouldEqual widgetId
          widget.packageName shouldEqual packageName
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addWidget(createAddWidgetRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "addWidgets" should {

    "return Unit for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addWidgets(Seq(createAddWidgetRequest())).run.run

      result must beLike {
        case Answer(widgets) =>
          widgets shouldEqual seqWidget
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addWidgets(Seq(createAddWidgetRequest())).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteAllWidgets" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllWidgets().run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllWidgets().run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteWidget" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteWidget(createDeleteWidgetRequest(widget = servicesWidget)).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteWidget(createDeleteWidgetRequest(widget = servicesWidget)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteWidgetsByMoment" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteWidgetsByMoment(momentId).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteWidgetsByMoment(momentId).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchWidgetByAppWidgetId" should {

    "return a Widget elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgetByAppWidgetId(createFetchWidgetByAppWidgetIdRequest(appWidgetId)).run.run

      result must beLike {
        case Answer(cards) =>
          cards.size shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgetByAppWidgetId(createFetchWidgetByAppWidgetIdRequest(appWidgetId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchWidgetsByMoment" should {

    "return a list of Widget elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgetsByMoment(createFetchWidgetsByMomentRequest(momentId)).run.run

      result must beLike {
        case Answer(cards) =>
          cards.size shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgetsByMoment(createFetchWidgetsByMomentRequest(momentId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchWidgets" should {

    "return a list of Widget elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgets.run.run

      result must beLike {
        case Answer(collections) =>
          collections.size shouldEqual seqWidget.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchWidgets.run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "findWidgetById" should {

    "return a Widget for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findWidgetById(createFindWidgetByIdRequest(id = widgetId)).run.run

      result must beLike {
        case Answer(maybeWidget) =>
          maybeWidget must beSome[Widget].which { widget =>
            widget.id shouldEqual widgetId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findWidgetById(createFindWidgetByIdRequest(id = nonExistentWidgetId)).run.run

      result must beLike {
        case Answer(maybeWidget) =>
          maybeWidget must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findWidgetById(createFindWidgetByIdRequest(id = widgetId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateWidget" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateWidget(createUpdateWidgetRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateWidget(createUpdateWidgetRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateWidgets" should {

    "return the sequence with the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateWidgets(createUpdateWidgetsRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual (item to items)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateWidgets(createUpdateWidgetsRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }
}
