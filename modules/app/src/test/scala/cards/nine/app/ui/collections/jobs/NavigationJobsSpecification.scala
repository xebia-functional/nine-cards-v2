package cards.nine.app.ui.collections.jobs

import android.os.Bundle
import cards.nine.app.ui.collections.jobs.uiactions.{
  GroupCollectionsDOM,
  GroupCollectionsUiActions,
  NavigationUiActions
}
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionTestData
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait NavigationJobsSpecification
    extends TaskServiceSpecification
    with Mockito
    with CollectionTestData {

  trait NavigationJobsScope extends Scope {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val mockGroupCollectionsJobs = mock[GroupCollectionsJobs]

    implicit val mockSingleCollectionJobs = mock[SingleCollectionJobs]

    val mockGroupCollectionsDOM = mock[GroupCollectionsDOM]

    val mockGroupCollectionsUiActions = mock[GroupCollectionsUiActions]

    val mockNavigationUiActions = mock[NavigationUiActions]

    mockNavigationUiActions.dom returns mockGroupCollectionsDOM

    val mockBundle = mock[Bundle]

    val navigationJobs =
      new NavigationJobs(mockGroupCollectionsUiActions, mockNavigationUiActions)(contextWrapper)

  }
}

class NavigationJobsSpec extends NavigationJobsSpecification {

  "showAppDialog" should {
    "call to openApps" in new NavigationJobsScope {

      mockGroupCollectionsDOM.getCurrentCollection returns Option(collection)
      mockNavigationUiActions.openApps(any)(any, any) returns serviceRight((): Unit)

      navigationJobs
        .showAppDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit

      there was two(mockGroupCollectionsDOM).getCurrentCollection
      there was one(mockNavigationUiActions).openApps(any)(
        ===(mockGroupCollectionsJobs),
        ===(Option(mockSingleCollectionJobs)))
    }

    "call to openApps when current collection is None" in new NavigationJobsScope {

      mockGroupCollectionsDOM.getCurrentCollection returns None
      mockNavigationUiActions.openApps(any)(any, any) returns serviceRight((): Unit)

      navigationJobs
        .showAppDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit

      there was two(mockGroupCollectionsDOM).getCurrentCollection
      there was one(mockNavigationUiActions).openApps(any)(
        ===(mockGroupCollectionsJobs),
        ===(Option(mockSingleCollectionJobs)))
    }
  }

  "showRecommendationDialog" should {

    "call to openRecommendations" in new NavigationJobsScope {

      mockGroupCollectionsDOM.getCurrentCollection returns Option(collection)
      mockNavigationUiActions.openRecommendations(any)(any, any) returns serviceRight((): Unit)

      navigationJobs
        .showRecommendationDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit

      there was two(mockGroupCollectionsDOM).getCurrentCollection
      there was one(mockNavigationUiActions).openRecommendations(any)(
        ===(mockGroupCollectionsJobs),
        ===(Option(mockSingleCollectionJobs)))
    }

    "shows a message of ContactUsError when current collection doesn't have appCategory and their cards doesn't have packageName" in new NavigationJobsScope {

      mockGroupCollectionsDOM.getCurrentCollection returns Option(
        collection
          .copy(appsCategory = None, cards = collection.cards.map(_.copy(packageName = None))))
      mockGroupCollectionsUiActions.showContactUsError() returns serviceRight((): Unit)

      navigationJobs
        .showRecommendationDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit

      there was one(mockGroupCollectionsDOM).getCurrentCollection
      there was one(mockGroupCollectionsUiActions).showContactUsError()
    }

    "shows a message of ContactUsError when current collection is None" in new NavigationJobsScope {

      mockGroupCollectionsDOM.getCurrentCollection returns None
      mockGroupCollectionsUiActions.showContactUsError() returns serviceRight((): Unit)

      navigationJobs
        .showRecommendationDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit

      there was one(mockGroupCollectionsDOM).getCurrentCollection
      there was one(mockGroupCollectionsUiActions).showContactUsError()
    }
  }

  "showContactsDialog" should {
    "call to openContacts" in new NavigationJobsScope {

      mockNavigationUiActions.openContacts(any)(any, any) returns serviceRight((): Unit)
      mockGroupCollectionsDOM.getCurrentCollection returns Option(collection)

      navigationJobs
        .showContactsDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit
      there was one(mockNavigationUiActions).openContacts(any)(
        ===(mockGroupCollectionsJobs),
        ===(Option(mockSingleCollectionJobs)))
    }

    "call to openContacts when current collection is None" in new NavigationJobsScope {

      mockNavigationUiActions.openContacts(any)(any, any) returns serviceRight((): Unit)
      mockGroupCollectionsDOM.getCurrentCollection returns None

      navigationJobs
        .showContactsDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit
      there was one(mockNavigationUiActions).openContacts(any)(
        ===(mockGroupCollectionsJobs),
        ===(Option(mockSingleCollectionJobs)))
    }
  }

  "showShortcutDialog" should {
    "call to openShortcuts" in new NavigationJobsScope {

      mockNavigationUiActions.openShortcuts(any)(any, any) returns serviceRight((): Unit)
      mockGroupCollectionsDOM.getCurrentCollection returns Option(collection)
      mockSingleCollectionJobs.saveCollectionIdForShortcut() returns serviceRight((): Unit)

      navigationJobs
        .showShortcutDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit
      there was one(mockNavigationUiActions).openShortcuts(any)(
        ===(mockGroupCollectionsJobs),
        ===(Option(mockSingleCollectionJobs)))
    }

    "call to openShortcuts when current collection is None" in new NavigationJobsScope {

      mockNavigationUiActions.openShortcuts(any)(any, any) returns serviceRight((): Unit)
      mockGroupCollectionsDOM.getCurrentCollection returns None
      mockSingleCollectionJobs.saveCollectionIdForShortcut() returns serviceRight((): Unit)

      navigationJobs
        .showShortcutDialog()(mockGroupCollectionsJobs, Option(mockSingleCollectionJobs))
        .mustRightUnit
      there was one(mockNavigationUiActions).openShortcuts(any)(
        ===(mockGroupCollectionsJobs),
        ===(Option(mockSingleCollectionJobs)))
    }
  }
}
