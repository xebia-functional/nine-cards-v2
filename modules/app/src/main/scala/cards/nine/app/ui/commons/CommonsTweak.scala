package cards.nine.app.ui.commons

import java.io.Closeable

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable._
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.{Fragment, FragmentManager}
import android.support.v7.widget.{ListPopupWindow, RecyclerView}
import android.view.View.{DragShadowBuilder, OnClickListener}
import android.view.{Gravity, View, ViewGroup}
import android.widget.AdapterView.OnItemClickListener
import android.widget._
import cards.nine.app.ui.commons.AppLog._
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.adapters.ThemeArrayAdapter
import cards.nine.app.ui.components.drawables.DrawerBackgroundDrawable
import cards.nine.app.ui.commons.dialogs.wizard.{WizardInlineFragment, WizardInlineType}
import cards.nine.app.ui.launcher.snails.LauncherSnails._
import cards.nine.app.ui.launcher.types.{DragLauncherType, DragObject}
import cards.nine.commons._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{DrawerBackgroundColor, DrawerTextColor, PrimaryColor}
import com.fortysevendeg.ninecardslauncher.R
import com.google.android.flexbox.FlexboxLayout
import macroid.FullDsl._
import macroid._
import macroid.extras.DeviceVersion.{KitKat, Lollipop}
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Try}

object CommonsTweak {

  val appsByRow = 5

  lazy val listUserWizardInline = Seq(
    ("Ana", R.drawable.ana),
    ("Domin", R.drawable.domin),
    ("Fede", R.drawable.fede),
    ("Javi" , R.drawable.javi_pacheco),
    ("Jorge", R.drawable.jorge_galindo),
    ("Paco", R.drawable.paco),
    ("Raúl", R.drawable.raul_raja),
    ("Diego", R.drawable.diego),
    ("Isra", R.drawable.isra),
    ("Aaron", R.drawable.aaron),
    ("Ale", R.drawable.ale),
    ("Andy", R.drawable.andy),
    ("Javi", R.drawable.javi_siloniz),
    ("Benjy", R.drawable.benjy),
    ("Fran", R.drawable.fran),
    ("John", R.drawable.john),
    ("Juan", R.drawable.juan),
    ("Juan Pedro", R.drawable.juan_pedro),
    ("Justin", R.drawable.justin),
    ("Nick", R.drawable.nick),
    ("Maureen", R.drawable.maureen),
    ("Noel", R.drawable.noel),
    ("Rafa", R.drawable.rafa)
  )

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

  def vLauncherWizardSnackbar(wizardInlineType: WizardInlineType, forceNavigationBarHeight: Boolean = true)
    (implicit contextWrapper: ContextWrapper,
      fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
      theme: NineCardsTheme,
      systemBarsTint: SystemBarsTint): Tweak[View] = Tweak[View] { view =>

    def showDialog = Ui {
      val dialog = new WizardInlineFragment()
      val bundle = new Bundle()
      bundle.putString(WizardInlineFragment.wizardInlineTypeKey, wizardInlineType.toString)
      dialog.setArguments(bundle)
      dialog.show(fragmentManagerContext.manager, "wizard-inline-dialog")
    }

    val (userSelectedName, userSelectedIcon) = listUserWizardInline(Random.nextInt(listUserWizardInline.length))
    val text = resGetString(R.string.wizard_inline_message, userSelectedName, wizardInlineType.name)
    val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
    val rootView = snackbar.getView.asInstanceOf[ViewGroup]
    (rootView <~
      vBackgroundColor(theme.get(DrawerBackgroundColor)) <~
      Transformer {
        case button: Button =>
          button <~
            tvColor(theme.get(PrimaryColor))
        case textView: TextView =>
          textView <~
            tvColor(theme.get(DrawerTextColor)) <~
            tvMaxLines(3) <~
            tvDrawablePadding(resGetDimensionPixelSize(R.dimen.padding_default)) <~
            tvGravity(Gravity.CENTER_VERTICAL) <~
            tvCompoundDrawablesWithIntrinsicBoundsResources(left = userSelectedIcon) <~
            On.click(showDialog ~ Ui(snackbar.dismiss()))
      }).run
    (forceNavigationBarHeight, rootView.getLayoutParams) match {
      case (true, params: FrameLayout.LayoutParams) =>
        val bottom = KitKat.ifSupportedThen(systemBarsTint.getNavigationBarHeight) getOrElse 0
        params.setMargins(0, 0, 0, bottom)
        snackbar.getView.setLayoutParams(params)
      case _ =>
    }
    snackbar.setAction(R.string.wizard_inline_show, new OnClickListener {
      override def onClick(v: View): Unit = showDialog.run
    })
    snackbar.show()
  }

  def vLauncherSnackbar(message: Int, args: Seq[String] = Seq.empty, length: Int = Snackbar.LENGTH_SHORT)
    (implicit contextWrapper: ContextWrapper, systemBarsTint: SystemBarsTint): Tweak[View] = Tweak[View] { view =>
      val snackbar = Snackbar.make(view, contextWrapper.application.getString(message, args:_*), length)
      snackbar.getView.getLayoutParams match {
        case params : FrameLayout.LayoutParams =>
          val bottom = KitKat.ifSupportedThen (systemBarsTint.getNavigationBarHeight) getOrElse 0
          params.setMargins(0, 0, 0, bottom)
          snackbar.getView.setLayoutParams(params)
        case _ =>
      }
      snackbar.show()
    }

  def vLauncherSnackbarWithAction(message: Int, resAction: Int, action: () => Unit, args: Seq[String] = Seq.empty, length: Int = Snackbar.LENGTH_SHORT)
    (implicit contextWrapper: ContextWrapper, systemBarsTint: SystemBarsTint): Tweak[View] = Tweak[View] { view =>
    val snackbar = Snackbar.make(view, contextWrapper.application.getString(message, args:_*), length)
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

  def rvCloseAdapter() = Tweak[RecyclerView] { view =>

    def safeClose(closeable: Closeable): Unit = Try(closeable.close()) match {
      case Failure(ex) => printErrorMessage(ex)
      case _ =>
    }

    Ui {
      view.getAdapter match {
        case a: Closeable => safeClose(a)
        case _ =>
      }
    }
  }

}