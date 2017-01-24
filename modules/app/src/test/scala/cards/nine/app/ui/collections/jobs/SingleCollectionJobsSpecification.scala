/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.collections.jobs

import cards.nine.app.di.Injector
import cards.nine.app.ui.collections.jobs.uiactions.SingleCollectionUiActions
import cards.nine.app.ui.commons.JobException
import cards.nine.app.ui.launcher.jobs.LauncherTestData
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.CollectionTestData
import cards.nine.process.collection.{CollectionException, CollectionProcess}
import cards.nine.process.theme.ThemeProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.CardValues._

trait SingleCollectionJobsSpecification extends TaskServiceSpecification with Mockito {

  trait SingleCollectionJobsScope extends Scope with CollectionTestData with LauncherTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockSingleCollectionUiActions = mock[SingleCollectionUiActions]

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockThemeProcess = mock[ThemeProcess]

    mockInjector.themeProcess returns mockThemeProcess

    val mockAnimateCards = true

    val mockCollection = collection

    val singleCollectionJobs = new SingleCollectionJobs(
      mockAnimateCards,
      Option(mockCollection),
      mockSingleCollectionUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def themeFile = ""

    }

  }

}

class SingleCollectionJobsSpec extends SingleCollectionJobsSpecification {

  "initialize" should {
    "Shows empty collection if it doesn't have a collection" in new SingleCollectionJobsScope {

      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockSingleCollectionUiActions.initialize(any, any) returns serviceRight(Unit)

      singleCollectionJobs.initialize().mustRightUnit

      there was one(mockSingleCollectionUiActions).initialize(true, mockCollection)

    }

    "Initializes all actions and services" in new SingleCollectionJobsScope {

      override val singleCollectionJobs =
        new SingleCollectionJobs(mockAnimateCards, None, mockSingleCollectionUiActions)(
          contextWrapper) {

          override lazy val di: Injector = mockInjector

          override def themeFile = ""

        }

      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)
      mockSingleCollectionUiActions.showEmptyCollection() returns serviceRight(Unit)

      singleCollectionJobs.initialize().mustRightUnit

    }
  }

  "reorderCard" should {
    "return a valid response when the service returns a right response" in new SingleCollectionJobsScope {

      mockTrackEventProcess.reorderApplication(any) returns serviceRight(Unit)
      mockCollectionProcess.reorderCard(any, any, any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.reloadCards() returns serviceRight(Unit)

      singleCollectionJobs.reorderCard(collection.id, card.id, position).mustRightUnit

      there was one(mockTrackEventProcess).reorderApplication(position)
      there was one(mockCollectionProcess).reorderCard(collection.id, card.id, position)
      there was one(mockSingleCollectionUiActions).reloadCards()
    }
  }

  "moveToCollection" should {
    "return a valid response when the service returns a right response" in new SingleCollectionJobsScope {

      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockSingleCollectionUiActions.moveToCollection(any) returns serviceRight(Unit)

      singleCollectionJobs.moveToCollection().mustRightUnit

      there was one(mockCollectionProcess).getCollections
      there was one(mockSingleCollectionUiActions).moveToCollection(seqCollection)
    }

    "return a CollectionException if the service throws an exception" in new SingleCollectionJobsScope {

      mockCollectionProcess.getCollections returns serviceLeft(CollectionException(""))

      singleCollectionJobs.moveToCollection().mustLeft[CollectionException]

      there was one(mockCollectionProcess).getCollections
      there was no(mockSingleCollectionUiActions).moveToCollection(any)
    }
  }

  "addCards" should {
    "returns a valid response when the service returns a right response" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockTrackEventProcess.addAppToCollection(any, any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.addCards(any) returns serviceRight(Unit)

      singleCollectionJobs.addCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was exactly(seqCard.size)(mockTrackEventProcess).addAppToCollection(any, any)
      there was one(mockSingleCollectionUiActions).addCards(seqCard)
    }

    "returns a valid response but not track nothing when don't have current collection" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(None)
      mockSingleCollectionUiActions.addCards(any) returns serviceRight(Unit)

      singleCollectionJobs.addCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was one(mockSingleCollectionUiActions).addCards(seqCard)
    }

    "returns a valid response and track although don't have apps Category, create a moment Category" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(
        Option(collection.copy(appsCategory = None)))
      mockTrackEventProcess.addAppToCollection(any, any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.addCards(any) returns serviceRight(Unit)

      singleCollectionJobs.addCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was one(mockSingleCollectionUiActions).addCards(seqCard)
    }

    "returns a valid response but not track nothing when don't have appsCategory and moment" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(
        Option(collection.copy(appsCategory = None, moment = None)))
      mockSingleCollectionUiActions.addCards(any) returns serviceRight(Unit)

      singleCollectionJobs.addCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was one(mockSingleCollectionUiActions).addCards(seqCard)
    }

    "Does nothing if cards is Seq.empty" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.addCards(any) returns serviceRight(Unit)

      singleCollectionJobs.addCards(Seq.empty).mustRightUnit

      there was no(mockSingleCollectionUiActions).getCurrentCollection
      there was one(mockSingleCollectionUiActions).addCards(Seq.empty)
    }

  }
  "removeCards" should {
    "returns a valid response when the service returns a right response" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(Option(collection))
      mockTrackEventProcess.removeFromCollection(any, any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.removeCards(any) returns serviceRight(Unit)

      singleCollectionJobs.removeCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was exactly(seqCard.size)(mockTrackEventProcess).removeFromCollection(any, any)
      there was one(mockSingleCollectionUiActions).removeCards(seqCard)
    }

    "returns a valid response but not track nothing when don't have current collection" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(None)
      mockSingleCollectionUiActions.removeCards(any) returns serviceRight(Unit)

      singleCollectionJobs.removeCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was one(mockSingleCollectionUiActions).removeCards(seqCard)
    }

    "returns a valid response and track although don't have apps Category, create a moment Category" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(
        Option(collection.copy(appsCategory = None)))
      mockTrackEventProcess.removeFromCollection(any, any) returns serviceRight(Unit)
      mockSingleCollectionUiActions.removeCards(any) returns serviceRight(Unit)

      singleCollectionJobs.removeCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was one(mockSingleCollectionUiActions).removeCards(seqCard)
    }

    "returns a valid response but not track nothing when don't have appsCategory and moment" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.getCurrentCollection returns serviceRight(
        Option(collection.copy(appsCategory = None, moment = None)))
      mockSingleCollectionUiActions.removeCards(any) returns serviceRight(Unit)

      singleCollectionJobs.removeCards(seqCard).mustRightUnit

      there was exactly(seqCard.size)(mockSingleCollectionUiActions).getCurrentCollection
      there was one(mockSingleCollectionUiActions).removeCards(seqCard)
    }

    "Does nothing if cards is Seq.empty" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.removeCards(any) returns serviceRight(Unit)

      singleCollectionJobs.removeCards(Seq.empty).mustRightUnit

      there was no(mockSingleCollectionUiActions).getCurrentCollection
      there was one(mockSingleCollectionUiActions).removeCards(Seq.empty)
    }

  }

  "reloadCards" should {
    "calls to action reloadCards" in new SingleCollectionJobsScope {
      mockSingleCollectionUiActions.reloadCards(any) returns serviceRight(Unit)
      singleCollectionJobs.reloadCards(seqCard).mustRightUnit
      there was one(mockSingleCollectionUiActions).reloadCards(seqCard)
    }
  }

  "bindAnimatedAdapter" should {
    "calls to bindAnimatedAdapter" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.bindAnimatedAdapter(any, any) returns serviceRight(Unit)
      singleCollectionJobs.bindAnimatedAdapter().mustRightUnit
      there was one(mockSingleCollectionUiActions).bindAnimatedAdapter(true, collection)
    }

    "return a JobException if the service throws an exception" in new SingleCollectionJobsScope {

      override val singleCollectionJobs =
        new SingleCollectionJobs(mockAnimateCards, None, mockSingleCollectionUiActions)(
          contextWrapper) {

          override lazy val di: Injector = mockInjector

        }
      singleCollectionJobs.bindAnimatedAdapter().mustLeft[JobException]
      there was no(mockSingleCollectionUiActions).bindAnimatedAdapter(any, any)
    }
  }

  "saveEditedCard" should {
    "Save the edited card" in new SingleCollectionJobsScope {

      mockCollectionProcess.editCard(any, any, any) returns serviceRight(card)
      mockSingleCollectionUiActions.reloadCard(any) returns serviceRight(Unit)

      singleCollectionJobs
        .saveEditedCard(collection.id, card.id, Option(newCardName))
        .mustRightUnit

      there was one(mockCollectionProcess).editCard(collection.id, card.id, newCardName)
      there was one(mockSingleCollectionUiActions).reloadCard(card)
    }

    "Shows a message form field error if cardName is an empty string" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.showMessageFormFieldError returns serviceRight(Unit)

      singleCollectionJobs.saveEditedCard(collection.id, card.id, Option("")).mustRightUnit

      there was one(mockSingleCollectionUiActions).showMessageFormFieldError
    }

    "Shows a message form field error if don't have cardName" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.showMessageFormFieldError returns serviceRight(Unit)

      singleCollectionJobs.saveEditedCard(collection.id, card.id, None).mustRightUnit

      there was one(mockSingleCollectionUiActions).showMessageFormFieldError
    }
  }

  "showData" should {
    "calls to show data" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.showData(any) returns serviceRight(Unit)
      singleCollectionJobs.showData().mustRightUnit
      there was one(mockSingleCollectionUiActions).showData(collection.cards.isEmpty)
    }

    "return a JobException if the service throws an exception" in new SingleCollectionJobsScope {

      override val singleCollectionJobs =
        new SingleCollectionJobs(mockAnimateCards, None, mockSingleCollectionUiActions)(
          contextWrapper) {

          override lazy val di: Injector = mockInjector

        }
      singleCollectionJobs.showData().mustLeft[JobException]
      there was no(mockSingleCollectionUiActions).showData(any)
    }
  }

  "showGenericError" should {
    "calls to action showGenericError" in new SingleCollectionJobsScope {

      mockSingleCollectionUiActions.showContactUsError() returns serviceRight(Unit)
      singleCollectionJobs.showGenericError().mustRightUnit
      there was one(mockSingleCollectionUiActions).showContactUsError()
    }
  }

}
