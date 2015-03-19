package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.{ImageView, TextView, LinearLayout}
import com.fortysevendeg.ninecardslauncher.ui.components.{TintableImageView, TestMultipleTypesAnimatedWorkSpaces}
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import macroid.FullDsl._
import macroid.{Ui, ActivityContext, AppContext}

import scala.util.Try

trait Layout
  extends Styles {

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
      l[TestMultipleTypesAnimatedWorkSpaces]() <~ workspaceStyle,
      l[LinearLayout](
        w[TintableImageView] <~ appDrawerStyle <~ On.click(
          uiShortToast("App Drawer")
        ),
        w[TintableImageView] <~ appDrawerStyle <~ On.click(
          uiShortToast("App Drawer")
        ),
        w[TintableImageView] <~ appDrawerStyle <~ On.click(
          uiShortToast("App Drawer")
        ),
        w[TintableImageView] <~ appDrawerStyle <~ On.click(
          uiShortToast("App Drawer")
        ),
        w[TintableImageView] <~ appDrawerStyle <~ On.click(
          uiShortToast("App Drawer")
        )
      ) <~ drawerBarContentStyle
    ) <~ rootStyle
  )

}
