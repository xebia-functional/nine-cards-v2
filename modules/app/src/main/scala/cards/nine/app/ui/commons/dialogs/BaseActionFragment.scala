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

package cards.nine.app.ui.commons.dialogs

import android.app.Dialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View}
import android.widget.FrameLayout
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.di.{Injector, InjectorImpl}
import cards.nine.app.ui.commons.AppUtils._
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import cards.nine.app.ui.components.widgets.tweaks.TintableImageViewTweaks._
import cards.nine.app.ui.preferences.commons.Theme
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models._
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerTextColor, PrimaryColor}
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ProgressBarTweaks._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

import scala.language.postfixOps

trait BaseActionFragment
    extends BottomSheetDialogFragment
    with TypedFindView
    with ContextSupportProvider
    with UiExtensions
    with Contexts[Fragment] {

  implicit lazy val di: Injector = new InjectorImpl

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  implicit lazy val theme: NineCardsTheme =
    di.themeProcess.getTheme(Theme.getThemeFile).resolveNow match {
      case Right(t) => t
      case _        => getDefaultTheme
    }

  private[this] lazy val defaultColor = theme.get(PrimaryColor)

  override protected def findViewById(id: Int): View =
    rootView map (_.findViewById(id)) orNull

  protected lazy val colorPrimary =
    getInt(Seq(getArguments), BaseActionFragment.colorPrimary, defaultColor)

  protected lazy val backgroundColor = theme.get(DrawerBackgroundColor)

  protected lazy val toolbar = Option(findView(TR.actions_toolbar))

  protected lazy val loading = Option(findView(TR.action_loading))

  protected lazy val loadingText = Option(findView(TR.action_loading_text))

  protected lazy val loadingBar = Option(findView(TR.action_loading_bar))

  protected lazy val content = Option(findView(TR.action_content_layout))

  protected lazy val rootContent = Option(findView(TR.action_content_root))

  protected lazy val fab = Option(findView(TR.action_content_fab))

  protected lazy val errorContent = Option(findView(TR.actions_content_error_layout))

  protected lazy val errorMessage = Option(findView(TR.actions_content_error_message))

  protected lazy val errorIcon = Option(findView(TR.actions_content_error_icon))

  protected lazy val errorButton = Option(findView(TR.actions_content_error_button))

  protected var rootView: Option[FrameLayout] = None

  def getLayoutId: Int

  def useFab: Boolean = false

  override def getTheme: Int = R.style.AppThemeDialog

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)

    def fabAnimation =
      vVisible + vScaleX(0) + vScaleY(0) ++ applyAnimation(scaleX = Option(1), scaleY = Option(1))

    val baseView = LayoutInflater
      .from(getActivity)
      .inflate(R.layout.base_action_fragment, javaNull, false)
      .asInstanceOf[FrameLayout]
    val layout =
      LayoutInflater.from(getActivity).inflate(getLayoutId, javaNull)
    rootView = Option(baseView)
    ((content <~ vgAddView(layout)) ~
      (loadingBar <~ pbColor(colorPrimary)) ~
      (errorIcon <~ tivColor(colorPrimary)) ~
      (errorContent <~ vGone) ~
      (errorMessage <~ tvColor(theme.get(DrawerTextColor).alpha(0.8f))) ~
      (errorButton <~ vBackgroundTint(colorPrimary)) ~
      (rootContent <~ vBackgroundColor(backgroundColor)) ~
      (if (useFab) fab <~ fabAnimation else Ui.nop)).run

    dialog.setContentView(baseView)
  }

  def unreveal(): Ui[Any] = Ui(dismissAllowingStateLoss())

  def showMessageInScreen(message: Int, error: Boolean, action: => Unit): Ui[_] =
    (loading <~ vGone) ~
      (errorIcon <~ ivSrc(if (error) R.drawable.placeholder_error
      else R.drawable.placeholder_empty)) ~
      (errorMessage <~ text(message)) ~
      (errorButton <~ On.click {
        action
        hideError
      }) ~
      (errorContent <~ vVisible)

  def hideError: Ui[_] = errorContent <~ vGone

}

object BaseActionFragment {
  val packages     = "packages"
  val colorPrimary = "color_primary"
}
