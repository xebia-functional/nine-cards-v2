package cards.nine.app.ui.commons

import android.content.Intent.ShortcutIconResource
import android.content.{Context, Intent}
import android.graphics.Bitmap
import android.os.Bundle
import cards.nine.app.commons.BroadcastDispatcher._
import cards.nine.app.di.Injector
import cards.nine.commons._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{CardTestData, CardValues}
import cards.nine.models.{Card, CardData}
import cards.nine.models.types.ShortcutCardType
import cards.nine.process.collection.CollectionProcess
import cards.nine.process.device.DeviceProcess
import cards.nine.process.theme.ThemeProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

class JobsSpecification
  extends TaskServiceSpecification
    with Mockito {

  trait JobsScope
    extends Scope {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockContext = mock[Context]

    contextWrapper.bestAvailable returns mockContext

    implicit val contextSupport = mock[ContextSupport]

    val jobs = new Jobs()(contextWrapper)
  }

  trait ShortcutJobsScope
    extends Scope
    with CardTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    implicit val contextSupport = mock[ContextSupport]

    val mockInjector: Injector = mock[Injector]

    val mockCollectionProcess: CollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockDeviceProcess: DeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockIntent: Intent = mock[Intent]

    val mockBundle: Bundle = mock[Bundle]

    val mockBitmap: Bitmap = mock[Bitmap]

    val mockIconResource: ShortcutIconResource = mock[ShortcutIconResource]

    val shortcutJobs = new ShortcutJobs()(contextWrapper) {

      override implicit lazy val di: Injector = mockInjector

    }
  }

}

class JobsSpec
  extends JobsSpecification {

  "sendBroadCastTask" should {

    "call to send broadcast with the right params" in new JobsScope {

      val action = "myAction"
      val command = "command"

      jobs.sendBroadCastTask(BroadAction(action, Some(command))).mustRightUnit

      val argMather = beLike[Intent] { case intent =>
        intent.getAction shouldEqual action
        intent.getStringExtra(keyType) shouldEqual commandType
        intent.getStringExtra(keyCommand) shouldEqual command
      }
      there was one(mockContext).sendBroadcast(argThat(argMather))

    }
  }

  "askBroadCastTask" should {

    "call to send broadcast with the right params" in new JobsScope {

      val action = "myAction"
      val command = "command"

      jobs.askBroadCastTask(BroadAction(action, Some(command))).mustRightUnit

      val argMather = beLike[Intent] { case intent =>
        intent.getAction shouldEqual action
        intent.getStringExtra(keyType) shouldEqual questionType
        intent.getStringExtra(keyCommand) shouldEqual command
      }
      there was one(mockContext).sendBroadcast(argThat(argMather))

    }
  }

}

class ShortcutJobsSpec
  extends JobsSpecification {

  "addNewShortcut" should {

    "return None when the intent is null" in new ShortcutJobsScope {
      shortcutJobs.addNewShortcut(1, javaNull).mustRight(_ must beNone)
    }

    "return None when the intent doesn't have the right extras" in new ShortcutJobsScope {
      mockIntent.getExtras returns mockBundle
      mockBundle.containsKey(any) returns false

      shortcutJobs.addNewShortcut(1, mockIntent).mustRight(_ must beNone)
    }

    "return the Card when the intent has the name and intent as extras" in new ShortcutJobsScope {
      mockIntent.getExtras returns mockBundle
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_NAME) returns true
      mockBundle.getString(Intent.EXTRA_SHORTCUT_NAME) returns card.term
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_INTENT) returns true
      mockBundle.getParcelable[Intent](Intent.EXTRA_SHORTCUT_INTENT) returns card.intent
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_ICON) returns false

      mockCollectionProcess.addCards(any, any) returns TaskService.right(Seq(card))

      shortcutJobs.addNewShortcut(1, mockIntent).mustRight(_ must beSome(card))

      val cardMatcher = beLike[Seq[CardData]] { case seq =>
        val maybeCard = seq.headOption
        maybeCard must beSome
        maybeCard.map(_.term) must beSome(card.term)
        maybeCard.flatMap(_.packageName) must beNone
        maybeCard.map(_.cardType) must beSome(ShortcutCardType)
        maybeCard.map(_.intent) must beSome
        maybeCard.flatMap(_.imagePath) must beNone
      }
      there was one(mockCollectionProcess).addCards(===(1), argThat(cardMatcher))
    }

    "return the Card when the intent has the name, the intent and the icon as extras" in new ShortcutJobsScope {
      mockIntent.getExtras returns mockBundle
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_NAME) returns true
      mockBundle.getString(Intent.EXTRA_SHORTCUT_NAME) returns card.term
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_INTENT) returns true
      mockBundle.getParcelable[Intent](Intent.EXTRA_SHORTCUT_INTENT) returns card.intent
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_ICON) returns true
      mockBundle.getParcelable[Bitmap](Intent.EXTRA_SHORTCUT_ICON) returns mockBitmap

      mockDeviceProcess.saveShortcutIcon(any, any)(any) returns TaskService.right(CardValues.cardImagePath)
      mockCollectionProcess.addCards(any, any) returns TaskService.right(Seq(card))

      shortcutJobs.addNewShortcut(1, mockIntent).mustRight(_ must beSome(card))

      val cardMatcher = beLike[Seq[CardData]] { case seq =>
        val maybeCard = seq.headOption
        maybeCard must beSome
        maybeCard.map(_.term) must beSome(card.term)
        maybeCard.flatMap(_.packageName) must beNone
        maybeCard.map(_.cardType) must beSome(ShortcutCardType)
        maybeCard.map(_.intent) must beSome
        maybeCard.flatMap(_.imagePath) shouldEqual card.imagePath
      }
      there was one(mockCollectionProcess).addCards(===(1), argThat(cardMatcher))
      there was one(mockDeviceProcess).saveShortcutIcon(===(mockBitmap), any)(any)
    }

    "return the Card when the intent has the name, the intent and the icon resource as extras" in new ShortcutJobsScope {
      mockIntent.getExtras returns mockBundle
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_NAME) returns true
      mockBundle.getString(Intent.EXTRA_SHORTCUT_NAME) returns card.term
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_INTENT) returns true
      mockBundle.getParcelable[Intent](Intent.EXTRA_SHORTCUT_INTENT) returns card.intent
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_ICON) returns false
      mockBundle.containsKey(Intent.EXTRA_SHORTCUT_ICON_RESOURCE) returns true
      mockBundle.getParcelable[ShortcutIconResource](Intent.EXTRA_SHORTCUT_ICON_RESOURCE) returns mockIconResource

      mockDeviceProcess.decodeShortcutIcon(any)(any) returns TaskService.right(mockBitmap)
      mockDeviceProcess.saveShortcutIcon(any, any)(any) returns TaskService.right(CardValues.cardImagePath)
      mockCollectionProcess.addCards(any, any) returns TaskService.right(Seq(card))

      shortcutJobs.addNewShortcut(1, mockIntent).mustRight(_ must beSome(card))

      val cardMatcher = beLike[Seq[CardData]] { case seq =>
        val maybeCard = seq.headOption
        maybeCard must beSome
        maybeCard.map(_.term) must beSome(card.term)
        maybeCard.flatMap(_.packageName) must beNone
        maybeCard.map(_.cardType) must beSome(ShortcutCardType)
        maybeCard.map(_.intent) must beSome
        maybeCard.flatMap(_.imagePath) shouldEqual card.imagePath
      }
      there was one(mockCollectionProcess).addCards(===(1), argThat(cardMatcher))
      there was one(mockDeviceProcess).saveShortcutIcon(===(mockBitmap), any)(any)
    }

  }

}