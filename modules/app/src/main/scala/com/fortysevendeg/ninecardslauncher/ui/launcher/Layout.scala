package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.{FrameLayout, LinearLayout}
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.ui.components.TintableImageView
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext, Ui}

import scala.util.Try

trait Layout
  extends Styles {

  var workspaces = slot[LauncherWorkSpaces]

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
      l[LinearLayout](
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerStyle <~ On.click(
            uiShortToast("App Drawer")
          )
        ) <~ appDrawerContentStyle,
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerStyle <~ On.click(
            uiShortToast("App Drawer")
          )
        ) <~ appDrawerContentStyle,
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerStyle <~ On.click(
            uiShortToast("App Drawer")
          )
        ) <~ appDrawerContentStyle,
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerStyle <~ On.click(
            uiShortToast("App Drawer")
          )
        ) <~ appDrawerContentStyle,
        l[FrameLayout](
          w[TintableImageView] <~ appDrawerStyle <~ On.click(
            uiShortToast("App Drawer")
          )
        ) <~ appDrawerContentStyle
      ) <~ drawerBarContentStyle
    ) <~ rootStyle
  )

}
