package cards.nine.app.ui.share

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.components.dialogs.CollectionDialog
import cards.nine.process.commons.models.Collection
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.{Contexts, Ui}

trait SharedContentUiActionsImpl
  extends SharedContentUiActions {

  self: TypedFindView with Contexts[AppCompatActivity] =>

  implicit val uiContext: UiContext[Activity]

  implicit val presenter: SharedContentPresenter

  implicit lazy val theme: NineCardsTheme = presenter.getTheme

  override def showChooseCollection(collections: Seq[Collection]): Ui[Any] = activityContextWrapper.original.get match {
    case Some(activity: Activity) =>
      Ui(new CollectionDialog(collections, presenter.collectionChosen, presenter.dialogDismissed).show())
    case _ => Ui.nop
  }

  override def showSuccess(): Ui[Any] = uiShortToast2(R.string.sharedCardAdded) ~ finishUi()

  override def showErrorEmptyContent(): Ui[Any] = uiShortToast2(R.string.sharedContentErrorEmpty) ~ finishUi()

  override def showErrorContentNotSupported(): Ui[Any] = uiLongToast2(R.string.sharedContentErrorNotSupported) ~ finishUi()

  override def showUnexpectedError(): Ui[Any] = uiShortToast2(R.string.sharedContentErrorUnexpected) ~ finishUi()

  override def finishUi(): Ui[Any] = Ui(uiContext.value.finish())
}
