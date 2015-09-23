package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener
import android.util.AttributeSet
import android.view.{Menu, MenuItem, LayoutInflater}
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Tweak, Ui}

class DrawerTab(context: Context, attrs: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attrs, defStyleAttr)
  with TypedFindView {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.app_drawer_tab, this)

  val maybeIcon: Option[ImageView] = Option(findView(TR.launcher_drawer_tab_icon))

  val maybeArrow: Option[ImageView] = Option(findView(TR.launcher_drawer_tab_arrow))

  val maybePopupMenu: Option[PopupMenu] = maybeArrow map (new PopupMenu(context, _))

  def loadIntTag(tag: Int) = Option(getTag(tag)) map Int.unbox getOrElse 0

}

object DrawerTab {

  def ttInitTab(
    drawableOn: Int,
    drawableOff: Int,
    selected: Boolean = true,
    menuListener: (Int) => Boolean = (_) => false) = Tweak[DrawerTab] { view =>
    runUi((view <~
      vIntTag(R.id.drawable_on, drawableOn) <~
      vIntTag(R.id.drawable_off, drawableOff) <~
      (if (selected) ttSelect else ttUnselect)) ~
      Ui(view.maybePopupMenu foreach (_.setOnMenuItemClickListener(new OnMenuItemClickListener {
        override def onMenuItemClick(menuItem: MenuItem): Boolean = menuListener(menuItem.getItemId)
      }))))
  }

  def ttSelect = Tweak[DrawerTab] { view =>
    runUi(Ui(view.setSelected(true)) ~
      (view.maybeArrow <~ vVisible) ~
      (view.maybeIcon <~ ivSrc(view.loadIntTag(R.id.drawable_on))))
  }

  def ttUnselect = Tweak[DrawerTab] { view =>
    runUi(Ui(view.setSelected(false)) ~
      (view.maybeArrow <~ vInvisible) ~
      (view.maybeIcon <~ ivSrc(view.loadIntTag(R.id.drawable_off))))
  }

  def ttAddMenuOptions(options: Seq[(Int, Int)]) = Tweak[DrawerTab] { view =>
    runUi(Ui(view.maybePopupMenu foreach { menu =>
      options foreach (t => menu.getMenu.add(Menu.NONE, t._1, Menu.NONE, t._2))
    }))
  }

  def ttOpenMenu = Tweak[DrawerTab](_.maybePopupMenu foreach(_.show()))

}