package cards.nine.app.ui.wizard.jobs

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.widget.ImageView
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, SystemBarsTint, UiContext}
import cards.nine.commons.javaNull
import cards.nine.commons.services.TaskService._
import cards.nine.process.collection.models.PackagesByCategory
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

class NewConfigurationUiActions(dom: WizardDOM with WizardUiListener)(implicit val context: ActivityContextWrapper, val uiContext: UiContext[_])
  extends WizardStyles
  with ImplicitsUiExceptions {

  val numberOfScreens = 6

  lazy val systemBarsTint = new SystemBarsTint

  def loadFirstStep(): TaskService[Unit] = {
    val stepView = LayoutInflater.from(context.bestAvailable).inflate(R.layout.wizard_new_conf_step_0, javaNull)
    val resColor = R.color.wizard_background_new_conf_step_0
    ((dom.newConfigurationStep <~
      vgRemoveAllViews <~
      vgAddView(stepView)) ~
      systemBarsTint.updateStatusColor(resGetColor(resColor)) ~
      createPagers() ~
      selectPager(0, resColor) ~
      (dom.newConfigurationNext <~
        On.click(Ui(dom.onLoadBetterCollections())) <~
        tvColorResource(resColor))).toService
  }

  def loadSecondStep(collections: Seq[PackagesByCategory]): TaskService[Unit] = {
    val stepView = LayoutInflater.from(context.bestAvailable).inflate(R.layout.wizard_new_conf_step_1, javaNull)
    val resColor = R.color.wizard_background_new_conf_step_0
    ((dom.newConfigurationStep <~
      vgRemoveAllViews <~
      vgAddView(stepView)) ~
      selectPager(1, resColor) ~
      (dom.newConfigurationStep1Description <~ tvText(s"${collections.length} collections"))).toService
  }

  private[this] def createPagers(): Ui[Any] = {
    val views = (0 until numberOfScreens) map { position =>
      (w[ImageView] <~
        paginationItemStyle <~
        vSetPosition(position)).get
    }
    dom.newConfigurationPagers <~ vgAddViews(views)
  }

  private[this] def selectPager(position: Int, resColor: Int): Ui[Any] = dom.newConfigurationPagers <~ Transformer {
    case i: ImageView if i.getPosition.contains(position) => i <~ vBackground(circleDrawable(resGetColor(resColor)))
    case i: ImageView => i <~ vBackground(circleDrawable())
  }

  private[this] def circleDrawable(color: Int = Color.LTGRAY): ShapeDrawable = {
    val drawable = new ShapeDrawable(new OvalShape())
    drawable.getPaint.setColor(color)
    drawable.getPaint.setAntiAlias(true)
    drawable
  }

}
