package com.fortysevendeg.repository.dockapp

import com.fortysevendeg.ninecardslauncher.repository.model.{DockApp, DockAppData}
import com.fortysevendeg.ninecardslauncher.repository.provider.{DockAppEntity, DockAppEntityData}

import scala.util.Random

trait DockAppRepositoryTestData {

  val testId = Random.nextInt(10)
  val testNonExistingId = 15
  val testName = Random.nextString(10)
  val testCardType = Random.nextString(10)
  val testIntent= Random.nextString(10)
  val testImagePath = Random.nextString(10)
  val testPosition = Random.nextInt(5)

  val dockAppEntitySeq = createDockAppEntitySeq(5)
  val dockAppEntity = dockAppEntitySeq.head
  val dockAppSeq = createDockAppSeq(5)
  val dockApp = dockAppSeq.head

  def createDockAppEntitySeq(num: Int) = List.tabulate(num)(
    i => DockAppEntity(
      id = testId + i,
      data = DockAppEntityData(
        name = testName,
        cardType = testCardType,
        intent = testIntent,
        imagePath = testImagePath,
        position = testPosition)))

  def createDockAppSeq(num: Int) = List.tabulate(num)(
    i => DockApp(
      id = testId + i,
      data = DockAppData(
        name = testName,
        cardType = testCardType,
        intent = testIntent,
        imagePath = testImagePath,
        position = testPosition)))

  def createDockAppValues = Map[String, Any](
    DockAppEntity.name ->  testName,
    DockAppEntity.cardType ->  testCardType,
    DockAppEntity.intent -> testIntent,
    DockAppEntity.imagePath -> testImagePath,
    DockAppEntity.position -> testPosition)

  def createDockAppData = DockAppData(
    name =  testName,
    cardType =  testCardType,
    intent = testIntent,
    imagePath = testImagePath,
    position = testPosition)
}
