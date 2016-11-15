package cards.nine.app.ui.dialogs.wizard

import android.app.Dialog
import android.support.design.widget.{BottomSheetBehavior, BottomSheetDialogFragment, CoordinatorLayout}
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
  with Contexts[Fragment] {

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  override def getTheme: Int = R.style.AppThemeDialog

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)

    val baseView = LayoutInflater.from(getActivity).inflate(R.layout.wizard_inline, javaNull, false).asInstanceOf[ViewGroup]

    val uiActions = new WizardInlineUiActions(new WizardInlineDOM(baseView))

    uiActions.initialize().resolveAsync()

    dialog.setContentView(baseView)

    getBehaviour(baseView) foreach (_.setState(BottomSheetBehavior.STATE_EXPANDED))

  }

  private[this] def getBehaviour(view: ViewGroup): Option[BottomSheetBehavior[_]] = view.getParent match {
    case view: View => view.getLayoutParams match {
      case params: CoordinatorLayout.LayoutParams => params.getBehavior match {
        case behavior: BottomSheetBehavior[_] => Option(behavior)
        case _ => None
      }
      case _ => None
    }
    case _ => None
  }

}
