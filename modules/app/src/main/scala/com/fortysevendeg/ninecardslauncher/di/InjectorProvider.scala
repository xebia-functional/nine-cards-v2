package com.fortysevendeg.ninecardslauncher.di

import android.app.{Activity, Application}
import android.support.v4.app.Fragment

trait InjectorProvider {
  
  def application: Option[Application]

  def di: Option[DependencyInjector] =
    application map {
      case a: DependencyInjector => a
    }
  
}

trait ActivityInjectorProvider extends InjectorProvider {

  self: Activity =>

  def application = Some(getApplication)

}

trait FragmentInjectorProvider extends InjectorProvider {

  self: Fragment =>
  
  def application = Option(getActivity) map (_.getApplication)
  
}
