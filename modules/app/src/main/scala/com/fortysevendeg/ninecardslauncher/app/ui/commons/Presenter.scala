package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.app.commons.BroadcastDispatcher._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, ContextSupportProvider, NineCardsPreferencesValue, ThemeFile}
import com.fortysevendeg.ninecardslauncher.app.di.{Injector, InjectorImpl}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import macroid.ContextWrapper
import rapture.core.Answer

class Presenter(implicit contextWrapper: ContextWrapper)
  extends ContextSupportProvider {

  implicit lazy val di: Injector = new InjectorImpl

  lazy val preferenceValues = new NineCardsPreferencesValue

  def getTheme: NineCardsTheme =
    di.themeProcess.getTheme(ThemeFile.readValue(preferenceValues)).run.run match {
      case Answer(t) => t
      case _ => getDefaultTheme
    }

  def sendBroadCast(broadAction: BroadAction) = {
    val intent = new Intent(broadAction.action)
    intent.putExtra(keyType, commandType)
    broadAction.command foreach (d => intent.putExtra(keyCommand, d))
    contextWrapper.bestAvailable.sendBroadcast(intent)
  }

}
