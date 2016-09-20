package com.fortysevendeg.repository.widget

import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapperImpl, UriCreator}
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.Widget
import com.fortysevendeg.ninecardslauncher.repository.provider.WidgetEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider._
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.repository._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import com.fortysevendeg.ninecardslauncher.commons.test.repository.{IntDataType, MockCursor, StringDataType}

trait WidgetRepositorySpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait WidgetRepositoryScope
    extends Scope
      with WidgetRepositoryTestData {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val widgetRepository = new WidgetRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]

    uriCreator.parse(any) returns mockUri

    val contentResolverException = new RuntimeException("Irrelevant message")

  }

}

trait WidgetMockCursor
  extends MockCursor
    with WidgetRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, widgetSeq map (_.id), IntDataType),
    (momentId, 1, widgetSeq map (_.data.momentId), IntDataType),
    (packageName, 2, widgetSeq map (_.data.packageName), StringDataType),
    (className, 3, widgetSeq map (_.data.className), StringDataType),
    (appWidgetId, 4, widgetSeq map (_.data.appWidgetId), IntDataType),
    (startX, 5, widgetSeq map (_.data.startX), IntDataType),
    (startY, 6, widgetSeq map (_.data.startY), IntDataType),
    (spanX, 7, widgetSeq map (_.data.spanX), IntDataType),
    (spanY, 8, widgetSeq map (_.data.spanY), IntDataType),
    (widgetType, 9, widgetSeq map (_.data.widgetType), StringDataType),
    (label, 10, widgetSeq map (_.data.label getOrElse javaNull), StringDataType),
    (imagePath, 11, widgetSeq map (_.data.imagePath getOrElse javaNull), StringDataType),
    (intent, 12, widgetSeq map (_.data.intent getOrElse javaNull), StringDataType))

  prepareCursor[Widget](widgetSeq.size, cursorData)
}

trait EmptyWidgetMockCursor
  extends MockCursor
    with WidgetRepositoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.id, 0, Seq.empty, IntDataType),
    (momentId, 1, Seq.empty, IntDataType),
    (packageName, 2, Seq.empty, StringDataType),
    (className, 3, Seq.empty, StringDataType),
    (appWidgetId, 4, Seq.empty, StringDataType),
    (startX, 5, Seq.empty, IntDataType),
    (startY, 6, Seq.empty, IntDataType),
    (spanX, 7, Seq.empty, IntDataType),
    (spanY, 8, Seq.empty, IntDataType),
    (widgetType, 9, Seq.empty, StringDataType),
    (label, 10, Seq.empty, StringDataType),
    (imagePath, 11, Seq.empty, StringDataType),
    (intent, 12, Seq.empty, StringDataType))

  prepareCursor[Widget](0, cursorData)
}

class WidgetRepositorySpec
  extends WidgetRepositorySpecification {

  "WidgetRepositoryClient component" should {

    "addWidget" should {

      "return a Widget object with a valid request" in
        new WidgetRepositoryScope {

          contentResolverWrapper.insert(any, any, any) returns testWidgetId
          val result = widgetRepository.addWidget(data = createWidgetData).value.run

          result must beLike {
            case Right(widget) =>
              widget.id shouldEqual testWidgetId
              widget.data.packageName shouldEqual testPackageName
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.insert(any, any, any) throws contentResolverException
          val result = widgetRepository.addWidget(data = createWidgetData).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "addWidgets" should {

      "return a sequence of Widget objects with a valid request" in
        new WidgetRepositoryScope {

          contentResolverWrapper.inserts(any, any, any, any) returns widgetIdSeq
          val result = widgetRepository.addWidgets(datas = widgetDataSeq).value.run

          result must beLike {
            case Right(widgets) =>
              widgets map (_.id) shouldEqual widgetIdSeq
              widgets map (_.data.packageName) shouldEqual (widgetSeq map (_.data.packageName))
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.inserts(any, any, any, any) throws contentResolverException
          val result = widgetRepository.addWidgets(datas = widgetDataSeq).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "deleteWidgets" should {

      "return a successful result when all widgets are deleted" in
        new WidgetRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) returns 1
          val result = widgetRepository.deleteWidgets().value.run
          result shouldEqual Right(1)

        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.delete(any, any, any, any) throws contentResolverException
          val result = widgetRepository.deleteWidgets().value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "deleteWidget" should {

      "return a successful result when a valid widget id is given" in
        new WidgetRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) returns 1
          val result = widgetRepository.deleteWidget(widget).value.run
          result shouldEqual Right(1)
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.deleteById(any, any, any, any, any) throws contentResolverException
          val result = widgetRepository.deleteWidget(widget).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "findWidgetById" should {

      "return a Widget object when a existing id is given" in
        new WidgetRepositoryScope {

          contentResolverWrapper.findById[WidgetEntity](any, any, any, any, any, any)(any) returns Some(widgetEntity)
          val result = widgetRepository.findWidgetById(id = testWidgetId).value.run

          result must beLike {
            case Right(maybeWidget) =>
              maybeWidget must beSome[Widget].which { widget =>
                widget.id shouldEqual testWidgetId
                widget.data.packageName shouldEqual testPackageName
              }
          }
        }

      "return None when a non-existing id is given" in
        new WidgetRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) returns None
          val result = widgetRepository.findWidgetById(id = testNonExistingWidgetId).value.run
          result shouldEqual Right(None)
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.findById(any, any, any, any, any, any)(any) throws contentResolverException
          val result = widgetRepository.findWidgetById(id = testWidgetId).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchWidgetByAppWidgetId" should {

      "return a Widget object when a existing appWidgetId is given" in
        new WidgetRepositoryScope {

          contentResolverWrapper.fetch(
            uri = mockUri,
            projection = allFields,
            where = s"$appWidgetId = ?",
            whereParams = Seq(testAppWidgetId.toString),
            orderBy = "")(f = getEntityFromCursor(widgetEntityFromCursor)) returns Some(widgetEntity)

          val result = widgetRepository.fetchWidgetByAppWidgetId(appWidgetId = testAppWidgetId).value.run

          result must beLike {
            case Right(maybeWidget) =>
              maybeWidget must beSome[Widget].which { widget =>
                widget.id shouldEqual testWidgetId
                widget.data.appWidgetId shouldEqual testAppWidgetId
              }
          }
        }

      "return None when a non-existing appWidgetId is given" in
        new WidgetRepositoryScope {

          contentResolverWrapper.fetch(uri = mockUri,
            projection = allFields,
            where = s"$appWidgetId = ?",
            whereParams = Seq(testNonExistingAppWidgetId.toString), orderBy = "")(f = getEntityFromCursor(widgetEntityFromCursor)) returns None

          val result = widgetRepository.fetchWidgetByAppWidgetId(appWidgetId = testNonExistingAppWidgetId).value.run
          result shouldEqual Right(None)
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.fetch(
            uri = mockUri,
            projection = allFields,
            where = s"$appWidgetId = ?",
            whereParams = Seq(testAppWidgetId.toString), orderBy = "")(f = getEntityFromCursor(widgetEntityFromCursor)) throws contentResolverException

          val result = widgetRepository.fetchWidgetByAppWidgetId(appWidgetId = testAppWidgetId).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchWidgetsByMoment" should {

      "return a Widget sequence when a existent moment id is given" in
        new WidgetRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$momentId = ?",
            whereParams = Seq(testMomentId.toString),
            orderBy = s"${WidgetEntity.momentId} asc")(f = getListFromCursor(widgetEntityFromCursor)) returns widgetEntitySeq

          val result = widgetRepository.fetchWidgetsByMoment(momentId = testMomentId).value.run
          result shouldEqual Right(widgetSeq)
        }

      "fetchWidgetsByMoment should return an empty sequence when a non-existent moment id is given" in
        new WidgetRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$momentId = ?",
            whereParams = Seq(testNonExistingMomentId.toString),
            orderBy = s"${WidgetEntity.momentId} asc")(f = getListFromCursor(widgetEntityFromCursor)) returns Seq.empty

          val result = widgetRepository.fetchWidgetsByMoment(momentId = testNonExistingMomentId).value.run
          result shouldEqual Right(Seq.empty)
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = s"$momentId = ?",
            whereParams = Seq(testMomentId.toString),
            orderBy = s"${WidgetEntity.momentId} asc")(f = getListFromCursor(widgetEntityFromCursor)) throws contentResolverException

          val result = widgetRepository.fetchWidgetsByMoment(momentId = testMomentId).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "fetchWidgets" should {

      "return all Widgets" in
        new WidgetRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = "",
            whereParams = Seq.empty, orderBy = "")(f = getListFromCursor(widgetEntityFromCursor)) returns widgetEntitySeq

          val result = widgetRepository.fetchWidgets().value.run
          result shouldEqual Right(widgetSeq)
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.fetchAll(
            uri = mockUri,
            projection = allFields,
            where = "",
            whereParams = Seq.empty, orderBy = "")(f = getListFromCursor(widgetEntityFromCursor)) throws contentResolverException

          val result = widgetRepository.fetchWidgets().value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "updateWidget" should {

      "return a successful result when the widget is updated" in
        new WidgetRepositoryScope {

          contentResolverWrapper.updateById(any, any, any, any) returns 1
          val result = widgetRepository.updateWidget(widget = widget).value.run
          result shouldEqual Right(1)

        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.updateById(any, any, any, any) throws contentResolverException
          val result = widgetRepository.updateWidget(widget = widget).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "updateWidgets" should {

      "return a successful result when the widgets are updated" in
        new WidgetRepositoryScope {

          contentResolverWrapper.updateByIds(any, any, any, any) returns Seq(5)
          val result = widgetRepository.updateWidgets(widgets = widgetSeq).value.run
          result shouldEqual Right(Seq(5))
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope {

          contentResolverWrapper.updateByIds(any, any, any, any) throws contentResolverException
          val result = widgetRepository.updateWidgets(widgets = widgetSeq).value.run
          result must beAnInstanceOf[Left[RepositoryException, _]]
        }
    }

    "getEntityFromCursor" should {

      "return None when an empty cursor is given" in
        new EmptyWidgetMockCursor with Scope {

          val result = getEntityFromCursor(widgetEntityFromCursor)(mockCursor)
          result must beNone
        }

      "return a Widget object when a cursor with data is given" in
        new WidgetMockCursor with Scope {

          val result = getEntityFromCursor(widgetEntityFromCursor)(mockCursor)

          result must beSome[WidgetEntity].which { widget =>
            widget.id shouldEqual widgetEntity.id
            widget.data shouldEqual widgetEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyWidgetMockCursor with Scope {

          val result = getListFromCursor(widgetEntityFromCursor)(mockCursor)
          result should beEmpty
        }

      "return a Widget sequence when a cursor with data is given" in
        new WidgetMockCursor with Scope {

          val result = getListFromCursor(widgetEntityFromCursor)(mockCursor)
          result shouldEqual widgetEntitySeq
        }
    }
  }
}
