package cards.nine.app.ui.commons.dialogs.contacts

import cards.nine.app.di.Injector
import cards.nine.app.ui.commons.RequestCodes
import cards.nine.app.ui.data.IterableData
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.DeviceTestData
import cards.nine.models.types.AllContacts
import cards.nine.process.device.{ContactException, DeviceProcess}
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.commons.test.data.WizardJobsValues._
import cards.nine.commons.test.data.DeviceValues._

trait ContactsJobsSpecification extends TaskServiceSpecification with Mockito {

  trait ContactsJobsScope extends Scope with DeviceTestData with IterableData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector: Injector = mock[Injector]

    val mockContactsUiActions = mock[ContactsUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val contactsJobs = new ContactsJobs(mockContactsUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

    }
  }

}

class ContactsJobsSpec extends ContactsJobsSpecification {

  "initialize" should {
    "" in new ContactsJobsScope {

      mockContactsUiActions.initialize() returns serviceRight(Unit)
      mockContactsUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getIterableContacts(any)(any) returns serviceRight(iterableContact)
      mockContactsUiActions.showContacts(any) returns serviceRight(Unit)

      contactsJobs.initialize().mustRightUnit

      there was one(mockContactsUiActions).initialize()
      there was one(mockContactsUiActions).showLoading()
    }
  }

  "destroy" should {
    "call to destroy" in new ContactsJobsScope {

      mockContactsUiActions.destroy() returns serviceRight(Unit)
      contactsJobs.destroy().mustRightUnit
      there was one(mockContactsUiActions).destroy()
    }
  }

  "loadContacts" should {
    "returns a valid response when the service returns a right response and hasn't a keyword" in new ContactsJobsScope {

      mockContactsUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getIterableContacts(any)(any) returns serviceRight(iterableContact)
      mockContactsUiActions.showContacts(any) returns serviceRight(Unit)

      contactsJobs.loadContacts(None).mustRightUnit

      there was one(mockContactsUiActions).showLoading()
      there was one(mockDeviceProcess).getIterableContacts(===(AllContacts))(any)
      there was one(mockContactsUiActions).showContacts(any)
    }

    "returns a valid response when the service returns a right response and has a keyword" in new ContactsJobsScope {

      mockContactsUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getIterableContactsByKeyWord(any)(any) returns serviceRight(
        iterableContact)
      mockContactsUiActions.showContacts(any) returns serviceRight(Unit)

      contactsJobs.loadContacts(Option(contactKeyword)).mustRightUnit

      there was one(mockContactsUiActions).showLoading()
      there was one(mockDeviceProcess).getIterableContactsByKeyWord(===(contactKeyword))(any)
      there was one(mockContactsUiActions).showContacts(any)
    }
  }

  "askForContactsPermission" should {
    "call to askForContactsPermission" in new ContactsJobsScope {

      mockContactsUiActions.askForContactsPermission(requestCode) returns serviceRight(Unit)
      contactsJobs.askForContactsPermission(requestCode).mustRightUnit
      there was one(mockContactsUiActions).askForContactsPermission(requestCode)
    }
  }

  "showContact" should {
    "returns a valid response when the service returns a right response" in new ContactsJobsScope {

      mockTrackEventProcess.addContactByFab() returns serviceRight(Unit)
      mockDeviceProcess.getContact(any)(any) returns serviceRight(contact)
      mockContactsUiActions.showSelectContactDialog(any) returns serviceRight(Unit)

      contactsJobs.showContact(lookupKey).mustRightUnit

      there was one(mockTrackEventProcess).addContactByFab()
      there was one(mockDeviceProcess).getContact(===(lookupKey))(any)
      there was one(mockContactsUiActions).showSelectContactDialog(contact)
    }

    "returns a ContactException when the service returns an exception" in new ContactsJobsScope {

      mockTrackEventProcess.addContactByFab() returns serviceRight(Unit)
      mockDeviceProcess.getContact(any)(any) returns serviceLeft(ContactException(""))

      contactsJobs.showContact(lookupKey).mustLeft[ContactException]

      there was one(mockTrackEventProcess).addContactByFab()
      there was one(mockDeviceProcess).getContact(===(lookupKey))(any)
      there was no(mockContactsUiActions).showSelectContactDialog(any)
    }
  }

  "requestPermissionsResult" should {
    "returns a valid response when the service returns a right response" in new ContactsJobsScope {

      mockContactsUiActions.showLoading() returns serviceRight(Unit)
      mockDeviceProcess.getIterableContacts(any)(any) returns serviceRight(iterableContact)
      mockContactsUiActions.showContacts(any) returns serviceRight(Unit)

      contactsJobs
        .requestPermissionsResult(
          RequestCodes.contactsPermission,
          contactPermissions,
          contactGranResults)
        .mustRightUnit

      there was one(mockContactsUiActions).showLoading()
      there was one(mockDeviceProcess).getIterableContacts(===(AllContacts))(any)
      there was one(mockContactsUiActions).showContacts(any)
    }

    "shows a error contacts permission" in new ContactsJobsScope {

      mockContactsUiActions.showErrorContactsPermission() returns serviceRight(Unit)
      contactsJobs
        .requestPermissionsResult(
          RequestCodes.contactsPermission,
          contactNoPermissions,
          contactGranResults)
        .mustRightUnit
      there was one(mockContactsUiActions).showErrorContactsPermission()
    }

    "Do nothing if requestCode isn't contactsPermission" in new ContactsJobsScope {

      contactsJobs
        .requestPermissionsResult(
          RequestCodes.callLogPermission,
          contactNoPermissions,
          contactGranResults)
        .mustRightUnit

    }
  }

  "showError" should {
    "call to showError" in new ContactsJobsScope {

      mockContactsUiActions.showError() returns serviceRight(Unit)
      contactsJobs.showError().mustRightUnit
      there was one(mockContactsUiActions).showError()
    }
  }

  "showErrorLoadingContacts" should {
    "call to showErrorLoadingContactsInScreen" in new ContactsJobsScope {

      mockContactsUiActions.showErrorLoadingContactsInScreen() returns serviceRight(Unit)
      contactsJobs.showErrorLoadingContacts().mustRightUnit
      there was one(mockContactsUiActions).showErrorLoadingContactsInScreen()
    }
  }

  "close" should {
    "call to close" in new ContactsJobsScope {

      mockContactsUiActions.close() returns serviceRight(Unit)
      contactsJobs.close().mustRightUnit
      there was one(mockContactsUiActions).close()
    }
  }

}
