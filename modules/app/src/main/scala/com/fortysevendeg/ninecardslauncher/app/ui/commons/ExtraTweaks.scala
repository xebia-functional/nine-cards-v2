package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.{Drawable, ShapeDrawable}
import android.graphics.{Color, Outline, PorterDuff, Typeface}
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
import android.support.design.widget.{FloatingActionButton, NavigationView, Snackbar}
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.{GravityCompat, TintableBackgroundView}
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener
import android.support.v7.widget.{ListPopupWindow, PopupMenu, RecyclerView, SwitchCompat, Toolbar}
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.view.{MenuItem, View, ViewGroup, ViewOutlineProvider}
import android.widget.AdapterView.OnItemClickListener
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Transformer, Tweak, Ui}

/**
  * This tweaks should be moved to Macroid-Extras
  */
object ExtraTweaks {

  def vClipBackground(radius: Int, padding: Int = 0): Tweak[View] = Tweak[View] {
    view =>
      view.setOutlineProvider(new ViewOutlineProvider() {
        override def getOutline(view: View, outline: Outline): Unit =
          outline.setRoundRect(padding, padding, view.getWidth - padding, view.getHeight - padding, radius)
      })
      view.setClipToOutline(true)
  }

  def vClearFocus = Tweak[View](_.clearFocus())

  def vRequestFocus = Tweak[View](_.requestFocus())

  def vEnabled(enabled: Boolean) = Tweak[View](_.setEnabled(enabled))

  def vTag2[T](tag: T) = Tweak[View](_.setTag(tag)) // We should use this in Macroid-Extras instead of vTag

  def vTag2[T](id: Int, tag: T) = Tweak[View](_.setTag(id, tag))

  def vgAddViewByIndex[V <: View](view: V, index: Int): Tweak[ViewGroup] = Tweak[ViewGroup](_.addView(view, index))

  def vBackgroundTint(color: Int) = Tweak[View] {
    case t: TintableBackgroundView => t.setSupportBackgroundTintList(ColorStateList.valueOf(color))
    case _ =>
  }

  def vSelected(selected: Boolean) = Tweak[View](_.setSelected(selected))

  def vClickable(clickable: Boolean): Tweak[FrameLayout] = Tweak[View]( _.setClickable(clickable))

  def vClearClick: Tweak[View] = Tweak[View]( _.setOnClickListener(javaNull))

  def vInvalidate: Tweak[View] = Tweak[View]( _.invalidate())

  def vPopupMenuShow(menu: Int, onMenuItemClickListener: (MenuItem) => Boolean)(implicit contextWrapper: ContextWrapper) =
    Tweak[View] { view =>
      val popupMenu = new PopupMenu(contextWrapper.bestAvailable, view)
      popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener {
        override def onMenuItemClick(item: MenuItem): Boolean = onMenuItemClickListener(item)
      })
      popupMenu.inflate(menu)
      popupMenu.show()
    }

  def vPopupMenuShow(menu: Seq[String], onMenuItemClickListener: (MenuItem) => Boolean)(implicit contextWrapper: ContextWrapper) =
    Tweak[View] { view =>
      val popupMenu = new PopupMenu(contextWrapper.bestAvailable, view)
      popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener {
        override def onMenuItemClick(item: MenuItem): Boolean = onMenuItemClickListener(item)
      })
      menu.zipWithIndex foreach {
        case (item, order) => popupMenu.getMenu.add(0, 0, order, item)
      }
      popupMenu.show()
    }

  def vListPopupWindowShow(
    layout: Int,
    menu: Seq[String],
    onItemClickListener: (Int) => Unit,
    width: Option[Int] = None,
    height: Option[Int] = None)(implicit contextWrapper: ContextWrapper) =
    Tweak[View] { view =>
      val listPopupWindow = new ListPopupWindow(contextWrapper.bestAvailable)
      listPopupWindow.setAdapter(new ArrayAdapter(contextWrapper.bestAvailable, layout, menu.toArray))
      listPopupWindow.setAnchorView(view)
      width foreach listPopupWindow.setWidth
      height foreach listPopupWindow.setHeight
      listPopupWindow.setModal(true)
      listPopupWindow.setOnItemClickListener(new OnItemClickListener {
        override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
          onItemClickListener(position)
          listPopupWindow.dismiss()
        }
      })
      listPopupWindow.show()
    }

  def sAdapter(adapter: SpinnerAdapter) = Tweak[Spinner](_.setAdapter(adapter))

  def etHideKeyboard(implicit contextWrapper: ContextWrapper) = Tweak[EditText] { editText =>
    Option(contextWrapper.application.getSystemService(Context.INPUT_METHOD_SERVICE)) map {
      case imm: InputMethodManager => imm.hideSoftInputFromWindow(editText.getWindowToken, 0)
    }
  }

  def tvCompoundDrawablesWithIntrinsicBounds2(
    left: Option[Drawable] = None,
    top: Option[Drawable] = None,
    right: Option[Drawable] = None,
    bottom: Option[Drawable] = None) =
    Tweak[TextView](_.setCompoundDrawablesWithIntrinsicBounds(left.orNull, top.orNull, right.orNull, bottom.orNull))

  def tvCompoundDrawablesWithIntrinsicBounds2(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) =
    Tweak[TextView](_.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom))

  val tvNormalMedium: Tweak[TextView] = Tweak[TextView](x => x.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL)))

  val tvBoldMedium: Tweak[TextView] = Tweak[TextView](x => x.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD)))

  val tvItalicMedium: Tweak[TextView] = Tweak[TextView](x => x.setTypeface(Typeface.create("sans-serif-medium", Typeface.ITALIC)))

  val tvBoldItalicMedium: Tweak[TextView] = Tweak[TextView](x => x.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD_ITALIC)))

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

  def tbChangeHeightLayout(height: Int) = Tweak[Toolbar] { view =>
    view.getLayoutParams.height = height
    view.requestLayout()
  }

  def dlStatusBarBackground(res: Int) = Tweak[DrawerLayout](_.setStatusBarBackground(res))

  def dlOpenDrawer = Tweak[DrawerLayout](_.openDrawer(GravityCompat.START))

  def dlCloseDrawer = Tweak[DrawerLayout](_.closeDrawer(GravityCompat.START))

  def nvNavigationItemSelectedListener(onItem: (Int) => Boolean) = Tweak[NavigationView](_.setNavigationItemSelectedListener(
    new OnNavigationItemSelectedListener {
      override def onNavigationItemSelected(menuItem: MenuItem): Boolean = onItem(menuItem.getItemId)
    }))

  def pbColor(color: Int) = Tweak[ProgressBar](_.getIndeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY))

  def fbaColorResource(id: Int)(implicit contextWrapper: ContextWrapper) = Tweak[FloatingActionButton] { view =>
    view.setBackgroundTintList(contextWrapper.application.getResources.getColorStateList(id))
    view.setRippleColor(ColorsUtils.getColorDark(resGetColor(id)))
  }

  def fbaColor(color: Int)(implicit contextWrapper: ContextWrapper) = Tweak[FloatingActionButton] { view =>
    view.setBackgroundTintList(ColorStateList.valueOf(color))
    view.setRippleColor(ColorsUtils.getColorDark(color))
  }

  def scColor(color: Int)(implicit contextWrapper: ContextWrapper) = Tweak[SwitchCompat] { view =>
    val colorOff = ColorsUtils.getColorDark(color, .1f)
    val states = Array(
      Array[Int](-android.R.attr.state_enabled),
      Array[Int](-android.R.attr.state_checked),
      Array[Int]()
    )
    val thumbColors = Array[Int](color, colorOff, color)
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

  def scCheckedChangeListener(onCheckedChange: (Boolean) => Unit)(implicit contextWrapper: ContextWrapper) = Tweak[SwitchCompat](
    _.setOnCheckedChangeListener(new OnCheckedChangeListener {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean): Unit = onCheckedChange(isChecked)
    })
  )

  def rvSwapAdapter[VH <: RecyclerView.ViewHolder](adapter: RecyclerView.Adapter[VH]): Tweak[RecyclerView] =
    Tweak[RecyclerView](_.swapAdapter(adapter, false))

  def rvScrollToTop: Tweak[RecyclerView] = Tweak[RecyclerView](_.scrollToPosition(0))

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

object CommonsTweak {

  def vBackgroundBoxWorkspace(color: Int)(implicit contextWrapper: ContextWrapper): Tweak[View] = {
    val radius = resGetDimensionPixelSize(R.dimen.radius_default)
    Lollipop.ifSupportedThen {
      vBackgroundColor(color) +
        ExtraTweaks.vClipBackground(radius) +
        vElevation(resGetDimensionPixelSize(R.dimen.elevation_box_workspaces))
    } getOrElse {
      val s = 0 until 8 map (_ => radius.toFloat)
      val d = new ShapeDrawable(new RoundRectShape(s.toArray, javaNull, javaNull))
      d.getPaint.setColor(color)
      vBackground(d)
    }
  }

  def vUseLayerHardware = vTag(R.id.use_layer_hardware, "")

  def vLayerHardware(activate: Boolean) = Transformer {
    case v: View if Option(v.getTag(R.id.use_layer_hardware)).isDefined => v <~ (if (activate) vLayerTypeHardware() else vLayerTypeNone())
  }

}
