package com.fortysevendeg.ninecardslauncher.process.cloud.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.cloud.CloudStorageProcessException
import com.fortysevendeg.ninecardslauncher.process.cloud.models.RawCloudStorageDevice
import com.fortysevendeg.ninecardslauncher.services.drive.{DriveServices, DriveServicesException}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AndroidIdNotFoundException, PersistenceServiceException, PersistenceServices}
import monix.eval.Task
import org.hamcrest.{Description, TypeSafeMatcher}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import cats.syntax.either._
import com.google.android.gms.common.api.GoogleApiClient

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

    val mockApiClient = mock[GoogleApiClient]

    persistenceServices.getAndroidId returns TaskService(Task(Either.right(sampleId)))

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

        persistenceServices.getAndroidId returns TaskService(Task(Either.right(deviceId)))

        val cloudStorageDevice = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = deviceId)

        val result = cloudStorageProcess.prepareForActualDevice(mockApiClient, Seq(cloudStorageDevice)).value.run
        result shouldEqual Right(Some(cloudStorageDevice), Seq.empty)

      }

    "return an empty option and an empty list when passing a empty list" in
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Either.right(deviceId)))

        val result = cloudStorageProcess.prepareForActualDevice(mockApiClient, Seq.empty).value.run
        result shouldEqual Right(None, Seq.empty)

      }

    "return an empty option and an empty list when passing only one element that not corresponds to the device id" in
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Either.right(deviceId)))

        val cloudStorageDevice = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = anotherDeviceId)

        val result = cloudStorageProcess.prepareForActualDevice(mockApiClient, Seq(cloudStorageDevice)).value.run
        result must beLike {
          case Right((maybeUserDevice, devices)) =>
            maybeUserDevice must beNone
        }

      }

    "return the newest device when passing two elements that correspond with the device id" in
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Either.right(deviceId)))

        val cloudStorageDevice1 = generateCloudStorageDevice(
          cloudId = cloudId,
          minusDays = 1,
          deviceId = deviceId)

        val cloudStorageDevice2 = generateCloudStorageDevice(
          cloudId = anotherCloudId,
          minusDays = 0,
          deviceId = deviceId)

        val result = cloudStorageProcess.prepareForActualDevice(mockApiClient, Seq(cloudStorageDevice1, cloudStorageDevice2)).value.run
        result shouldEqual Right(Some(cloudStorageDevice2), Seq(cloudStorageDevice1))

      }

    "return the actual device and a sorted list when passing some elements and one of them correspond with the device id" in
      new CloudStorageProcessImplScope {

        persistenceServices.getAndroidId returns TaskService(Task(Either.right(deviceId)))

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

        val result = cloudStorageProcess.prepareForActualDevice(mockApiClient, allDevices).value.run
        result shouldEqual Right(Some(cloudStorageDevice), Seq(cloudStorageDeviceFirst, cloudStorageDeviceMiddle, cloudStorageDeviceLast))

      }

    "return a CloudStorageProcessException when the service returns an exception" in
      new CloudStorageProcessImplScope  {

        persistenceServices.getAndroidId returns TaskService(Task(Either.left(androidIdNotFoundException)))
        val result = cloudStorageProcess.prepareForActualDevice(mockApiClient, Seq.empty).value.run
        result must beAnInstanceOf[Left[AndroidIdNotFoundException, _]]
      }
  }

  "getCloudStorageDevices" should {

    "return a sequence of CloudStorageResource when the service returns a valid response" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        driveServices.listFiles(any, any) returns TaskService(Task(Either.right(driveServiceFileSummarySeq)))
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))

        val result = cloudStorageProcess.getCloudStorageDevices(mockApiClient).value.run
        result must beLike {
          case Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual driveServiceFileSummarySeq.size
            resultSeqCollection.map(_.deviceName) shouldEqual driveServiceFileSummarySeq.map(_.title)
            resultSeqCollection.map(_.cloudId) shouldEqual driveServiceFileSummarySeq.map(_.uuid)
        }

      }

    "return an empty sequence when the service returns a valid empty sequence" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        driveServices.listFiles(any, any) returns TaskService(Task(Either.right(driveServiceFileSummaryEmptySeq)))
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))

        val result = cloudStorageProcess.getCloudStorageDevices(mockApiClient).value.run
        result shouldEqual Right(Seq.empty)
      }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.listFiles(any, any) returns TaskService(Task(Either.left(driveServicesException)))
        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))

        val result = cloudStorageProcess.getCloudStorageDevices(mockApiClient).value.run
        result must beAnInstanceOf[Left[DriveServicesException, _]]
      }

    "return a CloudStorageProcessException when there isn't a active user id" in
      new CloudStorageProcessImplScope  {

        context.getActiveUserId returns None

        val result = cloudStorageProcess.getCloudStorageDevices(mockApiClient).value.run
        result must beAnInstanceOf[Left[CloudStorageProcessException, _]]
      }

    "return a CloudStorageProcessException when a user with this id doesn't exists" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        driveServices.listFiles(any, any) returns TaskService(Task(Either.right(driveServiceFileSummaryEmptySeq)))
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(None)))

        val result = cloudStorageProcess.getCloudStorageDevices(mockApiClient).value.run
        result must beAnInstanceOf[Left[CloudStorageProcessException, _]]
      }

    "return a CloudStorageProcessException when the persistence service throws an exception" in
      new CloudStorageProcessImplScope  {

        persistenceServices.findUserById(any) returns TaskService(Task(Either.left(persistenceServicesException)))
        context.getActiveUserId returns Some(activeUserId)
        driveServices.listFiles(any, any) returns TaskService(Task(Either.right(driveServiceFileSummaryEmptySeq)))

        val result = cloudStorageProcess.getCloudStorageDevices(mockApiClient).value.run
        result must beAnInstanceOf[Left[PersistenceServiceException, _]]
      }

  }

  "getCloudStorageDevice" should {

    "return a valid CloudStorageDevice when the service returns a valid Json" in
      new CloudStorageProcessImplScope {

        driveServices.readFile(any, any) returns TaskService(Task(Either.right(driveServiceFile)))

        val result = cloudStorageProcess.getCloudStorageDevice(mockApiClient, cloudId).value.run
        result must beLike {
          case Right(device) =>
            device.data.deviceId shouldEqual deviceId
            device.data.deviceName shouldEqual deviceName
            device.data.collections.size shouldEqual numCollections
            device.data.collections.map(_.items.size) shouldEqual Seq.fill(numCollections)(numItemsPerCollection)
        }
      }

    "return a CloudStorageProcessException when the service return a non valid Json" in
      new CloudStorageProcessImplScope {

        driveServices.readFile(any, any) returns TaskService(Task(Either.right(invalidDriveServiceFileJson)))
        val result = cloudStorageProcess.getCloudStorageDevice(mockApiClient, cloudId).value.run
        result must beAnInstanceOf[Left[CloudStorageProcessException, _]]
      }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.readFile(any, any) returns TaskService(Task(Either.left(driveServicesException)))
        val result = cloudStorageProcess.getCloudStorageDevice(mockApiClient, cloudId).value.run
        result must beAnInstanceOf[Left[DriveServicesException, _]]
      }

  }

  "createOrUpdateCloudStorageDevice" should {

    "call to create file in Service with a valid Json and None for cloudId" in
      new CloudStorageProcessImplScope {

        driveServices.createFile(
          any,
          anyString,
          anArgThat[String, String](new JsonMatcher(validCloudStorageDeviceJson)),
          anyString,
          anyString,
          anyString) returns TaskService(Task(Either.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createOrUpdateCloudStorageDevice(mockApiClient, None, cloudStorageServiceData).value.run
      }

    "call to create file in Service with a valid Json and a cloudId" in
      new CloudStorageProcessImplScope {

        driveServices.updateFile(
          any,
          anyString,
          anyString,
          anyString) returns TaskService(Task(Either.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createOrUpdateCloudStorageDevice(mockApiClient, Some(cloudId), cloudStorageServiceData).value.run
      }

    "return a CloudStorageProcessException when the service return an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.createFile(any, anyString, anyString, anyString, anyString, anyString) returns TaskService(Task(Either.left(driveServicesException)))
        val result = cloudStorageProcess.createOrUpdateCloudStorageDevice(mockApiClient, None, generateCloudStorageDeviceData()).value.run
        result must beAnInstanceOf[Left[DriveServicesException, _]]
      }

  }

  "createOrUpdateActualCloudStorageDevice" should {

    "call to create file in Service with a valid Json when the user doesn't has a cloudId" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user.copy(deviceCloudId = None)))))
        driveServices.createFile(
          any,
          anyString,
          anyString,
          anyString,
          anyString,
          anyString) returns TaskService(Task(Either.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          mockApiClient,
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run
      }

    "call to update file in Service with a valid Json when the file does exists" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        driveServices.fileExists(any, any) returns TaskService(Task(Either.right(true)))
        driveServices.updateFile(
          any,
          anyString,
          anyString,
          anyString) returns TaskService(Task(Either.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          mockApiClient,
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run
      }

    "call to create file in Service with a valid Json when the file doesn't exists" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        driveServices.fileExists(any, any) returns TaskService(Task(Either.right(false)))
        driveServices.createFile(
          any,
          anyString,
          anyString,
          anyString,
          anyString,
          anyString) returns TaskService(Task(Either.right(driveServiceFileSummary)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          mockApiClient,
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run
      }

    "return a CloudStorageProcessException when the user does exists" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(None)))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          mockApiClient,
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run

        result must beAnInstanceOf[Left[CloudStorageProcessException, _]]

      }

    "return a CloudStorageProcessException when the drive service return an exception" in
      new CloudStorageProcessImplScope  {

        persistenceServices.getAndroidId returns TaskService(Task(Either.left(androidIdNotFoundException)))
        driveServices.updateFile(any, anyString, anyString, anyString) returns TaskService(Task(Either.left(driveServicesException)))
        context.getActiveUserId returns Some(activeUserId)
        persistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          mockApiClient,
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run

        result must beAnInstanceOf[Left[AndroidIdNotFoundException, _]]

      }

    "return a CloudStorageProcessException when the persistence service return an exception" in
      new CloudStorageProcessImplScope  {

        persistenceServices.getAndroidId returns TaskService(Task(Either.left(androidIdNotFoundException)))
        context.getActiveUserId returns Some(activeUserId)

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          mockApiClient,
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run

        result must beAnInstanceOf[Left[AndroidIdNotFoundException, _]]

      }

    "return a CloudStorageProcessException when there isn't a active user id" in
      new CloudStorageProcessImplScope {

        context.getActiveUserId returns None

        val cloudStorageServiceData = generateCloudStorageDeviceData()

        val result = cloudStorageProcess.createOrUpdateActualCloudStorageDevice(
          mockApiClient,
          cloudStorageServiceData.collections,
          cloudStorageServiceData.moments getOrElse Seq.empty,
          cloudStorageServiceData.dockApps getOrElse Seq.empty).value.run

        result must beAnInstanceOf[Left[CloudStorageProcessException, _]]
      }

  }

  "deleteCloudStorageDeviceByAndroidId" should {

    "return a valid response when the service finds the device" in
      new CloudStorageProcessImplScope {

        driveServices.deleteFile(any, any) returns TaskService(Task(Either.right(Unit)))
        val result = cloudStorageProcess.deleteCloudStorageDevice(mockApiClient, cloudId).value.run
        result must beAnInstanceOf[Right[_, Unit]]
      }

    "return a CloudStorageProcessException when the service returns an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.deleteFile(any, any) returns TaskService(Task(Either.left(driveServicesException)))
        val result = cloudStorageProcess.deleteCloudStorageDevice(mockApiClient, cloudId).value.run
        result must beAnInstanceOf[Left[DriveServicesException, _]]
      }

  }

  "getRawCloudStorageDevice" should {

    "return a valid response when the service finds the device" in
      new CloudStorageProcessImplScope {

        driveServices.readFile(any, any) returns TaskService(Task(Either.right(driveServiceFile)))
        val expected = RawCloudStorageDevice(
          cloudId = cloudId,
          uuid = driveServiceFile.summary.uuid,
          deviceId = driveServiceFile.summary.deviceId,
          title = driveServiceFile.summary.title,
          createdDate = driveServiceFile.summary.createdDate,
          modifiedDate = driveServiceFile.summary.modifiedDate,
          json = driveServiceFile.content)
        val result = cloudStorageProcess.getRawCloudStorageDevice(mockApiClient, cloudId).value.run
        result shouldEqual Right(expected)
      }

    "return a CloudStorageProcessException when the service returns an exception" in
      new CloudStorageProcessImplScope  {

        driveServices.readFile(any, any) returns TaskService(Task(Either.left(driveServicesException)))
        val result = cloudStorageProcess.getRawCloudStorageDevice(mockApiClient, cloudId).value.run
        result must beAnInstanceOf[Left[DriveServicesException, _]]
      }

  }

}