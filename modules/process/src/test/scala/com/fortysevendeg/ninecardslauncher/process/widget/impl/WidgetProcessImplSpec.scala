package com.fortysevendeg.ninecardslauncher.process.widget.impl

import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.widget.WidgetExceptionImpl
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait WidgetProcessImplSpecification
  extends Specification
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")

  trait WidgetProcessScope
    extends Scope {

    val mockPersistenceServices = mock[PersistenceServices]

    val widgetProcess = new WidgetProcessImpl(
      persistenceServices = mockPersistenceServices)

  }

  trait ValidGetWidgetByIdPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(widgetId) returns
      Service(Task(Result.answer(servicesWidgetOption)))

    mockPersistenceServices.findWidgetById(nonExistentWidgetId) returns
      Service(Task(Result.answer(None)))

  }

  trait ValidGetWidgetByAppWidgetIdPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.fetchWidgetByAppWidgetId(appWidgetId) returns
      Service(Task(Result.answer(servicesWidgetOption)))

    mockPersistenceServices.fetchWidgetByAppWidgetId(nonExistentAppWidgetId) returns
      Service(Task(Result.answer(None)))

  }

  trait ValidGetWidgetsByMomentPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.fetchWidgetsByMoment(momentId) returns
      Service(Task(Result.answer(seqServicesWidget)))

    mockPersistenceServices.fetchWidgetsByMoment(nonExistentMomentId) returns
      Service(Task(Result.answer(Seq.empty)))

  }

  trait ValidUpdateWidgetPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(any) returns Service(Task(Result.answer(servicesWidgetOption)))
    mockPersistenceServices.updateWidget(any) returns Service(Task(Result.answer(widgetId)))

  }

  trait InvalidFindWidgetPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(any) returns Service(Task(Result.answer(None)))

  }

  trait ErrorFindWidgetPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorUpdateWidgetPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(any) returns Service(Task(Result.answer(servicesWidgetOption)))
    mockPersistenceServices.updateWidget(any) returns Service(Task(Errata(persistenceServiceException)))

  }
}

class WidgetProcessImplSpec
  extends WidgetProcessImplSpecification
  with WidgetProcessImplData {

  "getWidgets" should {

    "returns a sequence of widgets for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgets returns Service(Task(Result.answer(seqServicesWidget)))
        mockPersistenceServices.addWidgets(any) returns Service(Task(Result.answer(widgets)))

        val widgets: Seq[Widget] = Seq.empty

        val result = widgetProcess.getWidgets.run.run
        result must beLike {
          case Answer(resultSeqWidget) =>
            resultSeqWidget.size shouldEqual seqServicesWidget.size
            resultSeqWidget map (_.packageName) shouldEqual seqServicesWidget.map(_.packageName)
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgets returns Service(Task(Errata(persistenceServiceException)))
        mockPersistenceServices.addWidgets(any) returns Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.getWidgets.run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "getWidgetById" should {

    "returns a widget for a valid request" in
      new WidgetProcessScope with ValidGetWidgetByIdPersistenceServicesResponses {
        val result = widgetProcess.getWidgetById(widgetId).run.run
        result must beLike {
          case Answer(resultWidget) => resultWidget must beSome.which { widget =>
            widget.packageName shouldEqual widget.packageName
          }
        }
      }

    "returns None for a valid request if the widgetId doesn't exist" in
      new WidgetProcessScope with ValidGetWidgetByIdPersistenceServicesResponses {
        val result = widgetProcess.getWidgetById(nonExistentWidgetId).run.run
        result must beLike {
          case Answer(resultWidget) => resultWidget shouldEqual None
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(widgetId) returns
          Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.getWidgetById(widgetId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "getWidgetByAppWidgetId" should {

    "returns a widget for a valid request" in
      new WidgetProcessScope with ValidGetWidgetByAppWidgetIdPersistenceServicesResponses {
        val result = widgetProcess.getWidgetByAppWidgetId(appWidgetId).run.run
        result must beLike {
          case Answer(resultWidget) => resultWidget must beSome.which { widget =>
            widget.packageName shouldEqual widget.packageName
          }
        }
      }

    "returns None for a valid request if the appWidgetId doesn't exist" in
      new WidgetProcessScope with ValidGetWidgetByAppWidgetIdPersistenceServicesResponses {
        val result = widgetProcess.getWidgetByAppWidgetId(nonExistentAppWidgetId).run.run
        result must beLike {
          case Answer(resultWidget) => resultWidget shouldEqual None
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetByAppWidgetId(appWidgetId) returns
          Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.getWidgetByAppWidgetId(appWidgetId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "getWidgetsByMoment" should {

    "returns a sequence of widgets for a valid request" in
      new WidgetProcessScope with ValidGetWidgetsByMomentPersistenceServicesResponses {
        val result = widgetProcess.getWidgetsByMoment(momentId).run.run
        result must beLike {
          case Answer(resultWidgets) =>
            resultWidgets map (_.packageName) shouldEqual (seqWidget map (_.packageName))
        }
      }

    "returns None for a valid request if the momentId doesn't exist" in
      new WidgetProcessScope with ValidGetWidgetsByMomentPersistenceServicesResponses {
        val result = widgetProcess.getWidgetsByMoment(nonExistentMomentId).run.run
        result must beLike {
          case Answer(resultWidgets) => resultWidgets shouldEqual Seq.empty
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetsByMoment(momentId) returns
          Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.getWidgetsByMoment(momentId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "addWidget" should {

    "returns a the widget added for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidget(any) returns Service(Task(Result.answer(servicesWidget)))

        val result = widgetProcess.addWidget(addWidgetRequest).run.run
        result must beLike {
          case Answer(resultWidget) =>
            resultWidget shouldEqual widget
        }
      }

    "returns a WidgetException if the service throws a exception adding the new widget" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidget(any) returns Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.addWidget(addWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "addWidgets" should {

    "returns the widgets added for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidgets(any) returns Service(Task(Result.answer(seqServicesWidget)))

        val result = widgetProcess.addWidgets(seqAddWidgetRequest).run.run
        result must beLike {
          case Answer(resultWidgets) =>
            resultWidgets shouldEqual seqWidget
        }
      }

    "returns a WidgetException if the service throws a exception adding the new widgets" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidgets(any) returns Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.addWidgets(seqAddWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "moveWidget" should {

    "returns a the updated widget for a valid request" in
      new WidgetProcessScope with ValidUpdateWidgetPersistenceServicesResponses {
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).run.run
        result must beLike {
          case Answer(resultWidget) =>
            resultWidget shouldEqual moveWidgetResponse
        }
      }

    "returns a WidgetException if the service returns a None finding the widget by Id" in
      new WidgetProcessScope with InvalidFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope with ErrorFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }

    "returns a WidgetException if the service throws a exception updating the widget" in
      new WidgetProcessScope with ErrorUpdateWidgetPersistenceServicesResponses {
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "resizeWidget" should {

    "returns a the updated widget for a valid request" in
      new WidgetProcessScope with ValidUpdateWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).run.run
        result must beLike {
          case Answer(resultWidget) =>
            resultWidget shouldEqual resizeWidgetResponse
        }
      }

    "returns a WidgetException if the service returns a None finding the widget by Id" in
      new WidgetProcessScope with InvalidFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope with ErrorFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }

    "returns a WidgetException if the service throws a exception updating the widget" in
      new WidgetProcessScope with ErrorUpdateWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "deleteAllWidgets" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns Service(Task(Result.answer(items)))

        val result = widgetProcess.deleteAllWidgets().run.run
        result must beLike {
          case Answer(resultWidgets) =>
            resultWidgets shouldEqual ((): Unit)
        }
      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.deleteAllWidgets().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "deleteWidget" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns Service(Task(Result.answer(servicesWidgetOption)))
        mockPersistenceServices.deleteWidget(any) returns Service(Task(Result.answer(items)))

        val result = widgetProcess.deleteWidget(widgetId).run.run
        result must beLike {
          case Answer(resultWidget) =>
            resultWidget shouldEqual ((): Unit)
        }
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope with ErrorFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns Service(Task(Result.answer(servicesWidgetOption)))
        mockPersistenceServices.deleteWidget(any) returns Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.deleteWidget(widgetId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

  "deleteWidgetsByMoment" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteWidgetsByMoment(any) returns Service(Task(Result.answer(items)))

        val result = widgetProcess.deleteWidgetsByMoment(momentId).run.run
        result must beLike {
          case Answer(resultWidget) =>
            resultWidget shouldEqual ((): Unit)
        }
      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteWidgetsByMoment(any) returns Service(Task(Errata(persistenceServiceException)))

        val result = widgetProcess.deleteWidgetsByMoment(momentId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetExceptionImpl]
          }
        }
      }
  }

}
