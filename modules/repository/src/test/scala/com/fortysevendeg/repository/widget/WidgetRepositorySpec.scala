package com.fortysevendeg.repository.widget

import android.net.Uri
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
import rapture.core.{Answer, Errata}

trait WidgetRepositorySpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait WidgetRepositoryScope
    extends Scope {

    lazy val contentResolverWrapper = mock[ContentResolverWrapperImpl]

    lazy val uriCreator = mock[UriCreator]

    lazy val widgetRepository = new WidgetRepository(contentResolverWrapper, uriCreator)

    lazy val mockUri = mock[Uri]
  }

  trait ValidWidgetRepositoryResponses
    extends WidgetRepositoryTestData {

    self: WidgetRepositoryScope =>

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = widgetValues,
      notificationUri = Some(mockUri)) returns testWidgetId

    contentResolverWrapper.inserts(
      authority = NineCardsUri.authorityPart,
      uri = mockUri,
      allValues = widgetValuesSeq,
      notificationUri = Some(mockUri)) returns widgetIdSeq

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testWidgetId,
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testWidgetId,
      projection = allFields)(
        f = getEntityFromCursor(widgetEntityFromCursor)) returns Some(widgetEntity)

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testNonExistingWidgetId,
      projection = allFields)(
        f = getEntityFromCursor(widgetEntityFromCursor)) returns None

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = s"$momentId asc")(
        f = getListFromCursor(widgetEntityFromCursor)) returns widgetEntitySeq

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$momentId = ?",
      whereParams = Seq(testMomentId.toString),
      orderBy = s"${WidgetEntity.momentId} asc")(
      f = getListFromCursor(widgetEntityFromCursor)) returns widgetEntitySeq

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$momentId = ?",
      whereParams = Seq(testNonExistingMomentId.toString),
      orderBy = s"${WidgetEntity.momentId} asc")(
      f = getListFromCursor(widgetEntityFromCursor)) returns Seq.empty

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = "")(
      f = getListFromCursor(widgetEntityFromCursor)) returns widgetEntitySeq

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$appWidgetId = ?",
      whereParams = Seq(testAppWidgetId.toString),
      orderBy = "")(
        f = getEntityFromCursor(widgetEntityFromCursor)) returns Some(widgetEntity)

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$appWidgetId = ?",
      whereParams = Seq(testNonExistingAppWidgetId.toString),
      orderBy = "")(
        f = getEntityFromCursor(widgetEntityFromCursor)) returns None

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testWidgetId,
      values = widgetValues,
      notificationUri = Some(mockUri)) returns 1

    contentResolverWrapper.updateByIds(
      authority = NineCardsUri.authorityPart,
      uri = mockUri,
      idAndValues = widgetIdAndValuesSeq,
      notificationUri = Some(mockUri)) returns Seq(5)
  }

  trait ValidAllWidgetsRepositoryResponses
    extends ValidWidgetRepositoryResponses {

    self: WidgetRepositoryScope =>

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields)(
      f = getListFromCursor(widgetEntityFromCursor)) returns widgetEntitySeq
  }

  trait ErrorWidgetRepositoryResponses
    extends WidgetRepositoryTestData {

    self: WidgetRepositoryScope =>

    val contentResolverException = new RuntimeException("Irrelevant message")

    uriCreator.parse(any) returns mockUri

    contentResolverWrapper.insert(
      uri = mockUri,
      values = widgetValues,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.inserts(
      authority = NineCardsUri.authorityPart,
      uri = mockUri,
      allValues = widgetValuesSeq,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.delete(
      uri = mockUri,
      where = "",
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.deleteById(
      uri = mockUri,
      id = testWidgetId,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.findById(
      uri = mockUri,
      id = testWidgetId,
      projection = allFields)(
        f = getEntityFromCursor(widgetEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = s"$momentId asc")(
        f = getListFromCursor(widgetEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = s"$momentId = ?",
      whereParams = Seq(testMomentId.toString),
      orderBy = s"${WidgetEntity.momentId} asc")(
      f = getListFromCursor(widgetEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields,
      where = "",
      whereParams = Seq.empty,
      orderBy = "")(
      f = getListFromCursor(widgetEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$momentId = ?",
      whereParams = Seq(testMomentId.toString),
      orderBy = "")(
        f = getEntityFromCursor(widgetEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.fetch(
      uri = mockUri,
      projection = allFields,
      where = s"$appWidgetId = ?",
      whereParams = Seq(testAppWidgetId.toString),
      orderBy = "")(
        f = getEntityFromCursor(widgetEntityFromCursor)) throws contentResolverException

    contentResolverWrapper.updateById(
      uri = mockUri,
      id = testWidgetId,
      values = widgetValues,
      notificationUri = Some(mockUri)) throws contentResolverException

    contentResolverWrapper.updateByIds(
      authority = NineCardsUri.authorityPart,
      uri = mockUri,
      idAndValues = widgetIdAndValuesSeq,
      notificationUri = Some(mockUri)) throws contentResolverException
  }

  trait ErrorAllWidgetsRepositoryResponses
    extends ErrorWidgetRepositoryResponses {

    self: WidgetRepositoryScope =>

    contentResolverWrapper.fetchAll(
      uri = mockUri,
      projection = allFields)(
      f = getListFromCursor(widgetEntityFromCursor)) throws contentResolverException
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
    (spanX, 5, widgetSeq map (_.data.spanX), IntDataType),
    (spanY, 6, widgetSeq map (_.data.spanY), IntDataType),
    (startX, 8, widgetSeq map (_.data.startX), IntDataType),
    (startY, 9, widgetSeq map (_.data.startY), IntDataType))

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
    (spanX, 5, Seq.empty, IntDataType),
    (spanY, 6, Seq.empty, IntDataType),
    (startX, 8, Seq.empty, IntDataType),
    (startY, 9, Seq.empty, IntDataType))

  prepareCursor[Widget](0, cursorData)
}

class WidgetRepositorySpec
  extends WidgetRepositorySpecification {

  "WidgetRepositoryClient component" should {

    "addWidget" should {

      "return a Widget object with a valid request" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.addWidget(data = createWidgetData).run.run

          result must beLike {
            case Answer(widget) =>
              widget.id shouldEqual testWidgetId
              widget.data.packageName shouldEqual testPackageName
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.addWidget(data = createWidgetData).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "addWidgets" should {

      "return a sequence of Widget objects with a valid request" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.addWidgets(datas = widgetDataSeq).run.run

          result must beLike {
            case Answer(widgets) =>
              widgets map (_.id) shouldEqual widgetIdSeq
              widgets map (_.data.packageName) shouldEqual (widgetSeq map (_.data.packageName))
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.addWidgets(datas = widgetDataSeq).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteWidgets" should {

      "return a successful result when all widgets are deleted" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.deleteWidgets().run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.deleteWidgets().run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "deleteWidget" should {

      "return a successful result when a valid widget id is given" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.deleteWidget(widget).run.run

          result must beLike {
            case Answer(deleted) =>
              deleted shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.deleteWidget(widget).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "findWidgetById" should {

      "return a Widget object when a existing id is given" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.findWidgetById(id = testWidgetId).run.run

          result must beLike {
            case Answer(maybeWidget) =>
              maybeWidget must beSome[Widget].which { widget =>
                widget.id shouldEqual testWidgetId
                widget.data.packageName shouldEqual testPackageName
              }
          }
        }

      "return None when a non-existing id is given" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {
          val result = widgetRepository.findWidgetById(id = testNonExistingWidgetId).run.run

          result must beLike {
            case Answer(maybeWidget) =>
              maybeWidget must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.findWidgetById(id = testWidgetId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchWidgetByAppWidgetId" should {

      "return a Widget object when a existing appWidgetId is given" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.fetchWidgetByAppWidgetId(appWidgetId = testAppWidgetId).run.run

          result must beLike {
            case Answer(maybeWidget) =>
              maybeWidget must beSome[Widget].which { widget =>
                widget.id shouldEqual testWidgetId
                widget.data.appWidgetId shouldEqual testAppWidgetId
              }
          }
        }

      "return None when a non-existing appWidgetId is given" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {
          val result = widgetRepository.fetchWidgetByAppWidgetId(appWidgetId = testNonExistingAppWidgetId).run.run

          result must beLike {
            case Answer(maybeWidget) =>
              maybeWidget must beNone
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.fetchWidgetByAppWidgetId(appWidgetId = testAppWidgetId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchWidgetsByMoment" should {

      "return a Widget sequence when a existent moment id is given" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.fetchWidgetsByMoment(momentId = testMomentId).run.run

          result must beLike {
            case Answer(widgets) =>
              widgets shouldEqual widgetSeq
          }
        }

      "fetchWidgetsByMoment should return an empty sequence when a non-existent moment id is given" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.fetchWidgetsByMoment(momentId = testNonExistingMomentId).run.run

          result must beLike {
            case Answer(widgets) =>
              widgets should beEmpty
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.fetchWidgetsByMoment(momentId = testMomentId).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "fetchWidgets" should {

      "return all Widgets" in
        new WidgetRepositoryScope
          with ValidAllWidgetsRepositoryResponses {

          val result = widgetRepository.fetchWidgets().run.run

          result must beLike {
            case Answer(widgets) =>
              widgets shouldEqual widgetSeq
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorAllWidgetsRepositoryResponses {

          val result = widgetRepository.fetchWidgets().run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "updateWidget" should {

      "return a successful result when the widget is updated" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.updateWidget(widget = widget).run.run

          result must beLike {
            case Answer(updated) =>
              updated shouldEqual 1
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.updateWidget(widget = widget).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "updateWidgets" should {

      "return a successful result when the widgets are updated" in
        new WidgetRepositoryScope
          with ValidWidgetRepositoryResponses {

          val result = widgetRepository.updateWidgets(widgets = widgetSeq).run.run

          result must beLike {
            case Answer(updated) =>
              updated shouldEqual Seq(5)
          }
        }

      "return a RepositoryException when a exception is thrown" in
        new WidgetRepositoryScope
          with ErrorWidgetRepositoryResponses {

          val result = widgetRepository.updateWidgets(widgets = widgetSeq).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, repositoryException)) => repositoryException must beLike {
                case e: RepositoryException => e.cause must beSome.which(_ shouldEqual contentResolverException)
              }
            }
          }
        }
    }

    "getEntityFromCursor" should {

      "return None when an empty cursor is given" in
        new EmptyWidgetMockCursor
          with Scope {

          val result = getEntityFromCursor(widgetEntityFromCursor)(mockCursor)

          result must beNone
        }

      "return a Widget object when a cursor with data is given" in
        new WidgetMockCursor
          with Scope {

          val result = getEntityFromCursor(widgetEntityFromCursor)(mockCursor)

          result must beSome[WidgetEntity].which { widget =>
            widget.id shouldEqual widgetEntity.id
            widget.data shouldEqual widgetEntity.data
          }
        }
    }

    "getListFromCursor" should {

      "return an empty sequence when an empty cursor is given" in
        new EmptyWidgetMockCursor
          with Scope {

          val result = getListFromCursor(widgetEntityFromCursor)(mockCursor)

          result should beEmpty
        }

      "return a Widget sequence when a cursor with data is given" in
        new WidgetMockCursor
          with Scope {

          val result = getListFromCursor(widgetEntityFromCursor)(mockCursor)

          result shouldEqual widgetEntitySeq
        }
    }
  }
}
