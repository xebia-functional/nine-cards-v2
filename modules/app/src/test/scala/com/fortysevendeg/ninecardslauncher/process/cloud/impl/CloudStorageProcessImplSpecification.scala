package com.fortysevendeg.ninecardslauncher.process.cloud.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.services.drive.{DriveServices, DriveServicesException}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AndroidIdNotFoundException, PersistenceServiceException, PersistenceServices}
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

  val driveServicesException = DriveServicesException("")

  val persistenceServicesException = PersistenceServiceException("")

  val androidIdNotFoundException = AndroidIdNotFoundException("")

  val sampleId = "android-id"

  trait CloudStorageProcessImplScope
    extends Scope {

    implicit val context = mock[ContextSupport]

    val driveServices = mock[DriveServices]

    val persistenceServices = mock[PersistenceServices]

    persistenceServices.getAndroidId returns Service(Task(Answer(sampleId)))

    val cloudStorageProcess = new CloudStorageProcessImpl(driveServices, persistenceServices)

  }

  trait WithErrorServices {

    self: CloudStorageProcessImplScope =>

    driveServices.listFiles(any) returns Service(Task(Errata(driveServicesException)))
    driveServices.fileExists(any) returns Service(Task(Errata(driveServicesException)))
    driveServices.readFile(any) returns Service(Task(Errata(driveServicesException)))
    driveServices.deleteFile(any) returns Service(Task(Errata(driveServicesException)))
    driveServices.createFile(anyString, anyString, anyString, anyString, anyString) returns Service(Task(Errata(driveServicesException)))
    driveServices.updateFile(anyString, anyString) returns Service(Task(Errata(driveServicesException)))

    persistenceServices.findUserById(any) returns Service(Task(Errata(persistenceServicesException)))
    persistenceServices.getAndroidId returns Service(Task(Errata(androidIdNotFoundException)))

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

  "prepareForActualDevice" should {

    "return the actual device and an empty list when passing only one element that corresponds to the device id" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        persistenceServices.getAndroidId returns Service(Task(Answer(deviceId)))

        val cloudStorageDevice = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = deviceId)

        val result = cloudStorageProcess.prepareForActualDevice(Seq(cloudStorageDevice)).run.run
        result must beLike {
          case Answer((maybeUserDevice, devices)) =>
            maybeUserDevice must beSome(cloudStorageDevice)
        }

      }

    "return an empty option and an empty list when passing a empty list" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        persistenceServices.getAndroidId returns Service(Task(Answer(deviceId)))

        val result = cloudStorageProcess.prepareForActualDevice(Seq.empty).run.run
        result must beLike {
          case Answer((maybeUserDevice, devices)) =>
            maybeUserDevice must beNone
            devices must beEmpty
        }

      }

    "return an empty option and an empty list when passing only one element that not corresponds to the device id" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        persistenceServices.getAndroidId returns Service(Task(Answer(deviceId)))

        val cloudStorageDevice = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = anotherDeviceId)

        val result = cloudStorageProcess.prepareForActualDevice(Seq(cloudStorageDevice)).run.run
        result must beLike {
          case Answer((maybeUserDevice, devices)) =>
            maybeUserDevice must beNone
        }

      }

    "return the newest device when passing two elements that correspond with the device id" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        persistenceServices.getAndroidId returns Service(Task(Answer(deviceId)))

        val cloudStorageDevice1 = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = deviceId)

        val cloudStorageDevice2 = generateCloudStorageDevice(
          cloudId = anotherCloudId,
          minusDays = 0,
          deviceId = deviceId)

        val result = cloudStorageProcess.prepareForActualDevice(Seq(cloudStorageDevice1, cloudStorageDevice2)).run.run
        result must beLike {
          case Answer((maybeUserDevice, devices)) =>
            maybeUserDevice must beSome(cloudStorageDevice2)
            devices shouldEqual Seq(cloudStorageDevice1)
        }

      }

    "return the actual device and a sorted list when passing some elements and one of them correspond with the device id" in
      new CloudStorageProcessImplScope with CloudStorageProcessImplData {

        persistenceServices.getAndroidId returns Service(Task(Answer(deviceId)))

        val cloudStorageDevice = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = deviceId)

        val cloudStorageDeviceLast = generateCloudStorageDevice(
          cloudId = anotherCloudId,
          minusDays = 3,
          deviceId = anotherDeviceId)

        val cloudStorageDeviceFirst = generateCloudStorageDevice(
          cloudId = anotherCloudId,
          minusDays = 1,
          deviceId = anotherDeviceId)

        val cloudStorageDeviceMiddle = generateCloudStorageDevice(anotherCloudId, 2, anotherDeviceId)

        val allDevices = Seq(cloudStorageDeviceMiddle, cloudStorageDevice, cloudStorageDeviceLast, cloudStorageDeviceFirst)

        val result = cloudStorageProcess.prepareForActualDevice(allDevices).run.run
        result must beLike {
          case Answer((maybeUserDevice, devices)) =>
            maybeUserDevice must beSome(cloudStorageDevice)
            devices shouldEqual Seq(cloudStorageDeviceFirst, cloudStorageDeviceMiddle, cloudStorageDeviceLast)
        }

      }

    "return a CloudStorageProcessException when the service returns an exception" in
      new CloudStorageProcessImplScope with WithErrorServices with CloudStorageProcessImplData {

        val result = cloudStorageProcess.prepareForActualDevice(Seq.empty).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beLike {
              case e: CloudStorageProcessException => e.cause must beSome(androidIdNotFoundException)
            }
          }
        }

      }

    "getCloudStorageDevices" should {

      "return a sequence of CloudStorageResource when the service returns a valid response" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          driveServices.listFiles(any) returns Service(Task(Answer(driveServiceFileSummarySeq)))

          persistenceServices.findUserById(any) returns Service(Task(Answer(Some(user))))

          val result = cloudStorageProcess.getCloudStorageDevices.run.run
          result must beLike {
            case Answer(resultSeqCollection) =>
              resultSeqCollection.size shouldEqual driveServiceFileSummarySeq.size
              resultSeqCollection.map(_.deviceName) shouldEqual driveServiceFileSummarySeq.map(_.title)
              resultSeqCollection.map(_.cloudId) shouldEqual driveServiceFileSummarySeq.map(_.uuid)
          }

        }

      "return an empty sequence when the service returns a valid empty sequence" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          driveServices.listFiles(any) returns Service(Task(Answer(driveServiceFileSummaryEmptySeq)))

          persistenceServices.findUserById(any) returns Service(Task(Answer(Some(user))))

          val result = cloudStorageProcess.getCloudStorageDevices.run.run
          result must beLike {
            case Answer(resultSeqCollection) =>
              resultSeqCollection must beEmpty
          }

        }

      "return a CloudStorageProcessException when the service return an exception" in
        new CloudStorageProcessImplScope with WithErrorServices with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          persistenceServices.findUserById(any) returns Service(Task(Answer(Some(user))))

          val result = cloudStorageProcess.getCloudStorageDevices.run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beLike {
                case e: CloudStorageProcessException => e.cause must beSome(driveServicesException)
              }
            }
          }

        }

      "return a CloudStorageProcessException when there isn't a active user id" in
        new CloudStorageProcessImplScope with WithErrorServices {

          context.getActiveUserId returns None

          val result = cloudStorageProcess.getCloudStorageDevices.run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beAnInstanceOf[CloudStorageProcessException]
            }
          }

        }

      "return a CloudStorageProcessException when a user with this id doesn't exists" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          driveServices.listFiles(any) returns Service(Task(Answer(driveServiceFileSummaryEmptySeq)))

          persistenceServices.findUserById(any) returns Service(Task(Answer(None)))

          val result = cloudStorageProcess.getCloudStorageDevices.run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beAnInstanceOf[CloudStorageProcessException]
            }
          }

        }

      "return a CloudStorageProcessException when the persistence service throws an exception" in
        new CloudStorageProcessImplScope with WithErrorServices with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          driveServices.listFiles(any) returns Service(Task(Answer(driveServiceFileSummaryEmptySeq)))

          val result = cloudStorageProcess.getCloudStorageDevices.run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beLike {
                case e: CloudStorageProcessException => e.cause must beSome(persistenceServicesException)
              }
            }
          }

        }

    }

    "getCloudStorageDevice" should {

      "return a valid CloudStorageDevice when the service returns a valid Json" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          driveServices.readFile(cloudId) returns Service(Task(Answer(driveServiceFile)))

          val result = cloudStorageProcess.getCloudStorageDevice(cloudId).run.run
          result must beLike {
            case Answer(device) =>
              device.data.deviceId shouldEqual deviceId
              device.data.deviceName shouldEqual deviceName
              device.data.collections.size shouldEqual numCollections
              device.data.collections.map(_.items.size) shouldEqual Seq.fill(numCollections)(numItemsPerCollection)
          }
        }

      "return a CloudStorageProcessException when the service return a non valid Json" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          driveServices.readFile(cloudId) returns Service(Task(Answer(invalidDriveServiceFileJson)))

          val result = cloudStorageProcess.getCloudStorageDevice(cloudId).run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beAnInstanceOf[CloudStorageProcessException]
            }
          }
        }

      "return a CloudStorageProcessException when the service return an exception" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData with WithErrorServices {

          val result = cloudStorageProcess.getCloudStorageDevice(cloudId).run.run
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

      "call to create file in Service with a valid Json" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          driveServices.createFile(
            anyString,
            anArgThat[String, String](new JsonMatcher(validCloudStorageDeviceJson)),
            anyString,
            anyString,
            anyString) returns Service(Task(Answer(driveServiceFileSummary)))

          val cloudStorageServiceData = generateCloudStorageDeviceData()

          cloudStorageProcess.createCloudStorageDevice(cloudStorageServiceData).run.run
        }

      "return a CloudStorageProcessException when the service return an exception" in
        new CloudStorageProcessImplScope with WithErrorServices with CloudStorageProcessImplData {

          val result = cloudStorageProcess.createCloudStorageDevice(generateCloudStorageDeviceData()).run.run
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

      "call to create file in Service with a valid Json when the user doesn't has a cloudId" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          persistenceServices.findUserById(any) returns Service(Task(Answer(Some(user.copy(deviceCloudId = None)))))

          driveServices.createFile(
            anyString,
            anyString,
            anyString,
            anyString,
            anyString) returns Service(Task(Answer(driveServiceFileSummary)))

          val cloudStorageServiceData = generateCloudStorageDeviceData()

          cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageServiceData.collections,
            cloudStorageServiceData.moments getOrElse Seq.empty,
            cloudStorageServiceData.dockApps getOrElse Seq.empty).run.run
        }

      "call to update file in Service with a valid Json when the file does exists" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          persistenceServices.findUserById(any) returns Service(Task(Answer(Some(user))))

          driveServices.fileExists(cloudId) returns Service(Task(Answer(true)))

          driveServices.updateFile(
            anyString,
            anyString) returns Service(Task(Answer(driveServiceFileSummary)))

          val cloudStorageServiceData = generateCloudStorageDeviceData()

          cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageServiceData.collections,
            cloudStorageServiceData.moments getOrElse Seq.empty,
            cloudStorageServiceData.dockApps getOrElse Seq.empty).run.run
        }

      "call to create file in Service with a valid Json when the file doesn't exists" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          persistenceServices.findUserById(any) returns Service(Task(Answer(Some(user))))

          driveServices.fileExists(cloudId) returns Service(Task(Answer(false)))

          driveServices.createFile(
            anyString,
            anyString,
            anyString,
            anyString,
            anyString) returns Service(Task(Answer(driveServiceFileSummary)))

          val cloudStorageServiceData = generateCloudStorageDeviceData()

          cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageServiceData.collections,
            cloudStorageServiceData.moments getOrElse Seq.empty,
            cloudStorageServiceData.dockApps getOrElse Seq.empty).run.run
        }

      "return a CloudStorageProcessException when the user does exists" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          persistenceServices.findUserById(any) returns Service(Task(Answer(None)))

          val cloudStorageServiceData = generateCloudStorageDeviceData()

          val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageServiceData.collections,
            cloudStorageServiceData.moments getOrElse Seq.empty,
            cloudStorageServiceData.dockApps getOrElse Seq.empty).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beAnInstanceOf[CloudStorageProcessException]
            }
          }
        }

      "return a CloudStorageProcessException when the drive service return an exception" in
        new CloudStorageProcessImplScope with WithErrorServices with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          persistenceServices.findUserById(any) returns Service(Task(Answer(Some(user))))

          val cloudStorageServiceData = generateCloudStorageDeviceData()

          val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageServiceData.collections,
            cloudStorageServiceData.moments getOrElse Seq.empty,
            cloudStorageServiceData.dockApps getOrElse Seq.empty).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beLike {
                case e: CloudStorageProcessException => e.cause must beSome(androidIdNotFoundException)
              }
            }
          }

        }

      "return a CloudStorageProcessException when the persistence service return an exception" in
        new CloudStorageProcessImplScope with WithErrorServices with CloudStorageProcessImplData {

          context.getActiveUserId returns Some(activeUserId)

          val cloudStorageServiceData = generateCloudStorageDeviceData()

          val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageServiceData.collections,
            cloudStorageServiceData.moments getOrElse Seq.empty,
            cloudStorageServiceData.dockApps getOrElse Seq.empty).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beLike {
                case e: CloudStorageProcessException => e.cause must beSome(androidIdNotFoundException)
              }
            }
          }

        }

      "return a CloudStorageProcessException when there isn't a active user id" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          context.getActiveUserId returns None

          val cloudStorageServiceData = generateCloudStorageDeviceData()

          val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
            cloudStorageServiceData.collections,
            cloudStorageServiceData.moments getOrElse Seq.empty,
            cloudStorageServiceData.dockApps getOrElse Seq.empty).run.run

          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, exception)) => exception must beAnInstanceOf[CloudStorageProcessException]
            }
          }

        }

    }

    "deleteCloudStorageDeviceByAndroidId" should {

      "return a valid response when the service finds the device" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData {

          driveServices.deleteFile(cloudId) returns Service(Task(Answer(Unit)))

          val result = cloudStorageProcess.deleteCloudStorageDevice(cloudId).run.run
          result must beAnInstanceOf[Answer[Unit, CloudStorageProcessException]]
        }

      "return a CloudStorageProcessException when the service returns an exception" in
        new CloudStorageProcessImplScope with CloudStorageProcessImplData with WithErrorServices {

          val result = cloudStorageProcess.deleteCloudStorageDevice(cloudId).run.run
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

}