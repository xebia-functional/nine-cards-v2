package com.fortysevendeg.repository.widget

import com.fortysevendeg.ninecardslauncher.repository.model.{Widget, WidgetData}
import com.fortysevendeg.ninecardslauncher.repository.provider.{WidgetEntity, WidgetEntityData}

import scala.util.Random

trait WidgetRepositoryTestData {

  val testWidgetId = Random.nextInt(10)
  val testNonExistingWidgetId = 15
  val testMomentId = Random.nextInt(10)
  val testNonExistingMomentId = 15
  val testPackageName = Random.nextString(5)
  val testClassName = Random.nextString(5)
  val testAppWidgetId = Random.nextInt(10)
  val testNonExistingAppWidgetId = 15
  val testStartX = Random.nextInt(10)
  val testStartY = Random.nextInt(10)
  val testSpanX = Random.nextInt(10)
  val testSpanY = Random.nextInt(10)
  val testWidgetType = Random.nextString(5)
  val testLabel = Random.nextString(5)
  val testImagePath = Random.nextString(5)
  val testIntent = Random.nextString(5)
  val testLabelOption = Option(testLabel)
  val testImagePathOption = Option(testImagePath)
  val testIntentOption = Option(testIntent)

  val widgetEntitySeq = createWidgetEntitySeq(5)
  val widgetEntity = widgetEntitySeq(0)
  val widgetSeq = createWidgetSeq(5)
  val widget = widgetSeq(0)
  val widgetIdSeq = widgetSeq map (_.id)
  val widgetDataSeq = widgetSeq map (_.data)
  val widgetValuesSeq = createWidgetValuesSeq(5)
  val widgetValues = widgetValuesSeq(0)
  val widgetIdAndValuesSeq = createWidgetIdAndValuesSeq(5)

  def createWidgetEntitySeq(num: Int) = List.tabulate(num)(
    i => WidgetEntity(
      id = testWidgetId + i,
      data = WidgetEntityData(
        momentId = testMomentId,
        packageName = testPackageName,
        className = testClassName,
        appWidgetId = testAppWidgetId,
        startX = testStartX,
        startY = testStartY,
        spanX = testSpanX,
        spanY = testSpanY,
        widgetType = testWidgetType,
        label = testLabel,
        imagePath = testImagePath,
        intent = testIntent)))

  def createWidgetSeq(num: Int) = List.tabulate(num)(
    i => Widget(
      id = testWidgetId + i,
      data = WidgetData(
        momentId = testMomentId,
        packageName = testPackageName,
        className = testClassName,
        appWidgetId = testAppWidgetId,
        startX = testStartX,
        startY = testStartY,
        spanX = testSpanX,
        spanY = testSpanY,
        widgetType = testWidgetType,
        label = testLabelOption,
        imagePath = testImagePathOption,
        intent = testIntentOption)))

  def createWidgetValuesSeq(num: Int) = List.tabulate(num)(
    i => Map[String, Any](
      WidgetEntity.momentId -> testMomentId,
      WidgetEntity.packageName -> testPackageName,
      WidgetEntity.className -> testClassName,
      WidgetEntity.appWidgetId -> testAppWidgetId,
      WidgetEntity.startX -> testStartX,
      WidgetEntity.startY -> testStartY,
      WidgetEntity.spanX -> testSpanX,
      WidgetEntity.spanY -> testSpanY,
      WidgetEntity.widgetType -> testWidgetType,
      WidgetEntity.label -> (testLabelOption orNull),
      WidgetEntity.imagePath -> (testImagePathOption orNull),
      WidgetEntity.intent -> (testIntentOption orNull)))

  def createWidgetIdAndValuesSeq(num: Int) = List.tabulate(num)(
    i => (testWidgetId + i, Map[String, Any](
      WidgetEntity.momentId -> testMomentId,
      WidgetEntity.packageName -> testPackageName,
      WidgetEntity.className -> testClassName,
      WidgetEntity.appWidgetId -> testAppWidgetId,
      WidgetEntity.startX -> testStartX,
      WidgetEntity.startY -> testStartY,
      WidgetEntity.spanX -> testSpanX,
      WidgetEntity.spanY -> testSpanY,
      WidgetEntity.widgetType -> testWidgetType,
      WidgetEntity.label -> (testLabelOption orNull),
      WidgetEntity.imagePath -> (testImagePathOption orNull),
      WidgetEntity.intent -> (testIntentOption orNull))))

  def createWidgetData = WidgetData(
    momentId = testMomentId,
    packageName = testPackageName,
    className = testClassName,
    appWidgetId = testAppWidgetId,
    startX = testStartX,
    startY = testStartY,
    spanX = testSpanX,
    spanY = testSpanY,
    widgetType = testWidgetType,
    label = testLabelOption,
    imagePath = testImagePathOption,
    intent = testIntentOption)
}
