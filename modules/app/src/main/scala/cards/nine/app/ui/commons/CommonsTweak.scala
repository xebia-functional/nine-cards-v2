package cards.nine.app.ui.commons

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.{ClipData, Context}
import android.graphics.drawable._
import android.graphics.drawable.shapes.OvalShape
import android.graphics.{Paint, PorterDuff}
import android.os.Vibrator
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.support.v7.widget.{ListPopupWindow, RecyclerView}
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View.{DragShadowBuilder, OnClickListener}
import android.view.inputmethod.{EditorInfo, InputMethodManager}
import android.view.{KeyEvent, View, ViewGroup}
import android.widget.AdapterView.{OnItemClickListener, OnItemSelectedListener}
import android.widget._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.adapters.ThemeArrayAdapter
import cards.nine.app.ui.components.drawables.DrawerBackgroundDrawable
import cards.nine.app.ui.launcher.snails.LauncherSnails._
import cards.nine.app.ui.launcher.types.DragLauncherType
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import macroid.extras.DeviceVersion.{KitKat, Lollipop}
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewGroupTweaks.{W => _, _}
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import com.google.android.flexbox.FlexboxLayout
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global

object CommonsTweak {

  val appsByRow = 5

  def vBackgroundBoxWorkspace(color: Int, horizontalPadding: Int = 0, verticalPadding: Int = 0)(implicit contextWrapper: ContextWrapper): Tweak[View] = {
    val radius = resGetDimensionPixelSize(R.dimen.radius_default)
    Lollipop.ifSupportedThen {
      vBackgroundColor(color) +
        vClipBackground(radius, horizontalPadding = horizontalPadding, verticalPadding = verticalPadding) +
        vElevation(resGetDimensionPixelSize(R.dimen.elevation_box_workspaces))
    } getOrElse {
      val drawable = new DrawerBackgroundDrawable(color, horizontalPadding, verticalPadding, radius)
      vBackground(drawable)
    }
  }

  def vBackgroundCollection(indexColor: Int)(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme): Tweak[View] =
    vBackgroundCircle(theme.getIndexColor(indexColor))

  @SuppressLint(Array("NewApi"))
  def vBackgroundCircle(color: Int)(implicit contextWrapper: ContextWrapper): Tweak[View] = {
    def createShapeDrawable(c: Int) = {
      val drawableColor = new ShapeDrawable(new OvalShape())
      drawableColor.getPaint.setColor(c)
      drawableColor.getPaint.setStyle(Paint.Style.FILL)
      drawableColor.getPaint.setAntiAlias(true)
      drawableColor
    }

    def getDrawable(c: Int): Drawable = {
      val drawableColor = createShapeDrawable(c)
      val padding = resGetDimensionPixelSize(R.dimen.elevation_default)
      val drawableShadow = createShapeDrawable(resGetColor(R.color.shadow_default))
      val layer = new LayerDrawable(Array(drawableShadow, drawableColor))
      layer.setLayerInset(0, padding, padding, padding, 0)
      layer.setLayerInset(1, padding, 0, padding, padding)
      layer
    }
    val elevation = resGetDimensionPixelSize(R.dimen.elevation_default)

    vBackground(Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(color.dark(0.2f))),
        createShapeDrawable(color),
        javaNull)
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), getDrawable(color.dark()))
      states.addState(Array.emptyIntArray, getDrawable(color))
      states
    }) + (Lollipop ifSupportedThen vElevation(elevation) getOrElse Tweak.blank)
  }

  def vSetPosition(position: Int): Tweak[View] = vTag(R.id.position, position)

  def vSetType(t: String) = vTag(R.id.view_type, t)

  def vAddField[T](key: String, value: T) = Tweak[View] { view =>
    view.setTag(R.id.fields_map, view.getFieldsMap + ((key, value)))
  }

  def vRemoveField(key: String) = Tweak[View] { view =>
    view.setTag(R.id.fields_map, view.getFieldsMap - key)
  }

  def vUseLayerHardware = vTag(R.id.use_layer_hardware, "")

  def vLayerHardware(activate: Boolean) = Transformer {
    case v: View if v.hasLayerHardware => v <~ (if (activate) vLayerTypeHardware() else vLayerTypeNone())
  }

  def vListThemedPopupWindowShow(
    icons: Seq[Int] = Seq.empty,
    values: Seq[String],
    onItemClickListener: (Int) ⇒ Unit,
    width: Option[Int] = None,
    height: Option[Int] = None,
    horizontalOffset: Option[Int] = None
  )(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme) =
    Tweak[View] { view =>
      val listPopupWindow = new ListPopupWindow(contextWrapper.bestAvailable)
      listPopupWindow.setAdapter(new ThemeArrayAdapter(icons, values))
      listPopupWindow.setAnchorView(view)
      horizontalOffset foreach listPopupWindow.setHorizontalOffset
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

  def ivReloadPager(currentPage: Int)(implicit contextWrapper: ContextWrapper) = Transformer {
    case imageView: ImageView if imageView.isPosition(currentPage) =>
      imageView <~ vActivated(true) <~~ pagerAppear
    case imageView: ImageView =>
      imageView <~ vActivated(false)
  }

  def vLauncherSnackbar(message: Int, args: Seq[String] = Seq.empty, lenght: Int = Snackbar.LENGTH_SHORT)
    (implicit contextWrapper: ContextWrapper, systemBarsTint: SystemBarsTint): Tweak[View] = Tweak[View] { view =>
      val snackbar = Snackbar.make(view, contextWrapper.application.getString(message, args:_*), lenght)
      snackbar.getView.getLayoutParams match {
        case params : FrameLayout.LayoutParams =>
          val bottom = KitKat.ifSupportedThen (systemBarsTint.getNavigationBarHeight) getOrElse 0
          params.setMargins(0, 0, 0, bottom)
          snackbar.getView.setLayoutParams(params)
        case _ =>
      }
      snackbar.show()
    }

  def vLauncherSnackbarWithAction(message: Int, resAction: Int, action: () => Unit, args: Seq[String] = Seq.empty, lenght: Int = Snackbar.LENGTH_SHORT)
    (implicit contextWrapper: ContextWrapper, systemBarsTint: SystemBarsTint): Tweak[View] = Tweak[View] { view =>
    val snackbar = Snackbar.make(view, contextWrapper.application.getString(message, args:_*), lenght)
    snackbar.getView.getLayoutParams match {
      case params : FrameLayout.LayoutParams =>
        val bottom = KitKat.ifSupportedThen (systemBarsTint.getNavigationBarHeight) getOrElse 0
        params.setMargins(0, 0, 0, bottom)
        snackbar.getView.setLayoutParams(params)
      case _ =>
    }
    snackbar.setAction(resAction, new OnClickListener {
      override def onClick(v: View): Unit = action()
    })
    snackbar.show()
  }

  def vStartDrag(
    dragLauncherType: DragLauncherType,
    shadow: DragShadowBuilder,
    label: Option[String] = None,
    text: Option[String] = None)
    (implicit contextWrapper: ContextWrapper): Tweak[View] =
    Tweak[View] { view =>
      val dragData = ClipData.newPlainText(label getOrElse "", text getOrElse "")
      view.startDrag(dragData, shadow, DragObject(shadow, dragLauncherType), 0)
    }

  def fblAddItems[T](items: Seq[T], onImageTweak: (T) => Tweak[ImageView])
    (implicit contextWrapper: ContextWrapper, uiContext: UiContext[_]): Tweak[FlexboxLayout] = Tweak[FlexboxLayout] { view =>
    val width = view.getWidth
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    val sizeIcon = resGetDimensionPixelSize(R.dimen.size_icon_item_collections_content) + (padding * 2)

    def getViews(widthView: Int) = {
      val widthSize = widthView / appsByRow
      items map { item =>
        (w[ImageView] <~
          lp[FlexboxLayout](widthSize, sizeIcon) <~
          vPadding(0, padding, 0, padding) <~
          onImageTweak(item)).get
      }
    }

    (view <~ (if (width > 0) {
      vgAddViews(getViews(width))
    } else {
      vGlobalLayoutListener { v => {
        view <~ vgAddViews(getViews(v.getWidth))
      }}
    })).run
  }

}