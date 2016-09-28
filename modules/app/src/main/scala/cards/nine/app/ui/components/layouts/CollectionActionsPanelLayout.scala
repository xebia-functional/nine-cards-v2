package cards.nine.app.ui.components.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent._
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.TextTweaks._
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.widgets.TintableButton
import cards.nine.app.ui.components.widgets.tweaks.TintableButtonTweaks._
import cards.nine.app.ui.launcher.LauncherPresenter
import cards.nine.commons.javaNull
import cards.nine.process.theme.models.{NineCardsTheme, PrimaryColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

class CollectionActionsPanelLayout(context: Context, attrs: AttributeSet, defStyle: Int)
  extends LinearLayout(context, attrs, defStyle)
  with TypedFindView {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  val unselectedPosition = -1

  val selectedScale = 1.1f

  val defaultScale = 1f

  LayoutInflater.from(context).inflate(R.layout.collections_actions_view_panel, this)

  var actions: Seq[CollectionActionItem] = Seq.empty

  var draggingTo: Option[Int] = None

  def leftActionView: Option[TintableButton] = Option(findView(TR.launcher_collections_action_1))

  def rightActionView: Option[TintableButton] = Option(findView(TR.launcher_collections_action_2))

  def load(actions: Seq[CollectionActionItem])
          (implicit theme: NineCardsTheme, presenter: LauncherPresenter, contextWrapper: ActivityContextWrapper): Ui[Any] = {

    def populate(action: CollectionActionItem, position: Int): Tweak[TintableButton] =
      tvText(action.name) +
        tvCompoundDrawablesWithIntrinsicBoundsResources(left = action.resource) +
        vSetPosition(position) +
        tbPressedColor(theme.get(PrimaryColor)) +
        tbResetColor

    def buttonByIndex(index: Int): Option[TintableButton] = index match {
      case 0 => leftActionView
      case 1 => rightActionView
      case _ => None
    }

    this.actions = actions
    Ui.sequence(actions.zipWithIndex map {
      case (action, index) => buttonByIndex(index) <~ populate(action, index)
    }: _*)
  }

  def dragController(action: Int, x: Float, y: Float)(implicit presenter: LauncherPresenter, contextWrapper: ActivityContextWrapper): Unit = {

    def performAction(action: CollectionActionItem) = action.collectionActionType match {
      case CollectionActionAppInfo => presenter.settingsInAddItem()
      case CollectionActionUninstall => presenter.uninstallInAddItem()
      case CollectionActionRemove => presenter.removeCollectionInReorderMode()
      case CollectionActionEdit => presenter.editCollectionInReorderMode()
      case _ =>
    }

    action match {
      case ACTION_DRAG_LOCATION =>
        val newPosition = Some(calculatePosition(x))
        if (newPosition != draggingTo) {
          draggingTo = newPosition
          (this <~ (draggingTo map select getOrElse select(unselectedPosition))).run
        }
      case ACTION_DROP =>
        draggingTo flatMap actions.lift match {
          case Some(action: CollectionActionItem) => performAction(action)
          case _ =>
        }
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case ACTION_DRAG_EXITED =>
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case ACTION_DRAG_ENDED =>
        draggingTo = None
        (this <~ select(unselectedPosition)).run
      case _ =>
    }
  }

  private[this] def calculatePosition(x: Float): Int = x.toInt / (getWidth / actions.length)

  private[this] def select(position: Int)(implicit contextWrapper: ActivityContextWrapper) = Transformer {
    case view: TintableButton if view.getPosition.contains(position) => Ui(view.setPressedColor())
    case view: TintableButton => Ui(view.setDefaultColor())
  }

}

case class CollectionActionItem(name: String, resource: Int, collectionActionType: CollectionActionType)

sealed trait CollectionActionType

case object CollectionActionAppInfo extends CollectionActionType

case object CollectionActionUninstall extends CollectionActionType

case object CollectionActionRemove extends CollectionActionType

case object CollectionActionEdit extends CollectionActionType

