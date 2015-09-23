package com.fortysevendeg.ninecardslauncher.app.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.{ImageView, LinearLayout}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid.{Ui, IdGeneration, Tweak}

class TextTab(context: Context, attrs: AttributeSet, defStyleAttr: Int)
  extends LinearLayout(context, attrs, defStyleAttr)
  with TypedFindView {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  LayoutInflater.from(context).inflate(R.layout.app_drawer_tab, this)

  val checkedState: Array[Int] = Array(android.R.attr.state_checked)

  val icon: Option[ImageView] = Option(findView(TR.launcher_drawer_tab_icon))

  val arrow: Option[ImageView] = Option(findView(TR.launcher_drawer_tab_arrow))

  def loadIntTag(tag: Int) = Option(getTag(tag)) map Int.unbox getOrElse 0

}

object TextTab extends IdGeneration {

  def ttInitTab(drawableOn: Int, drawableOff: Int, selected: Boolean = true) = Tweak[TextTab] { view =>
    runUi(view <~
      vIntTag(R.id.drawable_on, drawableOn) <~
      vIntTag(R.id.drawable_off, drawableOff) <~
      (if (selected) ttSelect else ttUnselect))
  }

  def ttSelect = Tweak[TextTab] { view =>
    runUi(Ui(view.setSelected(true)) ~
      (view.arrow <~ vVisible) ~
      (view.icon <~ ivSrc(view.loadIntTag(R.id.drawable_on))))
  }

  def ttUnselect = Tweak[TextTab] { view =>
    runUi(Ui(view.setSelected(false)) ~
      (view.arrow <~ vInvisible) ~
      (view.icon <~ ivSrc(view.loadIntTag(R.id.drawable_off))))
  }

}