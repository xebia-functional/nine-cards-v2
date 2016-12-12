package cards.nine.app.ui.components.preferences

import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.ImageView
import cards.nine.app.ui.commons.SafeUi._
import cards.nine.commons.javaNull
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ImageViewTweaks._
import macroid.extras.ViewTweaks._
import macroid.extras.UIActionsExtras._
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.{R, TR}
import macroid.FullDsl._
import macroid._
import macroid.extras.TextViewTweaks._

import scala.util.Random

class TeamPreference(context: Context, attrs: AttributeSet, defStyle: Int)
  extends Preference(context, attrs, defStyle) {

  def this(context: Context) = this(context, javaNull, 0)

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  implicit lazy val contextWrapper = ContextWrapper(context)

  override def onCreateView(parent: ViewGroup): View = {
    val teamView = LayoutInflater.from(context).inflate(R.layout.about_team_preference, javaNull).asInstanceOf[ViewGroup]

    import cards.nine.app.ui.commons.ViewGroupFindViews._

    val teamLayout = findView(TR.preference_about_team).run(teamView)

    import cards.nine.app.ui.commons.Team._

    val padding = resGetDimensionPixelSize(R.dimen.padding_default)

    val teamViews = Random.shuffle(team) map { person =>
      (w[ImageView] <~
        vWrapContent <~
        vPaddings(padding) <~
        ivSrc(person._2) <~
        On.click(uiLongToast(resGetString(R.string.team_name, person._1)))).get
    }

    (teamLayout <~ vgAddViews(teamViews)).run

    teamView
  }

}
