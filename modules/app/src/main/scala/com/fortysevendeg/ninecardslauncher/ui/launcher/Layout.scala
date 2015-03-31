package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.{ImageView, FrameLayout, LinearLayout}
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServicesComponent
import com.fortysevendeg.ninecardslauncher.ui.components.TintableImageView
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext, Ui}

import scala.util.Try

trait Layout
  extends Styles {

  self : PersistentServicesComponent =>

  var workspaces = slot[LauncherWorkSpaces]

  var appDrawerBar = slot[LinearLayout]

  var pager = slot[LinearLayout]

  def content(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[LinearLayout](
      l[LinearLayout](
        w[TintableImageView] <~ burgerButtonStyle <~ On.click(
          uiShortToast("Open Menu")
        ),
        w[TintableImageView] <~ googleButtonStyle <~ On.click(
          Ui {
            Try {
              val intent = new Intent(Intent.ACTION_WEB_SEARCH)
              context.get.startActivity(intent)
            }
          }
        ),
        w[TintableImageView] <~ micButtonStyle <~ On.click(
          Ui {
            Try {
              val intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH)
              context.get.startActivity(intent)
            }
          }
        )
      ) <~ searchContentStyle,
      l[LauncherWorkSpaces]() <~ workspaceStyle <~ wire(workspaces),
      l[LinearLayout]() <~ paginationContentStyle <~ wire(pager),
      l[LinearLayout](
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerAppStyle <~ On.click(
            uiShortToast("App 1")
          )
        ) <~ appDrawerContentStyle,
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerAppStyle <~ On.click(
            uiShortToast("App 2")
          )
        ) <~ appDrawerContentStyle,
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerStyle <~ On.click(
            uiShortToast("App Drawer")
          )
        ) <~ appDrawerContentStyle,
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerAppStyle <~ On.click(
            uiShortToast("App 3")
          )
        ) <~ appDrawerContentStyle,
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerAppStyle <~ On.click(
            uiShortToast("App 4")
          )
        ) <~ appDrawerContentStyle
      ) <~ drawerBarContentStyle <~ wire(appDrawerBar)
    ) <~ rootStyle
  )

  def pagination(position: Int)(implicit appContext: AppContext, context: ActivityContext) = getUi(
    w[ImageView] <~ paginationItemStyle <~ vTag(position.toString)
  )

}
