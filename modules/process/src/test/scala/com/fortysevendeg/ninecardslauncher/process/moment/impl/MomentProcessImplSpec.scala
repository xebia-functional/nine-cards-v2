package com.fortysevendeg.ninecardslauncher.process.moment.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
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

  trait ValidCreateMomentPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns CatsService(Task(Xor.right(seqServicesApps)))
    mockPersistenceServices.addCollections(any) returns CatsService(Task(Xor.right(moments map (_ => servicesCollection))))
    mockPersistenceServices.addMoment(any) returns CatsService(Task(Xor.right(servicesMoment)))

  }

  trait ErrorCreateMomentFetchCollectionsPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorCreateMomentFetchAppsPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorCreateMomentAddCollectionPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns CatsService(Task(Xor.right(seqServicesApps)))
    mockPersistenceServices.addCollections(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidSaveMomentPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.addMoments(any) returns CatsService(Task(Xor.right(moments map (_ => servicesMoment))))

  }

  trait ErrorSaveMomentPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.addMoments(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidDeleteAllMomentsPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.deleteAllMoments() returns CatsService(Task(Xor.right(momentId)))

  }

  trait ErrorDeleteAllMomentsPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.deleteAllMoments() returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidGetBestAvailableMomentPersistenceServicesResponses {

    self: MomentProcessScope =>

    val wifi: Option[String]
    val time: DateTime

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollectionForMoments)))
    mockPersistenceServices.fetchMoments returns CatsService(Task(Xor.right(servicesMomentSeq)))

    mockWifiServices.getCurrentSSID(contextSupport) returns CatsService(Task(Xor.right(wifi)))

    override val momentProcess = new MomentProcessImpl(
      momentProcessConfig = momentProcessConfig,
      persistenceServices = mockPersistenceServices,
      wifiServices = mockWifiServices) {

      override protected def getNowDateTime = time
    }
  }

  trait ErrorGetBestAvailableMomentPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollectionForMoments)))
    mockPersistenceServices.fetchMoments returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorGetBestAvailableMomentWifiServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollectionForMoments)))
    mockPersistenceServices.fetchMoments returns CatsService(Task(Xor.right(servicesMomentSeq)))
    mockWifiServices.getCurrentSSID(contextSupport) returns CatsService(Task(Xor.left(wifiServiceException)))

  }

  trait ValidGetAvailableMomentsPersistenceServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollectionForMoments)))
    mockPersistenceServices.fetchMoments returns CatsService(Task(Xor.right(servicesMomentSeq)))

  }

  trait ErrorGetAvailableMomentsServicesResponses {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollectionForMoments)))
    mockPersistenceServices.fetchMoments returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

}

class MomentProcessImplSpec
  extends MomentProcessImplSpecification {

  "createMoments" should {

    "return the MomentCollectionType of the three collections associated with the moments created" in
      new MomentProcessScope with ValidCreateMomentPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual moments.size
            resultSeqMoment map (_.name) shouldEqual seqCollection.map(_.name)
        }
      }

    "returns a MomentException if the service throws a exception fetching the existing collections" in
      new MomentProcessScope with ErrorCreateMomentFetchCollectionsPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[MomentException]
          }
      }

    "returns a MomentException if the service throws a exception fetching the apps" in
      new MomentProcessScope with ErrorCreateMomentFetchAppsPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[MomentException]
          }
      }

    "returns MomentException if the service throws a exception adding the collection" in
      new MomentProcessScope with ErrorCreateMomentAddCollectionPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[MomentException]
          }
      }
  }

  "saveMoments" should {

    "return the three moments saved" in
      new MomentProcessScope with ValidSaveMomentPersistenceServicesResponses {
        val result = momentProcess.saveMoments(seqMoments)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual seqMoments.size
        }
      }

    "returns MomentException when persistence services fails" in
      new MomentProcessScope with ErrorSaveMomentPersistenceServicesResponses {
        val result = momentProcess.saveMoments(seqMoments)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[MomentException]
          }
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
      new MomentProcessScope with ValidDeleteAllMomentsPersistenceServicesResponses {
        val result = momentProcess.deleteAllMoments().value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual ((): Unit)
        }
      }

    "returns a MomentException if the service throws a exception deleting the moments" in
      new MomentProcessScope with ErrorDeleteAllMomentsPersistenceServicesResponses {
        val result = momentProcess.deleteAllMoments().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[MomentException]
          }
      }
  }

  "getBestAvailableMoment" should {

    "returns the best available moment in the morning with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(homeMorningMoment)
        }
      }

    "returns the best available moment in the afternoon with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(homeMorningMoment)
        }
      }

    "returns the best available moment in the night with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(homeNightMoment)
        }
      }

    "returns the best available moment in the late night with the home's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = homeWifi.headOption
        override val time = nowLateNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(homeNightMoment)
        }
      }

    "returns the best available moment in the morning with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(workMoment)
        }
      }

    "returns the best available moment in the afternoon with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(transitMoment)
        }
      }

    "returns the best available moment in the morning with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowMorning

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(workMoment)
        }
      }

    "returns the best available moment in the afternoon with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowAfternoon

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(homeMorningMoment)
        }
      }

    "returns the best available moment in the night with no wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = None
        override val time = nowLateNight

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(homeNightMoment)
        }
      }

    "returns the best available moment in the morning on a weekend with the work's wifi available" in
      new MomentProcessScope with ValidGetBestAvailableMomentPersistenceServicesResponses {

        override val wifi = workWifi.headOption
        override val time = nowMorningWeekend

        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment shouldEqual Some(transitMoment)
        }
      }

    "returns a MomentException if the service throws a exception getting the moments" in
      new MomentProcessScope with ErrorGetBestAvailableMomentPersistenceServicesResponses {
        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[MomentException]
          }
      }

    "returns a MomentException if the service throws a exception getting the current SSID" in
      new MomentProcessScope with ErrorGetBestAvailableMomentWifiServicesResponses {
        val result = momentProcess.getBestAvailableMoment(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[MomentException]
          }
      }
  }

  "getAvailableMoments" should {

    "returns the available moments with collection" in
      new MomentProcessScope with ValidGetAvailableMomentsPersistenceServicesResponses {

        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultMoment) =>
            resultMoment flatMap (_.momentType map (_.name)) shouldEqual (servicesAvailableMomentsSeq flatMap (_.momentType))
        }
      }

    "returns a MomentException if the service throws a exception" in
      new MomentProcessScope with ErrorGetAvailableMomentsServicesResponses {
        val result = momentProcess.getAvailableMoments(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[MomentException]
          }
      }

  }

}
