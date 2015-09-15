package com.fortysevendeg.ninecardslauncher.app.ui.drawer

import android.support.v7.widget.RecyclerView
import android.view.View
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.TintableImageView
import com.fortysevendeg.ninecardslauncher.app.ui.components.TintableImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.process.theme.models.{AppDrawerPressedColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Tweak, ContextWrapper}

trait DrawerStyles {

  def drawerAppStyle(implicit context: ContextWrapper, theme: NineCardsTheme): Tweak[TintableImageView] = {
    val elevation = resGetDimensionPixelSize(R.dimen.elevation_pressed)
    Lollipop ifSupportedThen {
      vStateListAnimator(R.anim.elevation_transition) +
        vPaddings(elevation) +
        vCircleOutlineProvider(elevation)
    } getOrElse tivPressedColor(theme.get(AppDrawerPressedColor))
  }

  def recyclerStyle(implicit context: ContextWrapper): Tweak[RecyclerView] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    rvFixedSize +
      vPaddings(padding) +
      vgClipToPadding(false) +
      vOverScrollMode(View.OVER_SCROLL_NEVER)
  }

}
