package com.fortysevendeg.ninecardslauncher.process.cloud.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.services.drive.models.DriveServiceFile
import com.fortysevendeg.ninecardslauncher.services.drive.{DriveServices, DriveServicesException}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.hamcrest.{Description, TypeSafeMatcher}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json
import rapture.core.{Answer, Errata}

import scalaz.concurrent.Task

trait CloudStorageProcessImplSpecification
  extends Specification
  with Mockito {

  val driveServicesException = new DriveServicesException("")

  val sampleId = "android-id"

  implicit val context = mock[ContextSupport]

  trait CloudStorageProcessImplScope
    extends Scope {

    val driveServices = mock[DriveServices]

    val persistenceServices = mock[PersistenceServices]

    persistenceServices.getAndroidId returns Service(Task(Answer(sampleId)))

    val cloudStorageProcess = new CloudStorageProcessImpl(driveServices, persistenceServices)

  }

  trait WithErrorDriveServices {

    self: CloudStorageProcessImplScope =>

    driveServices.listFiles(any) returns Service(Task(Errata(driveServicesException)))
    driveServices.readFile(any) returns Service(Task(Errata(driveServicesException)))
    driveServices.findFile(any) returns Service(Task(Errata(driveServicesException)))
    driveServices.deleteFile(any) returns Service(Task(Errata(driveServicesException)))

  }

  class JsonMatcher(json: String) extends TypeSafeMatcher[String] {

    val expected = Json.parse(json)

    override def matchesSafely(item: String): Boolean =
      expected == Json.parse(item)

    override def describeTo(description: Description): Unit =
      description.appendText("Json are not equivalent")
  }

}

class CloudStorageProcessImplSpec
  extends CloudStorageProcessImplSpecification {

  "getCloudStorageDevices" should {

    "return a sequence of CloudStorageResource when the service returns a valid response" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        driveServices.listFiles(any) returns Service(Task(Answer(driveServiceFileSeq)))

        val result = cloudStorageProcess.getCloudStorageDevices.run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual driveServiceFileSeq.size
            resultSeqCollection.map(_.title) shouldEqual driveServiceFileSeq.map(_.title)
            resultSeqCollection.map(_.resourceId) shouldEqual driveServiceFileSeq.map(_.googleDriveId)
        }

      }

    "return an empty sequence when the service returns a valid empty sequence" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        driveServices.listFiles(any) returns Service(Task(Answer(driveServiceFileEmptySeq)))

        val result = cloudStorageProcess.getCloudStorageDevices.run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection must beEmpty
        }

      }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope with WithErrorDriveServices {

        val result = cloudStorageProcess.getCloudStorageDevices.run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beLike {
              case e: CloudStorageProcessException => e.cause must beSome(driveServicesException)
            }
          }
        }

      }

  }

  "getCloudStorageDevice" should {

    "return a valid CloudStorageDevice when the service returns a valid Json" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        driveServices.readFile(driveId) returns Service(Task(Answer(validCloudStorageDeviceJson)))

        val result = cloudStorageProcess.getCloudStorageDevice(driveId).run.run
        result must beLike {
          case Answer(device) =>
            device.deviceId shouldEqual deviceId
            device.deviceName shouldEqual deviceName
            device.collections.size shouldEqual numCollections
            device.collections.map(_.items.size) shouldEqual Seq.fill(numCollections)(numItemsPerCollection)
        }
      }

    "return a CloudStorageProcessException when the service return a non valid Json" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        driveServices.readFile(driveId) returns Service(Task(Answer(invalidCloudStorageDeviceJson)))

        val result = cloudStorageProcess.getCloudStorageDevice(driveId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CloudStorageProcessException]
          }
        }
    }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData with WithErrorDriveServices {

        val result = cloudStorageProcess.getCloudStorageDevice(driveId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beLike {
              case e: CloudStorageProcessException => e.cause must beSome(driveServicesException)
            }
          }
        }

      }

  }

  "getCloudStorageDeviceById" should {

    "return a valid CloudStorageDevice when the service returns a valid Json" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        driveServices.findFile(fileId) returns Service(Task(Answer(Some(driveServiceFile))))
        driveServices.readFile(driveServiceFile.googleDriveId) returns Service(Task(Answer(validCloudStorageDeviceJson)))

        val result = cloudStorageProcess.getCloudStorageDeviceByAndroidId(fileId).run.run
        result must beLike {
          case Answer(device) =>
            device.deviceId shouldEqual deviceId
            device.deviceName shouldEqual deviceName
            device.collections.size shouldEqual numCollections
            device.collections.map(_.items.size) shouldEqual Seq.fill(numCollections)(numItemsPerCollection)
        }
      }

    "return a CloudStorageProcessException when the service return a non valid Json" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        driveServices.findFile(fileId) returns Service(Task(Answer(Some(driveServiceFile))))
        driveServices.readFile(driveServiceFile.googleDriveId) returns Service(Task(Answer(invalidCloudStorageDeviceJson)))

        val result = cloudStorageProcess.getCloudStorageDeviceByAndroidId(fileId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CloudStorageProcessException]
          }
        }
    }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData with WithErrorDriveServices {

        val result = cloudStorageProcess.getCloudStorageDeviceByAndroidId(fileId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beLike {
              case e: CloudStorageProcessException => e.cause must beSome(driveServicesException)
            }
          }
        }

      }

  }

    "createOrUpdateCloudStorageDevice" should {

      "call to create file in Service with a valid Json when the file doesn't exists" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          driveServices.findFile(deviceId) returns Service(Task(Answer(None)))
          driveServices.createFile(
            anyString,
            anArgThat(new JsonMatcher(validCloudStorageDeviceJson)),
            anyString,
            anyString,
            anyString) returns Service(Task(Answer(())))

          cloudStorageProcess.createOrUpdateCloudStorageDevice(cloudStorageDevice).run.run
        }

      "call to update file in Service with a valid Json when the file does exists" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          driveServices.findFile(deviceId) returns Service(Task(Answer(driveServiceFileSeq.headOption)))
          driveServices.updateFile(
            anyString,
            anArgThat(new JsonMatcher(validCloudStorageDeviceJson))) returns Service(Task(Answer(())))

          cloudStorageProcess.createOrUpdateCloudStorageDevice(cloudStorageDevice).run.run
        }

      "return a CloudStorageProcessException when the service return an exception" in
        new CloudStorageProcessImplScope with WithErrorDriveServices with CloudStorageProcessImplData {

          val result = cloudStorageProcess.createOrUpdateCloudStorageDevice(cloudStorageDevice).run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beLike {
                case e: CloudStorageProcessException => e.cause must beSome(driveServicesException)
              }
            }
          }

        }

    }

    "createOrUpdateActualCloudStorageDevice" should {

      "call to create file in Service with a valid Json when the file doesn't exists" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          driveServices.findFile(sampleId) returns Service(Task(Answer(None)))
          driveServices.createFile(
            anyString,
            anyString,
            anyString,
            anyString,
            anyString) returns Service(Task(Answer(())))

          cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageDevice.collections,
            cloudStorageDevice.moments getOrElse Seq.empty).run.run
        }

      "call to update file in Service with a valid Json when the file does exists" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          driveServices.findFile(sampleId) returns Service(Task(Answer(driveServiceFileSeq.headOption)))
          driveServices.updateFile(
            anyString,
            anyString) returns Service(Task(Answer(())))

          cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageDevice.collections,
            cloudStorageDevice.moments getOrElse Seq.empty).run.run
        }

      "return a CloudStorageProcessException when the service return an exception" in
        new CloudStorageProcessImplScope with WithErrorDriveServices with CloudStorageProcessImplData {

          val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageDevice.collections,
            cloudStorageDevice.moments getOrElse Seq.empty).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beLike {
                case e: CloudStorageProcessException => e.cause must beSome(driveServicesException)
              }
            }
          }

        }

    }

  "deleteCloudStorageDeviceByAndroidId" should {

    "return a valid response when the service find the device" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        driveServices.deleteFile(driveId) returns Service(Task(Answer(Unit)))

        val result = cloudStorageProcess.deleteCloudStorageDevice(driveId).run.run
        result must beAnInstanceOf[Answer[Unit, CloudStorageProcessException]]
      }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData with WithErrorDriveServices {

        val result = cloudStorageProcess.deleteCloudStorageDevice(driveId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beLike {
              case e: CloudStorageProcessException => e.cause must beSome(driveServicesException)
            }
          }
        }

      }

  }

}