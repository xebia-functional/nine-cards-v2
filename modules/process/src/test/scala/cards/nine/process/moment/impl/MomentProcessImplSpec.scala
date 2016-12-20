package cards.nine.process.moment.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.MomentValues._
import cards.nine.commons.test.data.{CollectionTestData, MomentTestData}
import cards.nine.models._
import cards.nine.models.types._
import cards.nine.process.moment.MomentException
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.persistence.{PersistenceServiceException, PersistenceServices}
import cards.nine.services.connectivity.{ConnectivityServices, WifiServicesException}
import cats.syntax.either._
import monix.eval.Task
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait MomentProcessImplSpecification
    extends TaskServiceSpecification
    with MomentTestData
    with CollectionTestData
    with Mockito {

  val persistenceServiceException = PersistenceServiceException("")
  val wifiServiceException        = WifiServicesException("")

  trait MomentProcessScope extends Scope with MomentProcessImplData with MomentTestData {

    val resources = mock[Resources]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val mockPersistenceServices = mock[PersistenceServices]
    val mockWifiServices        = mock[ConnectivityServices]
    val mockAwarenessService    = mock[AwarenessServices]

    val mockIntent         = mock[Intent]
    val mockNineCardIntent = mock[NineCardsIntent]

    val momentProcess = new MomentProcessImpl(
      persistenceServices = mockPersistenceServices,
      connectivityServices = mockWifiServices,
      awarenessServices = mockAwarenessService)

  }

  trait BestAvailableMomentScope { self: MomentProcessScope =>

    val time: DateTime

    override val momentProcess = new MomentProcessImpl(
      persistenceServices = mockPersistenceServices,
      connectivityServices = mockWifiServices,
      awarenessServices = mockAwarenessService) {

      override protected def getNowDateTime = time
    }
  }

}

class MomentProcessImplSpec extends MomentProcessImplSpecification {

  "getMoments" should {

    "return the existing moments" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment)))
        val result = momentProcess.getMoments.run
        result shouldEqual Right(seqMoment)
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMoments returns TaskService(
          Task(Either.left(persistenceServiceException)))
        momentProcess.getMoments.mustLeft[MomentException]
      }
  }

  "getMomentByCollectionId" should {

    "returns a collection for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.getMomentByCollectionId(collectionId) returns serviceRight(
          Some(moment.copy(collectionId = Option(collectionId))))

        momentProcess.getMomentByCollectionId(collectionId).mustRight { maybeMoment =>
          maybeMoment must beSome.which { resultMoment =>
            resultMoment.id shouldEqual moment.id
            resultMoment.collectionId shouldEqual Option(collectionId)
          }
        }
      }

    "returns None for a valid request if the collection id doesn't exists" in
      new MomentProcessScope {

        mockPersistenceServices.getMomentByCollectionId(collectionId) returns serviceRight(None)
        momentProcess.getMomentByCollectionId(collectionId).mustRightNone
      }

    "returns a CollectionException if the service throws an exception" in
      new MomentProcessScope {

        mockPersistenceServices.getMomentByCollectionId(collectionId) returns serviceLeft(
          persistenceServiceException)
        momentProcess.getMomentByCollectionId(collectionId).mustLeft[MomentException]
      }
  }

  "getMomentByType" should {

    "return a moment by type" in
      new MomentProcessScope {

        mockPersistenceServices.getMomentByType(any) returns TaskService(
          Task(Either.right(moment)))
        val result = momentProcess.getMomentByType(momentType).run
        result shouldEqual Right(moment)
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.getMomentByType(any) returns TaskService(
          Task(Either.left(persistenceServiceException)))
        momentProcess.getMomentByType(momentType).mustLeft[MomentException]
      }
  }

  "fetchMomentByType" should {

    "return a moment by type" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMomentByType(any) returns TaskService(
          Task(Either.right(Option(moment))))
        val result = momentProcess.fetchMomentByType(momentType).run
        result shouldEqual Right(Option(moment))
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMomentByType(any) returns TaskService(
          Task(Either.left(persistenceServiceException)))
        momentProcess.fetchMomentByType(momentType).mustLeft[MomentException]
      }
  }

  "findMoment" should {

    "return a moment by id" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMomentById(any) returns TaskService(
          Task(Either.right(Option(moment))))
        val result = momentProcess.findMoment(moment.id).run
        result shouldEqual Right(Option(moment))
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.fetchMomentById(any) returns TaskService(
          Task(Either.left(persistenceServiceException)))
        momentProcess.findMoment(moment.id).mustLeft[MomentException]
      }
  }

  "updateMoments" should {

    "return Unit for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.updateMoment(any) returns TaskService(
          Task(Either.right(updatedMoment)))
        val result = momentProcess.updateMoment(moment)(contextSupport).run
        result shouldEqual Right((): Unit)
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.updateMoment(any) returns TaskService(
          Task(Either.left(persistenceServiceException)))
        momentProcess.updateMoment(moment)(contextSupport).mustLeft[MomentException]
      }

  }

  "saveMoments" should {

    "return the three moments saved" in
      new MomentProcessScope {

        mockPersistenceServices.addMoments(any) returns TaskService(Task(Either.right(seqMoment)))
        momentProcess.saveMoments(seqMomentData)(contextSupport).mustRight {
          _.size shouldEqual seqMoment.size
        }
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.addMoments(any) returns TaskService(
          Task(Either.left(persistenceServiceException)))
        momentProcess.saveMoments(seqMomentData)(contextSupport).mustLeft[MomentException]
      }

  }

  "deleteAllMoments" should {

    "returns a successful answer for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.deleteAllMoments() returns TaskService(
          Task(Either.right(momentId)))
        val result = momentProcess.deleteAllMoments().run
        result shouldEqual Right((): Unit)
      }

    "returns a MomentException if the service throws a exception deleting the moments" in
      new MomentProcessScope {

        mockPersistenceServices.deleteAllMoments() returns TaskService(
          Task(Either.left(persistenceServiceException)))
        momentProcess.deleteAllMoments().mustLeft[MomentException]
      }
  }

  "deleteMoment" should {

    "returns a successful answer for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.deleteMoment(moment.id) returns TaskService.right(momentId)
        val result = momentProcess.deleteMoment(moment.id).run
        result shouldEqual Right((): Unit)
      }

    "returns a MomentException if the service throws a exception deleting the moment" in
      new MomentProcessScope {

        mockPersistenceServices.deleteMoment(moment.id) returns TaskService.left(
          persistenceServiceException)
        val result = momentProcess.deleteMoment(moment.id).run
        result must beAnInstanceOf[Left[MomentException, _]]
      }
  }

  "getBestAvailableMoment" should {

    "returns the best available moment in the morning with the home's wifi available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(homeWifiSSID))

        val result = momentProcess.getBestAvailableMoment(None, None)(contextSupport).run
        result shouldEqual Right(Some(homeMoment))
      }

    "returns the best available moment in the afternoon with the home's wifi available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowAfternoon

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(homeWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(homeMoment))
      }

    "returns the best available moment in the night with the home's wifi available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowNight

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(homeWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(homeMoment))
      }

    "returns the best available moment in the late night with the home's wifi available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowLateNight

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(homeWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(homeMoment))
      }

    "returns the best available moment in the morning when two moments share the same wifi" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        val newWorkMoment = workMoment.copy(wifi = Seq(homeWifiSSID))

        val moments = allMoments.map {
          case Moment(_, _, _, _, _, WorkMoment, _) => newWorkMoment
          case m                                    => m
        }
        mockPersistenceServices.fetchMoments returns TaskService.right(moments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(homeWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(newWorkMoment))
      }

    "returns the best available moment in the morning when two moments share the same wifi but different hour range" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        val newWorkMoment = workMoment.copy(
          wifi = Seq(homeWifiSSID),
          timeslot = Seq(MomentTimeSlot("06:00", "20:00", Seq.fill(7)(1))))

        val moments = allMoments.map {
          case Moment(_, _, _, _, _, WorkMoment, _) => newWorkMoment
          case m                                    => m
        }
        mockPersistenceServices.fetchMoments returns TaskService.right(moments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(homeWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(homeMoment))
      }

    "returns the best available moment in the morning when two moments share the same wifi but only one is in range" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        val newWorkMoment = workMoment.copy(
          wifi = Seq(homeWifiSSID),
          timeslot = Seq(MomentTimeSlot("16:00", "20:00", Seq.fill(7)(1))))

        val moments = allMoments.map {
          case Moment(_, _, _, _, _, WorkMoment, _) => newWorkMoment
          case m                                    => m
        }
        mockPersistenceServices.fetchMoments returns TaskService.right(moments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(homeWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(homeMoment))
      }

    "returns the best available moment in the morning with no wifi available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(studyMoment))
      }

    "returns the best available moment in the afternoon with no wifi available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowAfternoon

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(outAndAboutMoment))
      }

    "returns the best available moment in the night with no wifi available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowNight

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(nightMoment))
      }

    "returns the best available moment when the headphones are sent" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result =
          momentProcess.getBestAvailableMoment(maybeHeadphones = Some(true))(contextSupport).run
        result shouldEqual Right(Some(musicMoment))
      }

    "returns the best available moment when the headphones state is active" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(true))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(musicMoment))
      }

    "returns the best available moment when the headphones are sent but a wifi is available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(workWifiSSID))

        val result =
          momentProcess.getBestAvailableMoment(maybeHeadphones = Some(true))(contextSupport).run
        result shouldEqual Right(Some(musicMoment))
      }

    "returns the best available moment when the headphones state is active but a wifi is available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(true))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(workWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(musicMoment))
      }

    "returns the best available moment when the headphones state is active but a wifi is available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(workWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(workMoment))
      }

    "returns the best available moment when the headphones state is active but there is no music moment available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(
          allMoments.filterNot(_.momentType == MusicMoment))
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(studyMoment))
      }

    "returns the best available moment when the InVehicleActivity is sent" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess
          .getBestAvailableMoment(maybeActivity = Some(InVehicleActivity))(contextSupport)
          .run
        result shouldEqual Right(Some(carMoment))
      }

    "returns the best available moment when the InVehicleActivity is active" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(InVehicleActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(carMoment))
      }

    "returns the best available moment when the InVehicleActivity is sent but a wifi is available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(workWifiSSID))

        val result = momentProcess
          .getBestAvailableMoment(maybeActivity = Some(InVehicleActivity))(contextSupport)
          .run
        result shouldEqual Right(Some(workMoment))
      }

    "returns the best available moment when the InVehicleActivity is active but a wifi is available" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(allMoments)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(InVehicleActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(workWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(workMoment))
      }

    "returns the best available moment when the only moment available is the default one" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(Seq(outAndAboutMoment))
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(Some(workWifiSSID))

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(outAndAboutMoment))
      }

    "returns the best available moment and add it to the DB when the default one doesn't exists" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowAfternoon

        mockPersistenceServices.fetchMoments returns TaskService.right(
          allMoments.filterNot(_.momentType.isDefault))
        mockPersistenceServices.addMoment(any) returns TaskService.right(outAndAboutMoment)
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(outAndAboutMoment))

        there was one(mockPersistenceServices).addMoment(
          outAndAboutMoment.toData.copy(collectionId = None))
      }

    "returns the best available moment when only one moment is happening" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        val moment1 = homeMoment
          .copy(wifi = Seq.empty, timeslot = Seq(MomentTimeSlot("06:00", "18:00", Seq.fill(7)(1))))
        val moment2 = workMoment
          .copy(wifi = Seq.empty, timeslot = Seq(MomentTimeSlot("18:00", "23:00", Seq.fill(7)(1))))

        mockPersistenceServices.fetchMoments returns TaskService.right(Seq(moment1, moment2))
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(moment1))
      }

    "returns the best available moment when both moments are happening but one has a lowest range" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        val moment1 = homeMoment
          .copy(wifi = Seq.empty, timeslot = Seq(MomentTimeSlot("06:00", "18:00", Seq.fill(7)(1))))
        val moment2 = workMoment
          .copy(wifi = Seq.empty, timeslot = Seq(MomentTimeSlot("06:00", "12:00", Seq.fill(7)(1))))

        mockPersistenceServices.fetchMoments returns TaskService.right(Seq(moment1, moment2))
        mockAwarenessService.getHeadphonesState returns TaskService.right(Headphones(false))
        mockAwarenessService.getTypeActivity returns TaskService.right(
          ProbablyActivity(UnknownActivity))
        mockWifiServices.getCurrentSSID(any) returns TaskService.right(None)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(Some(moment2))
      }

    "returns None when there is no moments in the database" in
      new MomentProcessScope with BestAvailableMomentScope {

        override val time = nowMorning

        mockPersistenceServices.fetchMoments returns TaskService.right(Seq.empty)

        val result = momentProcess.getBestAvailableMoment()(contextSupport).run
        result shouldEqual Right(None)
      }
  }

  "getAvailableMoments" should {

    "returns the available moments with collection" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(
          Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment)))

        val result = momentProcess.getAvailableMoments(contextSupport).run
        result shouldEqual Right(availableMoments)
      }

    "return Seq.empty for moments without collectionId" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(
          Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns TaskService(
          Task(Either.right(seqMoment map (_.copy(collectionId = None)))))

        val result = momentProcess.getAvailableMoments(contextSupport).run
        result shouldEqual Right(Seq.empty)
      }

    "return Seq.empty if collectionId of Moments it's different id of Collections" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(
          Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns
          TaskService(
            Task(Either.right(
              seqMoment map (moment =>
                               moment.copy(collectionId = moment.collectionId map (_ + 100))))))

        val result = momentProcess.getAvailableMoments(contextSupport).run
        result shouldEqual Right(Seq.empty)
      }

    "returns a MomentException if the service throws a exception" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(
          Task(Either.right(seqCollection)))
        mockPersistenceServices.fetchMoments returns TaskService(
          Task(Either.left(persistenceServiceException)))

        momentProcess.getAvailableMoments(contextSupport).mustLeft[MomentException]
      }

    "returns a MomentException if the service throws a exception" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(
          Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Either.right(seqMoment)))

        momentProcess.getAvailableMoments(contextSupport).mustLeft[MomentException]
      }

  }

}
