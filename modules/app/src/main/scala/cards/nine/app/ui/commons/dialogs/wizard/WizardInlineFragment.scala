package cards.nine.app.ui.commons.dialogs.wizard

import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
import android.support.design.widget.{
  BottomSheetBehavior,
  BottomSheetDialogFragment,
  CoordinatorLayout
}
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import cards.nine.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

class WizardInlineFragment
    extends BottomSheetDialogFragment
    with ContextSupportProvider
    with UiExtensions
    with WizardListener
    with Contexts[Fragment] {

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  lazy val wizardInlineType = WizardInlineType(
    getString(Seq(getArguments), WizardInlineFragment.wizardInlineTypeKey, ""))

  lazy val wizardInlinePreferences = new WizardInlinePreferences()

  override def getTheme: Int = R.style.AppThemeDialog

  override def onDestroy(): Unit = {
    wizardInlinePreferences.wasShowed(wizardInlineType)
    super.onDestroy()
  }

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)

    val baseView = LayoutInflater
      .from(getActivity)
      .inflate(R.layout.wizard_inline, javaNull, false)
      .asInstanceOf[ViewGroup]

    val uiActions =
      new WizardInlineUiActions(new WizardInlineDOM(baseView), this)

    uiActions.initialize(wizardInlineType).resolveAsync()

    dialog.setContentView(baseView)

    getBehaviour(baseView) foreach { behaviour =>
      behaviour.setBottomSheetCallback(new BottomSheetCallback {
        override def onSlide(view: View, slideOffset: Float): Unit = {}
        override def onStateChanged(view: View, newState: Int): Unit =
          if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismissWizard()
          }
      })
      behaviour.setState(BottomSheetBehavior.STATE_EXPANDED)
    }

  }

  override def dismissWizard(): Unit = {
    wizardInlinePreferences.wasShowed(wizardInlineType)
    dismiss()
  }

  private[this] def getBehaviour(viewGroup: ViewGroup): Option[BottomSheetBehavior[_]] =
    viewGroup.getParent match {
      case view: View =>
        view.getLayoutParams match {
          case params: CoordinatorLayout.LayoutParams =>
            params.getBehavior match {
              case behavior: BottomSheetBehavior[_] => Option(behavior)
              case _                                => None
            }
          case _ => None
        }
      case _ => None
    }

}

object WizardInlineFragment {

  val wizardInlineTypeKey = "wizard-inline-type-key"

}
