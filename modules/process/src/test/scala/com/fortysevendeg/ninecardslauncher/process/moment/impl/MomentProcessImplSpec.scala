package com.fortysevendeg.ninecardslauncher.process.moment.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentException, MomentProcessConfig}
import com.fortysevendeg.ninecardslauncher.services.persistence.{OrderByName, PersistenceServiceException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait MomentProcessImplSpecification
  extends Specification
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")

  trait MomentProcessScope
    extends Scope {

    val resources = mock[Resources]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val momentProcessConfig = MomentProcessConfig(Map.empty)

    val mockPersistenceServices = mock[PersistenceServices]
    val mockIntent = mock[Intent]
    val mockNineCardIntent = mock[NineCardIntent]

    val momentProcess = new MomentProcessImpl(
      momentProcessConfig = momentProcessConfig,
      persistenceServices = mockPersistenceServices)

  }

  trait ValidCreateMomentPersistenceServicesResponses
    extends MomentProcessImplData {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns Service(Task(Result.answer(seqServicesApps)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.addMoment(any) returns Service(Task(Result.answer(servicesMoment)))

  }

  trait ErrorCreateMomentFetchCollectionsPersistenceServicesResponses
    extends MomentProcessImplData {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorCreateMomentFetchAppsPersistenceServicesResponses
    extends MomentProcessImplData {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorCreateMomentAddCollectionPersistenceServicesResponses
    extends MomentProcessImplData {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns Service(Task(Result.answer(seqServicesApps)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorCreateMomentAddMomentPersistenceServicesResponses
    extends MomentProcessImplData {

    self: MomentProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.fetchApps(OrderByName, ascending = true) returns Service(Task(Result.answer(seqServicesApps)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.addMoment(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait GeneratePrivateMoments
    extends MomentProcessImplData {

    self: MomentProcessScope =>

  }

  trait ValidDeleteAllMomentsPersistenceServicesResponses
    extends MomentProcessImplData {

    self: MomentProcessScope =>

    mockPersistenceServices.deleteAllMoments() returns Service(Task(Result.answer(momentId)))

  }

  trait ErrorDeleteAllMomentsPersistenceServicesResponses
    extends MomentProcessImplData {

    self: MomentProcessScope =>

    mockPersistenceServices.deleteAllMoments() returns Service(Task(Errata(persistenceServiceException)))

  }

}

class MomentProcessImplSpec
  extends MomentProcessImplSpecification {

  "createMoments" should {

    "return the three moments created" in
      new MomentProcessScope with ValidCreateMomentPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual 3
            resultSeqMoment map (_.name) shouldEqual seqCollection.map(_.name)
        }
      }

    "returns a MomentException if the service throws a exception fetching the existing collections" in
      new MomentProcessScope with ErrorCreateMomentFetchCollectionsPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[MomentException]
          }
        }
      }

    "returns a MomentException if the service throws a exception fetching the apps" in
      new MomentProcessScope with ErrorCreateMomentFetchAppsPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[MomentException]
          }
        }
      }

    "returns an empty list if the service throws a exception adding the collection" in
      new MomentProcessScope with ErrorCreateMomentAddCollectionPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqMoment) =>
            resultSeqMoment shouldEqual Nil
        }
      }

    "returns an empty list if the service throws a exception adding the moment" in
      new MomentProcessScope with ErrorCreateMomentAddMomentPersistenceServicesResponses {
        val result = momentProcess.createMoments(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqMoment) =>
            resultSeqMoment shouldEqual Nil
        }
      }
  }

  "generatePrivateMoments" should {

    "return the three moment's collections created" in
      new MomentProcessScope with GeneratePrivateMoments {
        val result = momentProcess.generatePrivateMoments(seqApps, position)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqMoment) =>
            resultSeqMoment.size shouldEqual 3
            resultSeqMoment map (_.collectionType) shouldEqual seqMomentCollections.map(_.collectionType)
        }
      }
  }

  "deleteAllMoments" should {

    "returns a successful answer for a valid request" in
      new MomentProcessScope with ValidDeleteAllMomentsPersistenceServicesResponses {
        val result = momentProcess.deleteAllMoments().run.run
        result must beLike {
          case Answer(resultMoment) =>
            resultMoment shouldEqual ((): Unit)
        }
      }

    "returns a MomentException if the service throws a exception deleting the moments" in
      new MomentProcessScope with ErrorDeleteAllMomentsPersistenceServicesResponses {
        val result = momentProcess.deleteAllMoments().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[MomentException]
          }
        }
      }
  }
}
