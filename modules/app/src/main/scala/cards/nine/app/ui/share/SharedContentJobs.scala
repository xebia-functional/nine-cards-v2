package cards.nine.app.ui.share

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.URLUtil
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.share.SharedContentActivity._
import cards.nine.app.ui.share.models.{SharedContent, Web}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.types.ShortcutCardType
import cards.nine.models.{CardData, IconResize, NineCardsIntent, NineCardsIntentExtras}
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

class SharedContentJobs(
  val sharedContentUiActions: SharedContentUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(): TaskService[Unit] =
    for {
      theme <- getThemeTask
      _ <- TaskService.right(statuses = statuses.copy(theme = theme))
    } yield ()

  def receivedIntent(intent: Intent): TaskService[Unit] = {
    def readImageUri(intent: Intent): Option[Uri] = {
      Option(intent.getClipData) flatMap { clipData =>
        if (clipData.getItemCount > 0) Option(clipData.getItemAt(0)) flatMap (d => Option(d.getUri)) else None
      }
    }

    def isLink(link: String): Boolean = URLUtil.isValidUrl(link)

    def showErrorContentNotSupported() =
      for {
        _ <- di.trackEventProcess.sharedContentReceived(true)
        _ <- sharedContentUiActions.showErrorContentNotSupported()
      } yield ()

    val contentTypeText = "text/plain"

    Option(intent) map { i =>

      val action = Option(i.getAction)
      val intentType = Option(i.getType)
      val extra = readStringValue(i, Intent.EXTRA_TEXT)
      val subject = readStringValue(i, Intent.EXTRA_SUBJECT)

      (action, intentType, extra) match {
        case (Some(Intent.ACTION_SEND), Some(`contentTypeText`), Some(content)) if isLink(content) =>
          val sharedContent = SharedContent(
            contentType = Web,
            title = subject getOrElse resGetString(R.string.sharedContentDefaultTitle),
            content = content,
            image = readImageUri(i))
          statuses = statuses.copy(sharedContent = Some(sharedContent))
          for {
            _ <- di.trackEventProcess.sharedContentReceived(true)
            collections <- di.collectionProcess.getCollections
            _ <- sharedContentUiActions.showChooseCollection(collections)
          } yield ()
        case (Some(Intent.ACTION_SEND), Some(`contentTypeText`), Some(content)) => showErrorContentNotSupported()
        case (Some(Intent.ACTION_SEND), Some(`contentTypeText`), None) =>
          sharedContentUiActions.showErrorEmptyContent()
        case (Some(Intent.ACTION_SEND), _, _) => showErrorContentNotSupported()
        case _ => sharedContentUiActions.showUnexpectedError()
      }
    } getOrElse TaskService.empty

  }

  def collectionChosen(collectionId: Int): TaskService[Unit] = {

    def createRequest(sharedContent: SharedContent, imagePath: String): CardData = {

      val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharedContent.content))

      val nineCardIntent = NineCardsIntent(NineCardsIntentExtras())
      nineCardIntent.fill(intent)

      CardData(
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
            MediaStore.Images.Media.getBitmap(activityContextWrapper.bestAvailable.getContentResolver, uri),
            Some(IconResize(iconSize, iconSize)))
        case _ => TaskService.right("")
      }
    }

    def addCard(sharedContent: SharedContent): TaskService[Unit] = for {
      imagePath <- saveBitmap(sharedContent.image)
      _ <- di.collectionProcess.addCards(collectionId, Seq(createRequest(sharedContent, imagePath)))
    } yield ()

    statuses.sharedContent match {
      case Some(sharedContent) =>
        for {
          _ <- addCard(sharedContent)
          _ <- sharedContentUiActions.showSuccess()
        } yield ()
      case _ => sharedContentUiActions.showUnexpectedError()
    }
  }

  def dialogDismissed(): TaskService[Unit] = sharedContentUiActions.close()

}
