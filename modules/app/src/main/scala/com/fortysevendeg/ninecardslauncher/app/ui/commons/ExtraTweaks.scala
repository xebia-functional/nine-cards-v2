package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.content.res.ColorStateList
import android.graphics.{Color, PorterDuff}
import android.graphics.drawable.Drawable
import android.support.design.widget.{Snackbar, FloatingActionButton}
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.{RecyclerView, SwitchCompat, Toolbar}
import android.view.View
import android.view.View.OnClickListener
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.{CompoundButton, ProgressBar, RadioGroup}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import macroid.FullDsl._
import macroid.{ContextWrapper, Tweak, Ui}

/**
 * This tweaks should be moved to Macroid-Extras
 */
object ExtraTweaks {

  def vIntTag(tag: Int) = Tweak[View](_.setTag(tag))

  def vIntTag(id: Int, tag: Int) = Tweak[View](_.setTag(id, tag))

  def tbBackgroundColor(color: Int) = Tweak[Toolbar](_.setBackgroundColor(color))

  def tbTitle(res: Int) = Tweak[Toolbar](_.setTitle(res))

  def tbTitle(title: String) = Tweak[Toolbar](_.setTitle(title))

  def tbLogo(res: Int) = Tweak[Toolbar](_.setLogo(res))

  def tbLogo(drawable: Drawable) = Tweak[Toolbar](_.setLogo(drawable))

  def tbNavigationIcon(res: Int) = Tweak[Toolbar](_.setNavigationIcon(res))

  def tbNavigationIcon(drawable: Drawable) = Tweak[Toolbar](_.setNavigationIcon(drawable))

  def tbNavigationOnClickListener(click: (View) => Ui[_]) = Tweak[Toolbar](_.setNavigationOnClickListener(new OnClickListener {
    override def onClick(v: View): Unit = runUi(click(v))
  }))

  def pbColor(color: Int) = Tweak[ProgressBar](_.getIndeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY))

  def fbaColor(id: Int)(implicit contextWrapper: ContextWrapper) = Tweak[FloatingActionButton] { view =>
    view.setBackgroundTintList(contextWrapper.application.getResources.getColorStateList(id))
    view.setRippleColor(ColorsUtils.getColorDark(resGetColor(id)))
  }

  def scColor(colorOn: Int, colorOff: Int)(implicit contextWrapper: ContextWrapper) = Tweak[SwitchCompat] { view =>
    val states = Array(
      Array[Int](-android.R.attr.state_enabled),
      Array[Int](-android.R.attr.state_checked),
      Array[Int]()
    )
    val thumbColors = Array[Int](colorOn, colorOff, colorOn)
    val thumbStateList = new ColorStateList(states, thumbColors)

    val thumbDrawable = DrawableCompat.wrap(view.getThumbDrawable)
    DrawableCompat.setTintList(thumbDrawable.mutate(), thumbStateList)
    view.setThumbDrawable(thumbDrawable)

    val trackColorOn = ColorsUtils.setAlpha(Color.BLACK, .35f)
    val trackColorOff = ColorsUtils.setAlpha(Color.BLACK, .1f)

    val trackColors = Array[Int](trackColorOn, trackColorOff, trackColorOn)
    val trackStateList = new ColorStateList(states, trackColors)

    val trackDrawable = DrawableCompat.wrap(view.getTrackDrawable)
    DrawableCompat.setTintList(trackDrawable.mutate(), trackStateList)
    view.setTrackDrawable(trackDrawable)
  }

  def scChecked(checked: Boolean)(implicit contextWrapper: ContextWrapper) = Tweak[SwitchCompat](_.setChecked(checked))

  def scCheckedChangeListener(onCheckedChange: (Boolean) => Unit)(implicit contextWrapper: ContextWrapper) = Tweak[SwitchCompat] (
    _.setOnCheckedChangeListener(new OnCheckedChangeListener {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean): Unit = onCheckedChange(isChecked)
    })
  )

  def rvSwapAdapter[VH <: RecyclerView.ViewHolder](adapter: RecyclerView.Adapter[VH]): Tweak[RecyclerView] =
    Tweak[RecyclerView](_.swapAdapter(adapter, false))


  def uiSnackbarShort(res: Int) = Tweak[View] { view =>
    runUi(Ui(Snackbar.make(view, res, Snackbar.LENGTH_SHORT).show()))
  }

  def uiSnackbarLong(res: Int) = Tweak[View] { view =>
    runUi(Ui(Snackbar.make(view, res, Snackbar.LENGTH_LONG).show()))
  }

  def uiSnackbarIndefinite(res: Int) = Tweak[View] { view =>
    runUi(Ui(Snackbar.make(view, res, Snackbar.LENGTH_INDEFINITE).show()))
  }

  def uiSnackbarShort(message: String) = Tweak[View] { view =>
    runUi(Ui(Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()))
  }

  def uiSnackbarLong(message: String) = Tweak[View] { view =>
    runUi(Ui(Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()))
  }

  def uiSnackbarIndefinite(message: String) = Tweak[View] { view =>
    runUi(Ui(Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).show()))
  }

}
