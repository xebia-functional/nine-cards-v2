package com.fortysevendeg.ninecardslauncher.process.cloud.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.services.drive.{DriveServices, DriveServicesException}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AndroidIdNotFoundException, PersistenceServiceException, PersistenceServices}
import org.hamcrest.{Description, TypeSafeMatcher}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json

import scalaz.concurrent.Task

trait CloudStorageProcessImplSpecification
  extends Specification
    with Mockito {

  val driveServicesException = DriveServicesException("")

  val persistenceServicesException = PersistenceServiceException("")

  val androidIdNotFoundException = AndroidIdNotFoundException("")

  val sampleId = "android-id"

  trait CloudStorageProcessImplScope
    extends Scope
      with CloudStorageProcessImplData {

    implicit val context = mock[ContextSupport]

    val driveServices = mock[DriveServices]

    val persistenceServices = mock[PersistenceServices]

    persistenceServices.getAndroidId returns TaskService(Task(Xor.right(sampleId)))

    val cloudStorageProcess = new CloudStorageProcessImpl(driveServices, persistenceServices)

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
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Xor.right(deviceId)))

        val cloudStorageDevice = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = deviceId)

        val result = cloudStorageProcess.prepareForActualDevice(Seq(cloudStorageDevice)).value.run
        result shouldEqual Xor.Right(Some(cloudStorageDevice), Seq.empty)

      }

    "return an empty option and an empty list when passing a empty list" in
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Xor.right(deviceId)))

        val result = cloudStorageProcess.prepareForActualDevice(Seq.empty).value.run
        result shouldEqual Xor.Right(None, Seq.empty)

      }

    "return an empty option and an empty list when passing only one element that not corresponds to the device id" in
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Xor.right(deviceId)))

        val cloudStorageDevice = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = anotherDeviceId)

        val result = cloudStorageProcess.prepareForActualDevice(Seq(cloudStorageDevice)).value.run
        result must beLike {
          case Xor.Right((maybeUserDevice, devices)) =>
            maybeUserDevice must beNone
        }

      }

    "return the newest device when passing two elements that correspond with the device id" in
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Xor.right(deviceId)))

        val cloudStorageDevice1 = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = deviceId)

        val cloudStorageDevice2 = generateCloudStorageDevice(
          cloudId = anotherCloudId,
          minusDays = 0,
          deviceId = deviceId)

        val result = cloudStorageProcess.prepareForActualDevice(Seq(cloudStorageDevice1, cloudStorageDevice2)).value.run
        result shouldEqual Xor.Right(Some(cloudStorageDevice2), Seq(cloudStorageDevice1))

      }

    "return the actual device and a sorted list when passing some elements and one of them correspond with the device id" in
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Xor.right(deviceId)))

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

        val result = cloudStorageProcess.prepareForActualDevice(allDevices).value.run
        result shouldEqual Xor.Right(Some(cloudStorageDevice), Seq(cloudStorageDeviceFirst, cloudStorageDeviceMiddle, cloudStorageDeviceLast))

      }

    "return a CloudStorageProcessException when the service returns an exception" in
      new CloudStorageProcessImplScope  {

        persistenceServices.getAndroidId returns TaskService(Task(Xor.left(androidIdNotFoundException)))
        val result = cloudStorageProcess.prepareForActualDevice(Seq.empty).value.run
        result must beAnInstanceOf[Xor.Left[AndroidIdNotFoundException]]
      }
  }

  "getCloudStorageDevices" should {

    "return a sequence of CloudStorageResource when the service returns a valid response" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        driveServices.listFiles(any) returns TaskService(Task(Xor.right(driveServiceFileSummarySeq)))
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user))))

        val result = cloudStorageProcess.getCloudStorageDevices.value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual driveServiceFileSummarySeq.size
            resultSeqCollection.map(_.deviceName) shouldEqual driveServiceFileSummarySeq.map(_.title)
            resultSeqCollection.map(_.cloudId) shouldEqual driveServiceFileSummarySeq.map(_.uuid)
        }

      }

    "return an empty sequence when the service returns a valid empty sequence" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        driveServices.listFiles(any) returns TaskService(Task(Xor.right(driveServiceFileSummaryEmptySeq)))
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user))))

        val result = cloudStorageProcess.getCloudStorageDevices.value.run
        result shouldEqual Xor.Right(Seq.empty)
      }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.listFiles(any) returns TaskService(Task(Xor.left(driveServicesException)))
        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user))))

        val result = cloudStorageProcess.getCloudStorageDevices.value.run
        result must beAnInstanceOf[Xor.Left[DriveServicesException]]
      }

    "return a CloudStorageProcessException when there isn't a active user id" in
      new CloudStorageProcessImplScope  {

        context.getActiveUserId returns None

        val result = cloudStorageProcess.getCloudStorageDevices.value.run
        result must beAnInstanceOf[Xor.Left[CloudStorageProcessException]]
      }

    "return a CloudStorageProcessException when a user with this id doesn't exists" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        driveServices.listFiles(any) returns TaskService(Task(Xor.right(driveServiceFileSummaryEmptySeq)))
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(None)))

        val result = cloudStorageProcess.getCloudStorageDevices.value.run
        result must beAnInstanceOf[Xor.Left[CloudStorageProcessException]]
      }

    "return a CloudStorageProcessException when the persistence service throws an exception" in
      new CloudStorageProcessImplScope  {

        persistenceServices.findUserById(any) returns TaskService(Task(Xor.left(persistenceServicesException)))
        context.getActiveUserId returns Some(activeUserId)
        driveServices.listFiles(any) returns TaskService(Task(Xor.right(driveServiceFileSummaryEmptySeq)))

        val result = cloudStorageProcess.getCloudStorageDevices.value.run
        result must beAnInstanceOf[Xor.Left[PersistenceServiceException]]
      }

  }

  "getCloudStorageDevice" should {

    "return a valid CloudStorageDevice when the service returns a valid Json" in
      new CloudStorageProcessImplScope {

        driveServices.readFile(cloudId) returns TaskService(Task(Xor.right(driveServiceFile)))

        val result = cloudStorageProcess.getCloudStorageDevice(cloudId).value.run
        result must beLike {
          case Xor.Right(device) =>
            device.data.deviceId shouldEqual deviceId
            device.data.deviceName shouldEqual deviceName
            device.data.collections.size shouldEqual numCollections
            device.data.collections.map(_.items.size) shouldEqual Seq.fill(numCollections)(numItemsPerCollection)
        }
      }

    "return a CloudStorageProcessException when the service return a non valid Json" in
      new CloudStorageProcessImplScope {

        driveServices.readFile(cloudId) returns TaskService(Task(Xor.right(invalidDriveServiceFileJson)))
        val result = cloudStorageProcess.getCloudStorageDevice(cloudId).value.run
        result must beAnInstanceOf[Xor.Left[CloudStorageProcessException]]
      }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.readFile(any) returns TaskService(Task(Xor.left(driveServicesException)))
        val result = cloudStorageProcess.getCloudStorageDevice(cloudId).value.run
        result must beAnInstanceOf[Xor.Left[DriveServicesException]]
      }

  }

  "createOrUpdateCloudStorageDevice" should {

    "call to create file in Service with a valid Json" in
      new CloudStorageProcessImplScope {

        driveServices.createFile(
          anyString,
          anArgThat[String, String](new JsonMatcher(validCloudStorageDeviceJson)),
          anyString,
          anyString,
          anyString) returns TaskService(Task(Xor.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createCloudStorageDevice(cloudStorageServiceData).value.run
      }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.createFile(anyString, anyString, anyString, anyString, anyString) returns TaskService(Task(Xor.left(driveServicesException)))
        val result = cloudStorageProcess.createCloudStorageDevice(generateCloudStorageDeviceData()).value.run
        result must beAnInstanceOf[Xor.Left[DriveServicesException]]
      }

  }

  "createOrUpdateActualCloudStorageDevice" should {

    "call to create file in Service with a valid Json when the user doesn't has a cloudId" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user.copy(deviceCloudId = None)))))
        driveServices.createFile(
          anyString,
          anyString,
          anyString,
          anyString,
          anyString) returns TaskService(Task(Xor.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run
      }

    "call to update file in Service with a valid Json when the file does exists" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user))))
        driveServices.fileExists(cloudId) returns TaskService(Task(Xor.right(true)))
        driveServices.updateFile(
          anyString,
          anyString) returns TaskService(Task(Xor.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run
      }

    "call to create file in Service with a valid Json when the file doesn't exists" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user))))
        driveServices.fileExists(cloudId) returns TaskService(Task(Xor.right(false)))
        driveServices.createFile(
          anyString,
          anyString,
          anyString,
          anyString,
          anyString) returns TaskService(Task(Xor.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run
      }

    "return a CloudStorageProcessException when the user does exists" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(None)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run

        result must beAnInstanceOf[Xor.Left[CloudStorageProcessException]]

      }

    "return a CloudStorageProcessException when the drive service return an exception" in
      new CloudStorageProcessImplScope  {

        persistenceServices.getAndroidId returns TaskService(Task(Xor.left(androidIdNotFoundException)))
        driveServices.updateFile(anyString, anyString) returns TaskService(Task(Xor.left(driveServicesException)))
        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user))))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run

        result must beAnInstanceOf[Xor.Left[AndroidIdNotFoundException]]

      }

    "return a CloudStorageProcessException when the persistence service return an exception" in
      new CloudStorageProcessImplScope  {

        persistenceServices.getAndroidId returns TaskService(Task(Xor.left(androidIdNotFoundException)))
        context.getActiveUserId returns Some(activeUserId)

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run

        result must beAnInstanceOf[Xor.Left[AndroidIdNotFoundException]]

      }

    "return a CloudStorageProcessException when there isn't a active user id" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns None

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run

        result must beAnInstanceOf[Xor.Left[CloudStorageProcessException]]
      }

  }

  "deleteCloudStorageDeviceByAndroidId" should {

    "return a valid response when the service finds the device" in
      new CloudStorageProcessImplScope {

        driveServices.deleteFile(cloudId) returns TaskService(Task(Xor.right(Unit)))
        val result = cloudStorageProcess.deleteCloudStorageDevice(cloudId).value.run
        result must beAnInstanceOf[Xor.Right[Unit]]
      }

    "return a CloudStorageProcessException when the service returns an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.deleteFile(any) returns TaskService(Task(Xor.left(driveServicesException)))
        val result = cloudStorageProcess.deleteCloudStorageDevice(cloudId).value.run
        result must beAnInstanceOf[Xor.Left[DriveServicesException]]
      }

  }

}