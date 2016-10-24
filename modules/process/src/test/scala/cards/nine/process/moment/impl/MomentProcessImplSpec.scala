package cards.nine.process.moment.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.{CollectionTestData, MomentTestData}
import cards.nine.commons.test.data.MomentValues._
import cards.nine.models.NineCardsIntent
import cards.nine.models.types.NineCardsMoment
import cards.nine.models.types.NineCardsMoment._
import cards.nine.process.moment.MomentException
import cards.nine.services.persistence.{PersistenceServiceException, PersistenceServices}
import cards.nine.services.wifi.{WifiServices, WifiServicesException}
import cats.syntax.either._
import monix.eval.Task
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait MomentProcessImplSpecification
  extends Specification
  with MomentTestData 
  with CollectionTestData
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")
  val wifiServiceException = WifiServicesException("")

  trait MomentProcessScope
    extends Scope
    with MomentProcessImplData
    with MomentTestData {

    val resources = mock[Resources]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val mockPersistenceServices = mock[PersistenceServices]
    val mockWifiServices = mock[WifiServices]

    val mockIntent = mock[Intent]
    val mockNineCardIntent = mock[NineCardsIntent]

    val momentProcess = new MomentProcessImpl(
      persistenceServices = mockPersistenceServices,
      wifiServices = mockWifiServices)

  }

  trait ValidGetBestAvailableMomentPersistenceServicesResponses {

    self: MomentProcessScope =>

    val wifi: Option[String]
    val time: DateTime

    mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqCollection)))
    mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment)))

    mockWifiServices.getCurrentSSID(contextSupport) returns TaskService(Task(Either.right(wifi)))

    override val momentProcess = new MomentProcessImpl(
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

       mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment)))
       val result = momentProcess.getMoments.value.run
       result shouldEqual Right(seqMoment)
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

        mockPersistenceServices.getMomentByType(any) returns TaskService(Task(Either.right(moment)))
        val result = momentProcess.getMomentByType(momentType).value.run
        result shouldEqual Right(moment)
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.getMomentByType(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.getMomentByType(momentType).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "fetchMomentByType" should {

    "return a moment by type" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMomentByType(any) returns TaskService(Task(Either.right(Option(moment))))
        val result = momentProcess.fetchMomentByType(momentType).value.run
        result shouldEqual Right(Option(moment))
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMomentByType(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.fetchMomentByType(momentType).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "createMomentWithoutCollection" should {

    "return a new Moment without collection by type" in
      new MomentProcessScope {

        mockPersistenceServices.addMoment(any) returns TaskService(Task(Either.right(moment.copy(collectionId = None))))
        val result = momentProcess.createMomentWithoutCollection(momentType)(contextSupport).value.run
        result shouldEqual Right(moment.copy(collectionId = None))

      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.addMoment(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.createMomentWithoutCollection(momentType)(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "updateMoments" should {

    "return Unit for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.updateMoment(any) returns TaskService(Task(Either.right(updatedMoment)))
        val result = momentProcess.updateMoment(moment)(contextSupport).value.run
        result shouldEqual Right(():Unit)
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.updateMoment(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.updateMoment(moment)(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

  }

  "saveMoments" should {

    "return the three moments saved" in
      new MomentProcessScope {

        mockPersistenceServices.addMoments(any) returns TaskService(Task(Either.right(seqMoment)))
        val result = momentProcess.saveMoments(seqMomentData)(contextSupport).value.run
        result must beLike {
          case Right(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual seqMoment.size
        }
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.addMoments(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = momentProcess.saveMoments(seqMomentData)(contextSupport).value.run
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

  "deleteMoment" should {

    "returns a successful answer for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.deleteMoment(moment.id) returns TaskService.right(momentId)
        val result = momentProcess.deleteMoment(moment.id).value.run
        result shouldEqual Right(():Unit)
      }

    "returns a MomentException if the service throws a exception deleting the moment" in
      new MomentProcessScope  {

        mockPersistenceServices.deleteMoment(moment.id) returns TaskService.left(persistenceServiceException)
        val result = momentProcess.deleteMoment(moment.id).value.run
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
      }.pendingUntilFixed("Issue #943")


    "returns the best available moment in the afternoon with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(homeMorningMoment))
      }.pendingUntilFixed("Issue #943")

    "returns the best available moment in the night with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(homeNightMoment))
      }.pendingUntilFixed("Issue #943")

    "returns the best available moment in the late night with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowLateNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(homeNightMoment))
      }.pendingUntilFixed("Issue #943")

    "returns the best available moment in the morning with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(workMoment))
      }.pendingUntilFixed("Issue #943")

    "returns the best available moment in the afternoon with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }.pendingUntilFixed("Issue #943")

    "returns the best available moment in the morning with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }.pendingUntilFixed("Issue #943")

    "returns the best available moment in the afternoon with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }.pendingUntilFixed("Issue #943")

    "returns the best available moment in the night with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowLateNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }.pendingUntilFixed("Issue #943")

    "returns the best available moment in the morning on a weekend with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowMorningWeekend

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Right(Some(transitMoment))
      }.pendingUntilFixed("Issue #943")

    "returns a MomentException if the service throws a exception getting the moments" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

    "returns a MomentException if the service throws a exception getting the current SSID" in
      new MomentProcessScope  {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment)))
        mockWifiServices.getCurrentSSID(contextSupport) returns TaskService(Task(Either.left(wifiServiceException)))

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "getAvailableMoments" should {

    "returns the available moments with collection" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result shouldEqual Right(availableMoments)
      }

    "return Seq.empty for moments without collectionId" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment map (_.copy(collectionId = None)))))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

    "return Seq.empty if collectionId of Moments it's different id of Collections" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns
          TaskService(Task(Either.right(seqMoment map (moment => moment.copy(collectionId = moment.collectionId map (_ + 100))))))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

    "returns a MomentException if the service throws a exception" in
      new MomentProcessScope  {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

    "returns a MomentException if the service throws a exception" in
      new MomentProcessScope  {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beAnInstanceOf[Left[MomentException, _]]
      }

  }

}
