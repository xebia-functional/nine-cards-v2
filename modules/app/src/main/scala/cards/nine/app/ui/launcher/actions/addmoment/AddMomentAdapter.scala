package cards.nine.app.ui.launcher.actions.addmoment

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.NineCardsMomentOps._
import cards.nine.app.ui.commons.styles.CommonStyles
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.NineCardsMoment
import macroid.extras.ImageViewTweaks._
import macroid.extras.TextViewTweaks._
import com.fortysevendeg.ninecardslauncher.TypedResource._
import com.fortysevendeg.ninecardslauncher.{TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class AddMomentAdapter(
  moments: Seq[NineCardsMoment],
  onClick: (NineCardsMoment => Unit))
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderAddMomentLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAddMomentLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.add_moment_item, parent, false)
    new ViewHolderAddMomentLayoutAdapter(view)
  }

  override def getItemCount: Int = moments.size

  override def onBindViewHolder(viewHolder: ViewHolderAddMomentLayoutAdapter, position: Int): Unit =
    viewHolder.bind(moments(position), onClick).run

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}

class ViewHolderAddMomentLayoutAdapter(
  content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView
  with CommonStyles {

  val appsByRow = 5

  lazy val icon = findView(TR.add_moment_icon)

  lazy val name = findView(TR.add_moment_name)

  lazy val description = findView(TR.add_moment_description)

  ((icon <~ iconMomentStyle) ~
    (name <~ titleTextStyle) ~
    (description <~ subtitleTextStyle)).run

  def bind(moment: NineCardsMoment, onClick: (NineCardsMoment => Unit)): Ui[_] = {
    (content <~ On.click(Ui(onClick(moment)))) ~
    (icon <~ ivSrc(moment.getIconCollectionDetail)) ~
      (name <~ tvText(moment.getName)) ~
      (description <~ tvText(moment.getDescription))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

}