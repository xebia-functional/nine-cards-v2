package cards.nine.process.moment.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.MomentTestData
import cards.nine.models.NineCardsIntent
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.types.NineCardsMoment._
import cards.nine.process.moment.{MomentException, MomentProcessConfig}
import cards.nine.services.persistence.{OrderByName, PersistenceServiceException, PersistenceServices}
import cards.nine.services.wifi.{WifiServices, WifiServicesException}
import cats.syntax.either._
import monix.eval.Task
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait MomentProcessImplSpecification
  extends Specification
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")
  val wifiServiceException = WifiServicesException("")

  trait MomentProcessScope
    extends Scope 
    with MomentTestData {

    val resources = mock[Resources]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val momentProcessConfig = MomentProcessConfig(Map.empty)

    val mockPersistenceServices = mock[PersistenceServices]
    val mockWifiServices = mock[WifiServices]

    val mockIntent = mock[Intent]
    val mockNineCardIntent = mock[NineCardsIntent]

    val momentProcess = new MomentProcessImpl(
      momentProcessConfig = momentProcessConfig,
      persistenceServices = mockPersistenceServices,
      wifiServices = mockWifiServices)

  }

  trait ValidGetBestAvailableMomentPersistenceServicesResponses {

    self: MomentProcessScope =>

    val wifi: Option[String]
    val time: DateTime

    mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionForMoments)))
    mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(servicesMomentSeq)))

    mockWifiServices.getCurrentSSID(contextSupport) returns TaskService(Task(Either.right(wifi)))

    override val momentProcess = new MomentProcessImpl(
      momentProcessConfig = momentProcessConfig,
      persistenceServices = mockPersistenceServices,
      wifiServices = mockWifiServices) {

      override protected def getNowDateTime = time
    }
  }


}

class MomentProcessImplSpec
  extends MomentProcessImplSpecification {

  "getMoments" should {

    "return the existing moments" in
     new MomentProcessScope {

       mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(servicesMomentSeq)))
       val result = momentProcess.getMoments.value.run
       result shouldEqual Right(processMomentSeq)
     }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.getMoments.value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "getMomentByType" should {

    "return a moment by type" in
      new MomentProcessScope {

        mockPersistenceServices.getMomentByType(any) returns TaskService(Task(Either.right(servicesMoment)))
        val result = momentProcess.getMomentByType(mockNineCardMoment).value.run
        result shouldEqual Right(processMomentByType)
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.getMomentByType(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.getMomentByType(mockNineCardMoment).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "fetchMomentByType" should {

    "return a moment by type" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMomentByType(any) returns TaskService(Task(Either.right(Option(servicesMoment))))
        val result = momentProcess.fetchMomentByType(mockNineCardMoment).value.run
        result shouldEqual Right(Option(processMomentByType))
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMomentByType(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.fetchMomentByType(mockNineCardMoment).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "createMomentWithoutCollection" should {

    "return a new Moment without collection by type" in
      new MomentProcessScope {

        mockPersistenceServices.addMoment(any) returns TaskService(Task(Either.right(servicesMomentWihoutCollection)))
        val result = momentProcess.createMomentWithoutCollection(mockNineCardMoment)(contextSupport).value.run
        result shouldEqual Right(processMomentWithoutCollection)

      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.addMoment(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.createMomentWithoutCollection(mockNineCardMoment)(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "updateMoments" should {

    "return Unit for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.updateMoment(any) returns TaskService(Task(Either.right(item)))
        val result = momentProcess.updateMoment(updateMomentRequest)(contextSupport).value.run
        result shouldEqual Right(():Unit)
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.updateMoment(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.updateMoment(updateMomentRequest)(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

  }

  "saveMoments" should {

    "return the three moments saved" in
      new MomentProcessScope {

        mockPersistenceServices.addMoments(any) returns TaskService(Task(Either.right(moments map (_ => servicesMoment))))
        val result = momentProcess.saveMoments(seqMoments)(contextSupport).value.run
        result must beLike {
          case Right(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual seqMoments.size
        }
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.addMoments(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.saveMoments(seqMoments)(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

  }

  "deleteAllMoments" should {

    "returns a successful answer for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.deleteAllMoments() returns TaskService(Task(Either.right(momentId)))
        val result = momentProcess.deleteAllMoments().value.run
        result shouldEqual Right(():Unit)
      }

    "returns a MomentException if the service throws a exception deleting the moments" in
      new MomentProcessScope  {

        mockPersistenceServices.deleteAllMoments() returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.deleteAllMoments().value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "getBestAvailableMoment" should {

    "returns the best available moment in the morning with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(homeMorningMoment))
      }

    "returns the best available moment in the afternoon with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(homeMorningMoment))
      }

    "returns the best available moment in the night with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(homeNightMoment))
      }

    "returns the best available moment in the late night with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowLateNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(homeNightMoment))
      }

    "returns the best available moment in the morning with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(workMoment))

      }

    "returns the best available moment in the afternoon with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }

    "returns the best available moment in the morning with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }

    "returns the best available moment in the afternoon with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }

    "returns the best available moment in the night with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowLateNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }

    "returns the best available moment in the morning on a weekend with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowMorningWeekend

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }

    "returns a MomentException if the service throws a exception getting the moments" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

    "returns a MomentException if the service throws a exception getting the current SSID" in
      new MomentProcessScope  {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(servicesMomentSeq)))
        mockWifiServices.getCurrentSSID(contextSupport) returns TaskService(Task(Either.left(wifiServiceException)))

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "getAvailableMoments" should {

    "returns the available moments with collection" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(servicesMomentSeq)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beLike {
          case Right(resultMoment) =>
            resultMoment flatMap (_.momentType map (_.name)) shouldEqual (servicesAvailableMomentsSeq flatMap (_.momentType))
        }
      }

    "return Seq.empty for moments without collectionId" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqServicesMomentsWithoutCollection)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

    "return Seq.empty if collectionId of Moments it's different id of Collections" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionForMomentsWithoutId)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(servicesMomentSeq)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

    "returns a MomentException if the service throws a exception" in
      new MomentProcessScope  {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

    "returns a MomentException if the service throws a exception" in
      new MomentProcessScope  {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(servicesMomentSeq)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

  }

}
