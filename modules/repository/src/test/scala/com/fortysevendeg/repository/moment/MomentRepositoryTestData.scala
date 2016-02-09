package com.fortysevendeg.repository.moment

import com.fortysevendeg.ninecardslauncher.repository.model.{Moment, MomentData}
import com.fortysevendeg.ninecardslauncher.repository.provider.{MomentEntity, MomentEntityData}

import scala.util.Random

trait MomentRepositoryTestData {

  val testId = Random.nextInt(10)
  val testNonExistingId = 15
  val testCollectionId = Random.nextInt(5)
  val testTimeslot = Random.nextString(10)
  val testWifi= Random.nextString(10)
  val testHeadphone = Random.nextBoolean()
  val testCollectionIdOption = Option(testCollectionId)

  val momentEntitySeq = createMomentEntitySeq(5)
  val momentEntity = momentEntitySeq(0)
  val momentSeq = createMomentSeq(5)
  val moment = momentSeq(0)

  def createMomentEntitySeq(num: Int) = List.tabulate(num)(
    i => MomentEntity(
      id = testId + i,
      data = MomentEntityData(
        collectionId = testCollectionId,
        timeslot = testTimeslot,
        wifi = testWifi,
        headphone = testHeadphone)))

  def createMomentSeq(num: Int) = List.tabulate(num)(
    i => Moment(
      id = testId + i,
      data = MomentData(
        collectionId = testCollectionIdOption,
        timeslot = testTimeslot,
        wifi = testWifi,
        headphone = testHeadphone)))

  def createMomentValues = Map[String, Any](
    MomentEntity.collectionId -> (testCollectionIdOption orNull),
    MomentEntity.timeslot -> testTimeslot,
    MomentEntity.wifi -> testWifi,
    MomentEntity.headphone -> testHeadphone)

  def createMomentData = MomentData(
    collectionId = testCollectionIdOption,
    timeslot = testTimeslot,
    wifi = testWifi,
    headphone = testHeadphone)

  def createMomentValuesCollection = Map[String, Any](
    MomentEntity.collectionId -> (None orNull),
    MomentEntity.timeslot -> testTimeslot,
    MomentEntity.wifi -> testWifi,
    MomentEntity.headphone -> testHeadphone)

  def createMomentDataCollection = MomentData(
    collectionId = None,
    timeslot = testTimeslot,
    wifi = testWifi,
    headphone = testHeadphone)
}
