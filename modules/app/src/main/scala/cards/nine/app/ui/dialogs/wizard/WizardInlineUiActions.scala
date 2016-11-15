package cards.nine.app.ui.dialogs.wizard

import android.support.v4.app.{Fragment, FragmentManager}
import android.view.ViewGroup
import android.widget.ImageView
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.CommonsExcerpt._
import cards.nine.app.ui.components.layouts.WizardInlineData
import cards.nine.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.WizardInlineWorkspacesTweaks._
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.LinearLayoutTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

class WizardInlineUiActions(dom: WizardInlineDOM, listener: WizardListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  def initialize(): TaskService[Unit] = {

    val steps = getSteps

    def pagination(position: Int) =
      (w[ImageView] <~ paginationItemStyle <~ ivSrc(R.drawable.wizard_inline_pager) <~ vTag(position.toString)).get

    def createPagers() = {
      val pagerViews = steps.indices map { position =>
        val view = pagination(position)
        view.setActivated(position == 0)
        view
      }
      dom.wizardInlinePagination <~ vgAddViews(pagerViews)
    }

    def reloadPagers(currentPage: Int) = Transformer {
      case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) => i <~ vActivated(true)
      case i: ImageView => i <~ vActivated(false)
    }

    ((dom.wizardInlineWorkspace <~
      vGlobalLayoutListener(_ => {
        dom.wizardInlineWorkspace <~
          wiwData(steps) <~
          awsAddPageChangedObserver(currentPage => {
            val showAction = currentPage == steps.length - 1
            ((dom.wizardInlinePagination <~ reloadPagers(currentPage)) ~
              ((showAction, (dom.wizardInlineGotIt ~> isVisible).get, (dom.wizardInlinePagination ~> isVisible).get) match {
                case (true, false, _) =>
                  (dom.wizardInlineGotIt <~ applyFadeIn()) ~
                    (dom.wizardInlineSkip <~ applyFadeOut()) ~
                    (dom.wizardInlinePagination <~ applyFadeOut())
                case (false, _, false) =>
                  (dom.wizardInlineGotIt <~ applyFadeOut()) ~
                    (dom.wizardInlineSkip <~ applyFadeIn()) ~
                    (dom.wizardInlinePagination <~ applyFadeIn())
                case _ => Ui.nop
              })).run
          })
      })) ~
      (dom.wizardInlineSkip <~
        On.click(Ui(listener.dismiss()))) ~
      (dom.wizardInlineGotIt <~
        vGone <~
        On.click(Ui(listener.dismiss()))) ~
      createPagers()).toService
  }

  private[this] def getSteps = Seq(
    WizardInlineData(
      R.drawable.wizard_01,
      resGetString(R.string.wizard_inline_launcher_title_1),
      resGetString(R.string.wizard_inline_launcher_1)),
    WizardInlineData(
      R.drawable.wizard_02,
      resGetString(R.string.wizard_inline_launcher_title_2),
      resGetString(R.string.wizard_inline_launcher_2)),
    WizardInlineData(
      R.drawable.wizard_03,
      resGetString(R.string.wizard_inline_launcher_title_3),
      resGetString(R.string.wizard_inline_launcher_3)),
    WizardInlineData(
      R.drawable.wizard_04,
      resGetString(R.string.wizard_inline_launcher_title_4),
      resGetString(R.string.wizard_inline_launcher_4)))

  // Styles

  private[this] def paginationItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val size = resGetDimensionPixelSize(R.dimen.wizard_size_pager)
    val margin = resGetDimensionPixelSize(R.dimen.wizard_margin_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin)
  }

}
