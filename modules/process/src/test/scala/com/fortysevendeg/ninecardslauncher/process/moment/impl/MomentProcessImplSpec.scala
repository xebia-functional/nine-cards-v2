package com.fortysevendeg.ninecardslauncher.process.moment.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment._
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentException, MomentProcessConfig}
import com.fortysevendeg.ninecardslauncher.services.persistence.{OrderByName, PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.wifi.{WifiServices, WifiServicesException}
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait MomentProcessImplSpecification
  extends Specification
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")
  val wifiServiceException = WifiServicesException("")

  trait MomentProcessScope
    extends Scope 
    with MomentProcessImplData { 

    val resources = mock[Resources]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val momentProcessConfig = MomentProcessConfig(Map.empty)

    val mockPersistenceServices = mock[PersistenceServices]
    val mockWifiServices = mock[WifiServices]

    val mockIntent = mock[Intent]
    val mockNineCardIntent = mock[NineCardIntent]

    val momentProcess = new MomentProcessImpl(
      momentProcessConfig = momentProcessConfig,
      persistenceServices = mockPersistenceServices,
      wifiServices = mockWifiServices)

  }

  trait ValidGetBestAvailableMomentPersistenceServicesResponses {

    self: MomentProcessScope =>

    val wifi: Option[String]
    val time: DateTime

    mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollectionForMoments)))
    mockPersistenceServices.fetchMoments returns TaskService(Task(Xor.right(servicesMomentSeq)))

    mockWifiServices.getCurrentSSID(contextSupport) returns TaskService(Task(Xor.right(wifi)))

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

  "createMoments" should {

    "return the MomentCollectionType of the three collections associated with the moments created" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns TaskService(Task(Xor.right(seqServicesApps)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.right(moments map (_ => servicesCollection))))
        mockPersistenceServices.addMoment(any) returns TaskService(Task(Xor.right(servicesMoment)))

        val result = momentProcess.createMoments(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual moments.size
            resultSeqMoment map (_.name) shouldEqual seqCollection.map(_.name)
        }
      }

    "returns a MomentException if the service throws a exception fetching the existing collections" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = momentProcess.createMoments(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[MomentException]]
      }

    "returns a MomentException if the service throws a exception fetching the apps" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = momentProcess.createMoments(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[MomentException]]
      }

    "returns MomentException if the service throws a exception adding the collection" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns TaskService(Task(Xor.right(seqServicesApps)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = momentProcess.createMoments(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[MomentException]]
      }
  }

  "saveMoments" should {

    "return the three moments saved" in
      new MomentProcessScope {

        mockPersistenceServices.addMoments(any) returns TaskService(Task(Xor.right(moments map (_ => servicesMoment))))
        val result = momentProcess.saveMoments(seqMoments)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual seqMoments.size
        }
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope {

        mockPersistenceServices.addMoments(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = momentProcess.saveMoments(seqMoments)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[MomentException]]
      }

  }

  "generatePrivateMoments" should {

    "return the three moment's collections created" in
      new MomentProcessScope {
        val result = momentProcess.generatePrivateMoments(seqApps, position)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual moments.size
            resultSeqMoment map (_.collectionType) shouldEqual seqMomentCollections.map(_.collectionType)
        }
      }
  }

  "deleteAllMoments" should {

    "returns a successful answer for a valid request" in
      new MomentProcessScope {

        mockPersistenceServices.deleteAllMoments() returns TaskService(Task(Xor.right(momentId)))
        val result = momentProcess.deleteAllMoments().value.run
        result shouldEqual Xor.Right(():Unit)
      }

    "returns a MomentException if the service throws a exception deleting the moments" in
      new MomentProcessScope  {

        mockPersistenceServices.deleteAllMoments() returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = momentProcess.deleteAllMoments().value.run
        result must beAnInstanceOf[Xor.Left[MomentException]]
      }
  }

  "getBestAvailableMoment" should {

    "returns the best available moment in the morning with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(homeMorningMoment))
      }

    "returns the best available moment in the afternoon with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(homeMorningMoment))
      }

    "returns the best available moment in the night with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(homeNightMoment))
      }

    "returns the best available moment in the late night with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowLateNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(homeNightMoment))
      }

    "returns the best available moment in the morning with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(workMoment))

      }

    "returns the best available moment in the afternoon with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(transitMoment))
      }

    "returns the best available moment in the morning with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(transitMoment))
      }

    "returns the best available moment in the afternoon with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(transitMoment))
      }

    "returns the best available moment in the night with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowLateNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(transitMoment))
      }

    "returns the best available moment in the morning on a weekend with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowMorningWeekend

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result shouldEqual Xor.Right(Some(transitMoment))
      }

    "returns a MomentException if the service throws a exception getting the moments" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[MomentException]]
      }

    "returns a MomentException if the service throws a exception getting the current SSID" in
      new MomentProcessScope  {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Xor.right(servicesMomentSeq)))
        mockWifiServices.getCurrentSSID(contextSupport) returns TaskService(Task(Xor.left(wifiServiceException)))

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[MomentException]]
      }
  }

  "getAvailableMoments" should {

    "returns the available moments with collection" in
      new MomentProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Xor.right(servicesMomentSeq)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment flatMap (_.momentType map (_.name)) shouldEqual (servicesAvailableMomentsSeq flatMap (_.momentType))
        }
      }

    "returns a MomentException if the service throws a exception" in
      new MomentProcessScope  {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollectionForMoments)))
        mockPersistenceServices.fetchMoments returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[MomentException]]
      }

  }

}
