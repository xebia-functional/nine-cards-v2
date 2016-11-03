package cards.nine.repository.moment

import cards.nine.repository.model.{Moment, MomentData}
import cards.nine.repository.provider.{MomentEntity, MomentEntityData}

import scala.util.Random

trait MomentRepositoryTestData {

  val testId = Random.nextInt(10)
  val testNonExistingId = 15
  val testCollectionId = Random.nextInt(5)
  val testNonExistingCollectionId = Random.nextInt(5) + 100
  val testTimeslot = Random.nextString(10)
  val testWifi= Random.nextString(10)
  val testHeadphone = Random.nextBoolean()
  val testMomentType= Random.nextString(10)
  val testCollectionIdOption = Option(testCollectionId)
  val testMockWhere = "mock-where"

  val momentEntitySeq = createMomentEntitySeq(5)
  val momentEntity = momentEntitySeq(0)
  val momentSeq = createMomentSeq(5)
  val momentIdSeq = momentSeq map (_.id)
  val momentDataSeq = momentSeq map (_.data)
  val moment = momentSeq(0)

  def createMomentEntitySeq(num: Int) = List.tabulate(num)(
    i => MomentEntity(
      id = testId + i,
      data = MomentEntityData(
        collectionId = Some(testCollectionId),
        timeslot = testTimeslot,
        wifi = testWifi,
        headphone = testHeadphone,
        momentType = testMomentType)))

  def createMomentSeq(num: Int) = List.tabulate(num)(
    i => Moment(
      id = testId + i,
      data = MomentData(
        collectionId = testCollectionIdOption,
        timeslot = testTimeslot,
        wifi = testWifi,
        headphone = testHeadphone,
        momentType = Option(testMomentType))))

  def createMomentValues = Map[String, Any](
    MomentEntity.collectionId -> (testCollectionIdOption orNull),
    MomentEntity.timeslot -> testTimeslot,
    MomentEntity.wifi -> testWifi,
    MomentEntity.headphone -> testHeadphone,
    MomentEntity.momentType -> testMomentType)

  def createMomentData = MomentData(
    collectionId = testCollectionIdOption,
    timeslot = testTimeslot,
    wifi = testWifi,
    headphone = testHeadphone,
    momentType = Option(testMomentType))

  def createMomentValuesCollection = Map[String, Any](
    MomentEntity.collectionId -> (None orNull),
    MomentEntity.timeslot -> testTimeslot,
    MomentEntity.wifi -> testWifi,
    MomentEntity.headphone -> testHeadphone,
    MomentEntity.momentType -> testMomentType)

  def createMomentDataCollection = MomentData(
    collectionId = None,
    timeslot = testTimeslot,
    wifi = testWifi,
    headphone = testHeadphone,
    momentType = Option(testMomentType))
}
