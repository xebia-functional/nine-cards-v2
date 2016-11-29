package cards.nine.app.ui.commons.dialogs.shortcuts

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, View, ViewGroup}
import cards.nine.models.{NineCardsTheme, Shortcut}
import cards.nine.models.types.theme.DrawerTextColor
import macroid.extras.ImageViewTweaks._
import macroid.extras.TextViewTweaks._
import com.fortysevendeg.ninecardslauncher.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

case class ShortcutAdapter(shortcuts: Seq[Shortcut], onConfigure: (Shortcut) => Unit)
  (implicit activityContext: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderShortcutLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShortcutLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.shortcut_item, parent, false).asInstanceOf[ViewGroup]
    ViewHolderShortcutLayoutAdapter(view)
  }

  override def getItemCount: Int = shortcuts.size

  override def onBindViewHolder(viewHolder: ViewHolderShortcutLayoutAdapter, position: Int): Unit = {
    val shortcut = shortcuts(position)
    viewHolder.bind(shortcut, onConfigure).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}

case class ViewHolderShortcutLayoutAdapter(content: ViewGroup)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
    with TypedFindView {

  lazy val icon = Option(findView(TR.simple_item_icon))

  lazy val name = Option(findView(TR.simple_item_name))

  (name <~ tvColor(theme.get(DrawerTextColor))).run

  def bind(shortcut: Shortcut, onConfigure: (Shortcut) => Unit): Ui[_] =
    (content <~ On.click(Ui(onConfigure(shortcut)))) ~
      (icon <~ (shortcut.icon map ivSrc getOrElse Tweak.blank)) ~
      (name <~ tvText(shortcut.title))

  override def findViewById(id: Int): View = content.findViewById(id)

}