package cards.nine.process.widget.impl

import cards.nine.commons.services.TaskService
import cards.nine.process.widget.AppWidgetException
import cards.nine.services.persistence.models.Widget
import cards.nine.services.persistence.{PersistenceServiceException, PersistenceServices}
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import cards.nine.commons.test.TaskServiceTestOps._
import cats.syntax.either._

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

}

class WidgetProcessImplSpec
  extends WidgetProcessImplSpecification
    with WidgetProcessImplData {

  "getWidgets" should {

    "returns a sequence of widgets for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgets returns TaskService(Task(Either.right(seqServicesWidget)))
        mockPersistenceServices.addWidgets(any) returns TaskService(Task(Either.right(widgets)))

        val widgets: Seq[Widget] = Seq.empty

        val result = widgetProcess.getWidgets.value.run
        result must beLike {
          case Right(resultSeqWidget) =>
            resultSeqWidget.size shouldEqual seqServicesWidget.size
            resultSeqWidget map (_.packageName) shouldEqual seqServicesWidget.map(_.packageName)
        }
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgets returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.addWidgets(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.getWidgets.value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "getWidgetById" should {

    "returns a widget for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(widgetId) returns
          TaskService(Task(Either.right(servicesWidgetOption)))
        val result = widgetProcess.getWidgetById(widgetId).value.run
        result must beLike {
          case Right(resultWidget) => resultWidget must beSome.which { widget =>
            widget.packageName shouldEqual widget.packageName
          }
        }
      }

    "returns None for a valid request if the widgetId doesn't exist" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(nonExistentWidgetId) returns TaskService(Task(Either.right(None)))
        val result = widgetProcess.getWidgetById(nonExistentWidgetId).value.run
        result shouldEqual Right(None)
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(widgetId) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.getWidgetById(widgetId).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "getWidgetByAppWidgetId" should {

    "returns a widget for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetByAppWidgetId(appWidgetId) returns
          TaskService(Task(Either.right(servicesWidgetOption)))

        val result = widgetProcess.getWidgetByAppWidgetId(appWidgetId).value.run
        result must beLike {
          case Right(resultWidget) => resultWidget must beSome.which { widget =>
            widget.packageName shouldEqual widget.packageName
          }
        }
      }

    "returns None for a valid request if the appWidgetId doesn't exist" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetByAppWidgetId(nonExistentAppWidgetId) returns TaskService(Task(Either.right(None)))
        val result = widgetProcess.getWidgetByAppWidgetId(nonExistentAppWidgetId).value.run
        result shouldEqual Right(None)
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetByAppWidgetId(appWidgetId) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.getWidgetByAppWidgetId(appWidgetId).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "getWidgetsByMoment" should {

    "returns a sequence of widgets for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetsByMoment(momentId) returns
          TaskService(Task(Either.right(seqServicesWidget)))
        val result = widgetProcess.getWidgetsByMoment(momentId).value.run
        result must beLike {
          case Right(resultWidgets) =>
            resultWidgets map (_.packageName) shouldEqual (seqWidget map (_.packageName))
        }
      }

    "returns None for a valid request if the momentId doesn't exist" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetsByMoment(nonExistentMomentId) returns TaskService(Task(Either.right(Seq.empty)))
        val result = widgetProcess.getWidgetsByMoment(nonExistentMomentId).value.run
        result shouldEqual Right(Seq.empty)
      }

    "returns a WidgetException if the service throws a exception" in
      new WidgetProcessScope {

        mockPersistenceServices.fetchWidgetsByMoment(momentId) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.getWidgetsByMoment(momentId).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "addWidget" should {

    "returns a the widget added for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidget(any) returns TaskService(Task(Either.right(servicesWidget)))
        val result = widgetProcess.addWidget(addWidgetRequest).value.run
        result shouldEqual Right(widget)
      }

    "returns a WidgetException if the service throws a exception adding the new widget" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidget(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.addWidget(addWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "addWidgets" should {

    "returns the widgets added for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidgets(any) returns TaskService(Task(Either.right(seqServicesWidget)))
        val result = widgetProcess.addWidgets(seqAddWidgetRequest).value.run
        result shouldEqual Right(seqWidget)
      }

    "returns a WidgetException if the service throws a exception adding the new widgets" in
      new WidgetProcessScope {

        mockPersistenceServices.addWidgets(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.addWidgets(seqAddWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "moveWidget" should {

    "returns a the updated widget for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(servicesWidgetOption)))
        mockPersistenceServices.updateWidget(any) returns TaskService(Task(Either.right(widgetId)))
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).value.run
        result shouldEqual Right(moveWidgetResponse)
      }

    "returns a WidgetException if the service returns a None finding the widget by Id" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(None)))
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }

    "returns a WidgetException if the service throws a exception updating the widget" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(servicesWidgetOption)))
        mockPersistenceServices.updateWidget(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.moveWidget(widgetId, moveWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "resizeWidget" should {

    "returns a the updated widget for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(servicesWidgetOption)))
        mockPersistenceServices.updateWidget(any) returns TaskService(Task(Either.right(widgetId)))
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result shouldEqual Right(resizeWidgetResponse)
      }

    "returns a WidgetException if the service returns a None finding the widget by Id" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(None)))
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }

    "returns a WidgetException if the service throws a exception updating the widget" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(servicesWidgetOption)))
        mockPersistenceServices.updateWidget(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "updateAppWidgetId"  should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(servicesWidgetOption)))
        mockPersistenceServices.updateWidget(any) returns TaskService(Task(Either.right(widgetId)))

        val result = widgetProcess.updateAppWidgetId(widgetId,appWidgetId).value.run
        result must beLike {
          case Right(appWidget) =>
            appWidget.appWidgetId shouldEqual Some(appWidgetId)
        }

      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(servicesWidgetOption)))
        mockPersistenceServices.updateWidget(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.updateAppWidgetId(widgetId,appWidgetId).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "deleteAllWidgets" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Either.right(items)))
        val result = widgetProcess.deleteAllWidgets().value.run
        result shouldEqual Right((): Unit)

      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.deleteAllWidgets().value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "deleteWidget" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(servicesWidgetOption)))
        mockPersistenceServices.deleteWidget(any) returns TaskService(Task(Either.right(items)))
        val result = widgetProcess.deleteWidget(widgetId).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a WidgetException if the service throws a exception finding the widget by Id" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.resizeWidget(widgetId, resizeWidgetRequest).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.findWidgetById(any) returns TaskService(Task(Either.right(servicesWidgetOption)))
        mockPersistenceServices.deleteWidget(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.deleteWidget(widgetId).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

  "deleteWidgetsByMoment" should {

    "returns a successful answer for a valid request" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteWidgetsByMoment(any) returns TaskService(Task(Either.right(items)))
        val result = widgetProcess.deleteWidgetsByMoment(momentId).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a WidgetException if the service throws a exception deleting the moments" in
      new WidgetProcessScope {

        mockPersistenceServices.deleteWidgetsByMoment(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = widgetProcess.deleteWidgetsByMoment(momentId).value.run
        result must beAnInstanceOf[Left[AppWidgetException, _]]
      }
  }

}
