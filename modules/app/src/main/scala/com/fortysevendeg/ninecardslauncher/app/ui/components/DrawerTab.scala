package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener
import android.util.AttributeSet
import android.view.{LayoutInflater, Menu, MenuItem}
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

  def getSelectedMenuItem = loadIntTag(R.id.selected_item)

}

object DrawerTab {

  def ttInitTab(
    drawableOn: Int,
    drawableOff: Int,
    selected: Boolean = true,
    menuResource: Int,
    menuItemId: Int,
    menuListener: (Int) => Unit = (_) => ()) = Tweak[DrawerTab] { view =>
    runUi((view <~
      vIntTag(R.id.drawable_on, drawableOn) <~
      vIntTag(R.id.drawable_off, drawableOff) <~
      (if (selected) ttSelect else ttUnselect)) ~
      Ui {
        view.maybePopupMenu foreach { popupMenu =>
          popupMenu.inflate(menuResource)
          selectedMenuItem(popupMenu.getMenu, menuItemId) foreach { menuItem =>
            menuItem.setChecked(true)
            view.setTag(R.id.selected_item, menuItem.getItemId)
            if (selected) menuListener(menuItem.getItemId)
          }
          popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener {
            override def onMenuItemClick(menuItem: MenuItem): Boolean =
              if (menuItem.isChecked) {
                false
              } else {
                menuItem.setChecked(true)
                view.setTag(R.id.selected_item, menuItem.getItemId)
                menuListener(menuItem.getItemId)
                true
              }
          })
        }
      })
  }

  private[this] def selectedMenuItem(menu: Menu, menuItemId: Int): Option[MenuItem] =
    Option(menu.findItem(menuItemId)) match {
      case Some(m) => Some(m)
      case None if menu.size() > 0 => Some(menu.getItem(0))
      case _ => None
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

  def ttOpenMenu = Tweak[DrawerTab](_.maybePopupMenu foreach (_.show()))

}