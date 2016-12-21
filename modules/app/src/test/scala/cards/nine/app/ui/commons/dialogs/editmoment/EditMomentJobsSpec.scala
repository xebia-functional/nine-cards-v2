package cards.nine.app.ui.commons.dialogs.editmoment

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.{BroadAction, JobException}
import cards.nine.app.ui.commons.dialogs.editmoment.EditMomentFragment._
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.MomentValues._
import cards.nine.commons.test.data.{CollectionTestData, MomentTestData}
import cards.nine.models.types.NineCardsMoment
import cards.nine.process.collection.CollectionProcess
import cards.nine.process.device.DeviceProcess
import cards.nine.process.moment.MomentProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.commons.test.data.CommonValues._

trait EditMomentJobsSpecification extends TaskServiceSpecification with Mockito {

  trait EditMomentJobsScope extends Scope with MomentTestData with CollectionTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector: Injector = mock[Injector]

    val mockEditMomentUiActions = mock[EditMomentUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val editMomentJobs = new EditMomentJobs(mockEditMomentUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def sendBroadCastTask(broadAction: BroadAction) = TaskService.empty

    }
  }

}

class EditMomentJobsSpec extends EditMomentJobsSpecification {

  sequential
  "initialize" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      mockTrackEventProcess.editMoment(any) returns serviceRight(Unit)
      mockMomentProcess.getMomentByType(any) returns serviceRight(moment)
      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockEditMomentUiActions.initialize(any, any) returns serviceRight(Unit)

      editMomentJobs.initialize(NineCardsMoment.defaultMoment).mustRightUnit

      there was one(mockTrackEventProcess).editMoment(NineCardsMoment.defaultMoment.name)
      there was one(mockMomentProcess).getMomentByType(NineCardsMoment.defaultMoment)
      there was one(mockCollectionProcess).getCollections
      there was one(mockEditMomentUiActions).initialize(moment, seqCollection)

    }
  }

  "momentNotFound" should {
    "call to close" in new EditMomentJobsScope {

      mockEditMomentUiActions.close() returns serviceRight(Unit)
      editMomentJobs.momentNotFound().mustRightUnit
      there was one(mockEditMomentUiActions).close()
    }
  }

  "setCollectionId" should {
    "returns a valid response when the service returns a right response and modifiedMoment have collectionId equal collectionId" in new EditMomentJobsScope {

      mockTrackEventProcess.quickAccessToCollection() returns serviceRight(Unit)

      editMomentJobs.setCollectionId(Option(collectionId)).mustRightUnit

      there was one(mockTrackEventProcess).quickAccessToCollection()
      statuses.modifiedMoment map (_.collectionId shouldEqual Option(collectionId))
    }

    "returns a valid response when the service returns a right response and modifiedMoment have collectionId equal None" in new EditMomentJobsScope {

      mockTrackEventProcess.quickAccessToCollection() returns serviceRight(Unit)

      editMomentJobs.setCollectionId(Option(0)).mustRightUnit

      there was one(mockTrackEventProcess).quickAccessToCollection()
      statuses.modifiedMoment map (_.collectionId shouldEqual None)
    }
  }

  "swapDay" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      mockEditMomentUiActions.reloadDays(any, any) returns serviceRight(Unit)
      editMomentJobs.swapDay(position, day).mustRightUnit

    }

    "returns a JobException when timeslot not found" in new EditMomentJobsScope {

      editMomentJobs.swapDay(noFoundPosition, day).mustLeft[JobException]

    }
  }

  "changeFromHour" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      mockEditMomentUiActions.reloadDays(any, any) returns serviceRight(Unit)
      editMomentJobs.changeFromHour(position, hour).mustRightUnit

    }

    "returns a JobException when timeslot not found" in new EditMomentJobsScope {

      editMomentJobs.changeFromHour(noFoundPosition, hour).mustLeft[JobException]

    }
  }

  "changeToHour" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      mockEditMomentUiActions.reloadDays(any, any) returns serviceRight(Unit)
      editMomentJobs.changeToHour(position, hour).mustRightUnit

    }

    "returns a JobException when timeslot not found" in new EditMomentJobsScope {

      editMomentJobs.changeToHour(noFoundPosition, hour).mustLeft[JobException]

    }
  }

  "addHour" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = Option(moment))
      mockTrackEventProcess.setHours() returns serviceRight(Unit)
      mockEditMomentUiActions.loadHours(any) returns serviceRight(Unit)

      editMomentJobs.addHour().mustRightUnit

      there was one(mockTrackEventProcess).setHours()
      there was one(mockEditMomentUiActions).loadHours(any)
    }

    "shows a message when the item is duplicated" in new EditMomentJobsScope {

      statuses = statuses.copy(
        modifiedMoment = statuses.modifiedMoment map (_.copy(timeslot = Seq(newTimeslot))))

      mockEditMomentUiActions.showItemDuplicatedMessage() returns serviceRight(Unit)
      editMomentJobs.addHour().mustRightUnit
      there was one(mockEditMomentUiActions).showItemDuplicatedMessage()
    }

    "shows a error message when the modifiedMoment is None" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = None)
      mockTrackEventProcess.setHours() returns serviceRight(Unit)
      mockEditMomentUiActions.showSavingMomentErrorMessage() returns serviceRight(Unit)
      editMomentJobs.addHour().mustRightUnit
      there was one(mockEditMomentUiActions).showSavingMomentErrorMessage()
    }
  }

  "removeHour" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = Option(moment))
      mockEditMomentUiActions.loadHours(any) returns serviceRight(Unit)
      editMomentJobs.removeHour(position).mustRightUnit
      there was one(mockEditMomentUiActions).loadHours(any)
    }

    "shows a error message when the modifiedMoment is None" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = None)
      mockEditMomentUiActions.showSavingMomentErrorMessage() returns serviceRight(Unit)

      editMomentJobs.removeHour(position).mustRightUnit

      there was one(mockEditMomentUiActions).showSavingMomentErrorMessage()
    }
  }

  "addWifi" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      mockDeviceProcess.getConfiguredNetworks(any) returns serviceRight(wifiSeq)
      mockEditMomentUiActions.showWifiDialog(any) returns serviceRight(Unit)

      editMomentJobs.addWifi().mustRightUnit

      there was one(mockDeviceProcess).getConfiguredNetworks(any)
      there was one(mockEditMomentUiActions).showWifiDialog(wifiSeq)
    }
  }
  "addWifi" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = Option(moment.copy(wifi = Seq.empty)))
      mockTrackEventProcess.setWifi() returns serviceRight(Unit)
      mockEditMomentUiActions.loadWifis(any) returns serviceRight(Unit)

      editMomentJobs.addWifi(wifiSeq.head).mustRightUnit

      there was one(mockTrackEventProcess).setWifi()
      there was one(mockEditMomentUiActions).loadWifis(any)
    }

    "shows a message when the item is duplicated" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = Option(moment.copy(wifi = wifiSeq)))
      mockEditMomentUiActions.showItemDuplicatedMessage() returns serviceRight(Unit)
      editMomentJobs.addWifi(wifiSeq.head).mustRightUnit
      there was one(mockEditMomentUiActions).showItemDuplicatedMessage()
    }

    "shows a error message when the modifiedMoment is None" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = None)
      mockTrackEventProcess.setWifi() returns serviceRight(Unit)
      mockEditMomentUiActions.showSavingMomentErrorMessage() returns serviceRight(Unit)
      editMomentJobs.addWifi(wifiSeq.head).mustRightUnit
      there was one(mockEditMomentUiActions).showSavingMomentErrorMessage()
    }
  }

  "removeWifi" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = Option(moment))
      mockEditMomentUiActions.loadWifis(any) returns serviceRight(Unit)
      editMomentJobs.removeWifi(position).mustRightUnit
      there was one(mockEditMomentUiActions).loadWifis(any)
    }

    "shows a error message when the modifiedMoment is None" in new EditMomentJobsScope {

      statuses = statuses.copy(modifiedMoment = None)
      mockEditMomentUiActions.showSavingMomentErrorMessage() returns serviceRight(Unit)
      editMomentJobs.removeWifi(position).mustRightUnit
      there was one(mockEditMomentUiActions).showSavingMomentErrorMessage()
    }
  }

  "saveMoment" should {
    "returns a valid response when the service returns a right response" in new EditMomentJobsScope {

      statuses = statuses.copy(moment = None, modifiedMoment = Option(moment))
      mockMomentProcess.updateMoment(any)(any) returns serviceRight(Unit)
      mockEditMomentUiActions.close() returns serviceRight(Unit)

      editMomentJobs.saveMoment().mustRightUnit

      there was one(mockMomentProcess).updateMoment(any)(any)
      there was one(mockEditMomentUiActions).close()

    }
    "call to close when wasn't modified the moment" in new EditMomentJobsScope {

      statuses = statuses.copy(moment = None, modifiedMoment = None)
      mockEditMomentUiActions.close() returns serviceRight(Unit)
      editMomentJobs.saveMoment().mustRightUnit
      there was one(mockEditMomentUiActions).close()
    }
  }
}
