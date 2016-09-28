package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.graphics.Color
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.ninecardslauncher.utils.SystemBarTintManager
import macroid.{ActivityContextWrapper, Ui}

class SystemBarsTint(implicit activityContextWrapper: ActivityContextWrapper) {

  private[this] lazy val systemBarTintManager = new SystemBarTintManager(activityContextWrapper.getOriginal)

  def initAllSystemBarsTint(): Ui[_] = Ui(Lollipop ifNotSupportedThen {
    systemBarTintManager.setStatusBarTintEnabled(true)
    systemBarTintManager.setNavigationBarTintEnabled(true)
  })

  def initSystemStatusBarTint(): Ui[_] = Ui(Lollipop ifNotSupportedThen {
    systemBarTintManager.setStatusBarTintEnabled(true)
  })

  def initSystemNavigationBarTint(): Ui[_] = Ui(Lollipop ifNotSupportedThen {
    systemBarTintManager.setNavigationBarTintEnabled(true)
  })

  def updateStatusToBlack(): Ui[_] = updateStatusColor(Color.BLACK)

  def updateStatusToTransparent(): Ui[_] = updateStatusColor(Color.TRANSPARENT)

  def updateStatusColor(color: Int): Ui[_] =
    Ui {
      Lollipop ifSupportedThen {
        activityContextWrapper.getOriginal.getWindow.setStatusBarColor(color)
      } getOrElse {
        systemBarTintManager.setStatusBarTintColor(color)
      }
    }

  def updateNavigationToBlack(): Ui[_] = updateNavigationColor(Color.BLACK)

  def updateNavigationToTransparent(): Ui[_] = updateNavigationColor(Color.TRANSPARENT)

  def updateNavigationColor(color: Int): Ui[_] =
    Ui {
      Lollipop ifSupportedThen {
        activityContextWrapper.getOriginal.getWindow.setNavigationBarColor(color)
      } getOrElse {
        systemBarTintManager.setNavigationBarTintColor(color)
      }
    }

  def hasNavigationBar = systemBarTintManager.getConfig.hasNavigationBar

  def getNavigationBarHeight = systemBarTintManager.getConfig.getNavigationBarHeight

  def getStatusBarHeight = systemBarTintManager.getConfig.getStatusBarHeight

}
