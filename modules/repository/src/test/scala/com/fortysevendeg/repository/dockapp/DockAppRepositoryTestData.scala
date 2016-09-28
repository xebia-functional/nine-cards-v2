package com.fortysevendeg.repository.dockapp

import cards.nine.repository.model.{DockApp, DockAppData}
import cards.nine.repository.provider.{DockAppEntity, DockAppEntityData}

import scala.util.Random

trait DockAppRepositoryTestData {

  val testId = Random.nextInt(10)
  val testNonExistingId = 15
  val testName = Random.nextString(10)
  val testCardType = Random.nextString(10)
  val testIntent= Random.nextString(10)
  val testImagePath = Random.nextString(10)
  val testPosition = Random.nextInt(5)
  val testMockWhere = "mock-where"

  val dockAppEntitySeq = createDockAppEntitySeq(5)
  val dockAppEntity = dockAppEntitySeq(0)
  val dockAppSeq = createDockAppSeq(5)
  val dockAppIdSeq = dockAppSeq map (_.id)
  val dockAppDataSeq = dockAppSeq map (_.data)
  val dockApp = dockAppSeq(0)

  def createDockAppEntitySeq(num: Int) = List.tabulate(num)(
    i => DockAppEntity(
      id = testId + i,
      data = DockAppEntityData(
        name = testName,
        dockType = testCardType,
        intent = testIntent,
        imagePath = testImagePath,
        position = testPosition)))

  def createDockAppSeq(num: Int) = List.tabulate(num)(
    i => DockApp(
      id = testId + i,
      data = DockAppData(
        name = testName,
        dockType = testCardType,
        intent = testIntent,
        imagePath = testImagePath,
        position = testPosition)))

  def createDockAppValues = Map[String, Any](
    DockAppEntity.name -> testName,
    DockAppEntity.dockType -> testCardType,
    DockAppEntity.intent -> testIntent,
    DockAppEntity.imagePath -> testImagePath,
    DockAppEntity.position -> testPosition)

  def createDockAppData = DockAppData(
    name = testName,
    dockType = testCardType,
    intent = testIntent,
    imagePath = testImagePath,
    position = testPosition)
}
