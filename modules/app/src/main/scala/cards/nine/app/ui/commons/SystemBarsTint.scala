/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.commons

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import macroid.extras.DeviceVersion.{Lollipop, Marshmallow}
import cards.nine.utils.SystemBarTintManager
import macroid.{ActivityContextWrapper, Ui}

class SystemBarsTint(implicit activityContextWrapper: ActivityContextWrapper) {

  private[this] lazy val systemBarTintManager = new SystemBarTintManager(
    activityContextWrapper.getOriginal)

  def initAllSystemBarsTint(): Ui[_] =
    Ui(Lollipop ifNotSupportedThen {
      systemBarTintManager.setStatusBarTintEnabled(true)
      systemBarTintManager.setNavigationBarTintEnabled(true)
    })

  def initSystemStatusBarTint(): Ui[_] =
    Ui(Lollipop ifNotSupportedThen {
      systemBarTintManager.setStatusBarTintEnabled(true)
    })

  def initSystemNavigationBarTint(): Ui[_] =
    Ui(Lollipop ifNotSupportedThen {
      systemBarTintManager.setNavigationBarTintEnabled(true)
    })

  def updateStatusToBlack(): Ui[_] = updateStatusColor(Color.BLACK)

  def updateStatusToTransparent(): Ui[_] = updateStatusColor(Color.TRANSPARENT)

  @SuppressLint(Array("NewApi"))
  def updateStatusColor(color: Int): Ui[_] =
    Ui {
      Lollipop ifSupportedThen {
        activityContextWrapper.getOriginal.getWindow.setStatusBarColor(color)
      } getOrElse {
        systemBarTintManager.setStatusBarTintColor(color)
      }
    }

  def updateNavigationToBlack(): Ui[_] = updateNavigationColor(Color.BLACK)

  def updateNavigationToTransparent(): Ui[_] =
    updateNavigationColor(Color.TRANSPARENT)

  @SuppressLint(Array("NewApi"))
  def updateNavigationColor(color: Int): Ui[_] =
    Ui {
      Lollipop ifSupportedThen {
        activityContextWrapper.getOriginal.getWindow.setNavigationBarColor(color)
      } getOrElse {
        systemBarTintManager.setNavigationBarTintColor(color)
      }
    }

  def lightStatusBar(): Ui[_] =
    Ui(
      Marshmallow ifSupportedThen
        activityContextWrapper.getOriginal.getWindow.getDecorView
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR))

  def defaultStatusBar(): Ui[_] =
    Ui(
      Marshmallow ifSupportedThen
        activityContextWrapper.getOriginal.getWindow.getDecorView.setSystemUiVisibility(0))

  def hasNavigationBar = systemBarTintManager.getConfig.hasNavigationBar

  def getNavigationBarHeight =
    systemBarTintManager.getConfig.getNavigationBarHeight

  def getStatusBarHeight = systemBarTintManager.getConfig.getStatusBarHeight

}
