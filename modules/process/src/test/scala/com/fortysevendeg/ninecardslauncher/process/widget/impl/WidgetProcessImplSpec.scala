package com.fortysevendeg.ninecardslauncher.process.widget.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.process.widget.AppWidgetException
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

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
      CatsService(Task(Xor.right(servicesWidgetOption)))

    mockPersistenceServices.findWidgetById(nonExistentWidgetId) returns
      CatsService(Task(Xor.right(None)))

  }

  trait ValidGetWidgetByAppWidgetIdPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.fetchWidgetByAppWidgetId(appWidgetId) returns
      CatsService(Task(Xor.right(servicesWidgetOption)))

    mockPersistenceServices.fetchWidgetByAppWidgetId(nonExistentAppWidgetId) returns
      CatsService(Task(Xor.right(None)))

  }

  trait ValidGetWidgetsByMomentPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.fetchWidgetsByMoment(momentId) returns
      CatsService(Task(Xor.right(seqServicesWidget)))

    mockPersistenceServices.fetchWidgetsByMoment(nonExistentMomentId) returns
      CatsService(Task(Xor.right(Seq.empty)))

  }

  trait ValidUpdateWidgetPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(any) returns CatsService(Task(Xor.right(servicesWidgetOption)))
    mockPersistenceServices.updateWidget(any) returns CatsService(Task(Xor.right(widgetId)))

  }

  trait InvalidFindWidgetPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(any) returns CatsService(Task(Xor.right(None)))

  }

  trait ErrorFindWidgetPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorUpdateWidgetPersistenceServicesResponses
    extends WidgetProcessImplData {

    self: WidgetProcessScope =>

    mockPersistenceServices.findWidgetById(any) returns CatsService(Task(Xor.right(servicesWidgetOption)))
    mockPersistenceServices.updateWidget(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

}

class WidgetProcessImplSpec
  extends WidgetProcessImplSpecification
    with WidgetProcessImplData {

  "getWidgets" should {

    "returns a sequence of widgets for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgets returns CatsService(Task(Xor.right(seqServicesWidget)))
        mockPersistenceServices.addWidgets(any) returns CatsService(Task(Xor.right(widgets)))

        val widgets: Seq[Widget] = Seq.empty

        val result = widgetProcess.getWidgets.value.run
        result must beLike {
          case Xor.Right(resultSeqWidget) =>
            resultSeqWidget.size shouldEqual seqServicesWidget.size
            resultSeqWidget map (_.packageName) shouldEqual seqServicesWidget.map(_.packageName)
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgets returns CatsService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.addWidgets(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.getWidgets.value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "getWidgetById" should {

    "returns a widget for a valid request" in
      new WidgetProcessScope with ValidGetWidgetByIdPersistenceServicesResponses {
        val result = widgetProcess.getWidgetById(widgetId).value.run
        result must beLike {
          case Xor.Right(resultWidget) => resultWidget must beSome.which { widget =>
            widget.packageName shouldEqual widget.packageName
          }
        }
      }

    "returns None for a valid request if the widgetId doesn't exist" in
      new WidgetProcessScope with ValidGetWidgetByIdPersistenceServicesResponses {
        val result = widgetProcess.getWidgetById(nonExistentWidgetId).value.run
        result must beLike {
          case Xor.Right(resultWidget) => resultWidget shouldEqual None
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(widgetId) returns
          CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.getWidgetById(widgetId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "getWidgetByAppWidgetId" should {

    "returns a widget for a valid request" in
      new WidgetProcessScope with ValidGetWidgetByAppWidgetIdPersistenceServicesResponses {
        val result = widgetProcess.getWidgetByAppWidgetId(appWidgetId).value.run
        result must beLike {
          case Xor.Right(resultWidget) => resultWidget must beSome.which { widget =>
            widget.packageName shouldEqual widget.packageName
          }
        }
      }

    "returns None for a valid request if the appWidgetId doesn't exist" in
      new WidgetProcessScope with ValidGetWidgetByAppWidgetIdPersistenceServicesResponses {
        val result = widgetProcess.getWidgetByAppWidgetId(nonExistentAppWidgetId).value.run
        result must beLike {
          case Xor.Right(resultWidget) => resultWidget shouldEqual None
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetByAppWidgetId(appWidgetId) returns
          CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.getWidgetByAppWidgetId(appWidgetId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "getWidgetsByMoment" should {

    "returns a sequence of widgets for a valid request" in
      new WidgetProcessScope with ValidGetWidgetsByMomentPersistenceServicesResponses {
        val result = widgetProcess.getWidgetsByMoment(momentId).value.run
        result must beLike {
          case Xor.Right(resultWidgets) =>
            resultWidgets map (_.packageName) shouldEqual (seqWidget map (_.packageName))
        }
      }

    "returns None for a valid request if the momentId doesn't exist" in
      new WidgetProcessScope with ValidGetWidgetsByMomentPersistenceServicesResponses {
        val result = widgetProcess.getWidgetsByMoment(nonExistentMomentId).value.run
        result must beLike {
          case Xor.Right(resultWidgets) => resultWidgets shouldEqual Seq.empty
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetsByMoment(momentId) returns
          CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.getWidgetsByMoment(momentId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "addWidget" should {

    "returns a the widget added for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidget(any) returns CatsService(Task(Xor.right(servicesWidget)))

        val result = widgetProcess.addWidget(addWidgetRequest).value.run
        result must beLike {
          case Xor.Right(resultWidget) =>
            resultWidget shouldEqual widget
        }
      }

    "returns a WidgetException if the service throws a exception adding the new widget" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidget(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.addWidget(addWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "addWidgets" should {

    "returns the widgets added for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidgets(any) returns CatsService(Task(Xor.right(seqServicesWidget)))

        val result = widgetProcess.addWidgets(seqAddWidgetRequest).value.run
        result must beLike {
          case Xor.Right(resultWidgets) =>
            resultWidgets shouldEqual seqWidget
        }
      }

    "returns a WidgetException if the service throws a exception adding the new widgets" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidgets(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.addWidgets(seqAddWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "moveWidget" should {

    "returns a the updated widget for a valid request" in
      new WidgetProcessScope with ValidUpdateWidgetPersistenceServicesResponses {
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).value.run
        result must beLike {
          case Xor.Right(resultWidget) =>
            resultWidget shouldEqual moveWidgetResponse
        }
      }

    "returns a WidgetException if the service returns a None finding the widget by Id" in
      new WidgetProcessScope with InvalidFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope with ErrorFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }

    "returns a WidgetException if the service throws a exception updating the widget" in
      new WidgetProcessScope with ErrorUpdateWidgetPersistenceServicesResponses {
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "resizeWidget" should {

    "returns a the updated widget for a valid request" in
      new WidgetProcessScope with ValidUpdateWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beLike {
          case Xor.Right(resultWidget) =>
            resultWidget shouldEqual resizeWidgetResponse
        }
      }

    "returns a WidgetException if the service returns a None finding the widget by Id" in
      new WidgetProcessScope with InvalidFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope with ErrorFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }

    "returns a WidgetException if the service throws a exception updating the widget" in
      new WidgetProcessScope with ErrorUpdateWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "deleteAllWidgets" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns CatsService(Task(Xor.right(items)))

        val result = widgetProcess.deleteAllWidgets().value.run
        result must beLike {
          case Xor.Right(resultWidgets) =>
            resultWidgets shouldEqual ((): Unit)
        }
      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.deleteAllWidgets().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "deleteWidget" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns CatsService(Task(Xor.right(servicesWidgetOption)))
        mockPersistenceServices.deleteWidget(any) returns CatsService(Task(Xor.right(items)))

        val result = widgetProcess.deleteWidget(widgetId).value.run
        result must beLike {
          case Xor.Right(resultWidget) =>
            resultWidget shouldEqual ((): Unit)
        }
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope with ErrorFindWidgetPersistenceServicesResponses {
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns CatsService(Task(Xor.right(servicesWidgetOption)))
        mockPersistenceServices.deleteWidget(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.deleteWidget(widgetId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

  "deleteWidgetsByMoment" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteWidgetsByMoment(any) returns CatsService(Task(Xor.right(items)))

        val result = widgetProcess.deleteWidgetsByMoment(momentId).value.run
        result must beLike {
          case Xor.Right(resultWidget) =>
            resultWidget shouldEqual ((): Unit)
        }
      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteWidgetsByMoment(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

        val result = widgetProcess.deleteWidgetsByMoment(momentId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppWidgetException]
        }
      }
  }

}
