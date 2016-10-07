package cards.nine.app.ui.share

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.URLUtil
import cards.nine.models.{NineCardsIntentExtras$, NineCardsIntent}
import cards.nine.models.types.ShortcutCardType
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.app.ui.commons.AppLog._
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.share.models.{SharedContent, Web}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.collection.AddCardRequest
import cards.nine.process.commons.models.Collection
import cards.nine.process.device.IconResize
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, Ui}
import monix.eval.Task
import cats.syntax.either._


class SharedContentPresenter(uiActions: SharedContentUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  import Statuses._

  var statuses = SharedContentPresenterStatuses()

  def receivedIntent(intent: Intent): Unit = {

    def readImageUri(intent: Intent): Option[Uri] = {
      Option(intent.getClipData) flatMap { clipData =>
        if (clipData.getItemCount > 0) Option(clipData.getItemAt(0)) flatMap (d => Option(d.getUri)) else None
      }
    }

    def isLink(link: String): Boolean = URLUtil.isValidUrl(link)

    val contentTypeText = "text/plain"

    Option(intent) foreach { i =>

      val action = Option(i.getAction)
      val intentType = Option(i.getType)
      val extra = Option(i.getStringExtra(Intent.EXTRA_TEXT))
      val subject = Option(i.getStringExtra(Intent.EXTRA_SUBJECT))

      (action, intentType, extra) match {
        case (Some(Intent.ACTION_SEND), Some(`contentTypeText`), Some(content)) if isLink(content) =>
          val sharedContent = SharedContent(
            contentType = Web,
            title = subject getOrElse resGetString(R.string.sharedContentDefaultTitle),
            content = content,
            image = readImageUri(i))
          statuses = statuses.copy(sharedContent = Some(sharedContent))
          di.collectionProcess.getCollections.resolveAsyncUi2(
            onResult = uiActions.showChooseCollection,
            onException = error)
        case (Some(Intent.ACTION_SEND), Some(`contentTypeText`), Some(content)) =>
          uiActions.showErrorContentNotSupported().run
        case (Some(Intent.ACTION_SEND), Some(`contentTypeText`), None) =>
          uiActions.showErrorEmptyContent().run
        case (Some(Intent.ACTION_SEND), _, _) =>
          uiActions.showErrorContentNotSupported().run
        case _ =>
      }
    }
  }

  def collectionChosen(collectionId: Int): Unit = {

    def createRequest(sharedContent: SharedContent, imagePath: String): AddCardRequest = {

      val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharedContent.content))

      val nineCardIntent = NineCardsIntent(NineCardsIntentExtras())
      nineCardIntent.fill(intent)

      AddCardRequest(
        term = sharedContent.title,
        packageName = None,
        cardType = ShortcutCardType,
        intent = nineCardIntent,
        imagePath = Option(imagePath))
    }

    def saveBitmap(maybeUri: Option[Uri]): TaskService[String] = {
      maybeUri match {
        case Some(uri) =>
          val iconSize = resGetDimensionPixelSize(R.dimen.size_icon_app_medium)
          di.deviceProcess.saveShortcutIcon(
            MediaStore.Images.Media.getBitmap(contextWrapper.bestAvailable.getContentResolver, uri),
            Some(IconResize(iconSize, iconSize)))
        case _ => TaskService(Task(Either.right("")))
      }
    }

    def addCard(sharedContent: SharedContent): TaskService[Unit] = for {
      imagePath <- saveBitmap(sharedContent.image)
      _ <- di.collectionProcess.addCards(collectionId, Seq(createRequest(sharedContent, imagePath)))
    } yield ()

    statuses.sharedContent match {
      case Some(sharedContent) =>
        addCard(sharedContent).resolveAsyncUi2(
          onResult = (_) => uiActions.showSuccess(),
          onException = error)
      case _ => uiActions.showUnexpectedError().run
    }
  }

  def dialogDismissed() = uiActions.finishUi().run

  private[this] def error(throwable: Throwable): Ui[Any] = {
    printErrorMessage(throwable)
    uiActions.showUnexpectedError()
  }

}

object Statuses {

  case class SharedContentPresenterStatuses(
    sharedContent: Option[SharedContent] = None)

}

trait SharedContentUiActions {

  def showChooseCollection(collections: Seq[Collection]): Ui[Any]

  def showSuccess(): Ui[Any]

  def showErrorEmptyContent(): Ui[Any]

  def showErrorContentNotSupported(): Ui[Any]

  def showUnexpectedError(): Ui[Any]

  def finishUi(): Ui[Any]

}
