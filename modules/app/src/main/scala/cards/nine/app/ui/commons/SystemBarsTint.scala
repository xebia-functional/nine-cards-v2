package cards.nine.app.ui.commons

import android.graphics.Color
import android.view.View
import com.fortysevendeg.macroid.extras.DeviceVersion.{Lollipop, Marshmallow}
import cards.nine.utils.SystemBarTintManager
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

  def lightStatusBar(): Ui[_] =
    Ui(Marshmallow ifSupportedThen
      activityContextWrapper.getOriginal.getWindow.getDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR))

  def defaultStatusBar(): Ui[_] =
    Ui(Marshmallow ifSupportedThen
      activityContextWrapper.getOriginal.getWindow.getDecorView.setSystemUiVisibility(0))

  def hasNavigationBar = systemBarTintManager.getConfig.hasNavigationBar

  def getNavigationBarHeight = systemBarTintManager.getConfig.getNavigationBarHeight

  def getStatusBarHeight = systemBarTintManager.getConfig.getStatusBarHeight

}
